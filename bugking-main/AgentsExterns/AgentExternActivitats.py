import json
from multiprocessing import Process, Queue
import argparse
import logging

import requests
from flask import Flask, request, render_template
from rdflib import Graph, RDF, Namespace, RDFS, Literal, XSD, BNode
from rdflib.namespace import FOAF
from amadeus import Client, ResponseError
from AgentUtil.OntoNamespaces import ACL, DSO, TIO
from AgentUtil.FlaskServer import shutdown_server
from AgentUtil.Agent import Agent
from AgentUtil.ACLMessages import build_message, get_message_properties
from AgentUtil.Logging import config_logger
from AgentUtil.DSO import DSO
from AgentUtil.OWN import OWN
from AgentUtil.Util import gethostname
import socket

amadeus = Client(
    client_id='U44xrEbn4gCntvQ4wEAAoWxvSQwGjzDZ',
    client_secret='JHzxXjzI6uT9sYOs'
)

logger = config_logger(level=1)
# Definimos los parametros de la linea de comandos
parser = argparse.ArgumentParser()
parser.add_argument('--open', help="Define si el servidor est abierto al exterior o no", action='store_true',
                    default=False)
parser.add_argument('--verbose', help="Genera un log de la comunicacion del servidor web", action='store_true',
                    default=False)
parser.add_argument('--port', type=int, help="Puerto de comunicacion del agente")

# Logging
logger = config_logger(level=1)

# parsing de los parametros de la linea de comandos
args = parser.parse_args()

# Configuration stuff
if args.port is None:
    port = 5008
else:
    port = args.port

if args.open:
    hostname = '0.0.0.0'
    hostaddr = gethostname()
else:
    hostaddr = hostname = socket.gethostname()

print('DS Hostname =', hostaddr)

# Directory Service Graph
dsgraph = Graph()

# Vinculamos todos los espacios de nombre a utilizar
dsgraph.bind('acl', ACL)
dsgraph.bind('rdf', RDF)
dsgraph.bind('rdfs', RDFS)
dsgraph.bind('foaf', FOAF)
dsgraph.bind('dso', DSO)

agn = Namespace("http://www.agentes.org#")
AgentExternActivitats = Agent('AgentExternActivitats',
                              agn.AgentExternActivitats,
                              'http://%s:%d/comm' % (hostaddr, port),
                              'http://%s:%d/Stop' % (hostaddr, port))
app = Flask(__name__)

mss_cnt = 0


# BERLIN = "52.52991178975366, 13.423325128546951"
# LONDRES = "51.50694394191481, -0.1702972518658655"
# BARCELONA "41.38781457206419, 2.1381222732350844"

def get_activities_amadeus(ciutat_desti):
    
    destination_lat = 0
    destination_long = 0    
    if str(ciutat_desti) == "Londres":
        destination_lat = 51.50694394191481
        destination_long = -0.1702972518658655
    if str(ciutat_desti)=="Paris":       
        destination_lat = 48.85756 
        destination_long = 2.34280
    
    # de moment nomes busquem londres
    #destination_lat = "51.50694394191481"
    #destination_long = "-0.1702972518658655"
    origin_lat = "41.38781457206419"
    origin_long = "2.1381222732350844"
    people = 1
    global mss_cnt

    gmess = Graph()
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN)
    reg_obj = OWN['Informa_sobre']
    try:        
        activities = amadeus.shopping.activities.get(latitude=float(destination_lat), longitude=float(destination_long))

        for activity in activities.data:
            name = activity['name']

            address = str(ciutat_desti)
            if "geoCode" in activity.keys():
                geo_code = activity["geoCode"]
                geo_url = "https://geocode.xyz/" + str(geo_code['latitude']) + "," + str(geo_code['longitude']) + "?geoit=json"
                response = requests.get(geo_url)
                if response.status_code == 200:
                    data = response.text
                    address_json = json.loads(data)
                    number = ""
                    if address_json["stnumber"]:
                        number = str(address_json["stnumber"])
                    address = address_json["staddress"] + " " + number + ", " + address_json["city"]            
            activitat = BNode()
            gmess.add((activitat, RDF.first, OWN.Activitats))
            gmess.add((activitat, OWN.Nom_Activitat, Literal(name, datatype=XSD.string)))
            gmess.add((activitat, OWN.Lloc_Activitat, Literal(address, datatype=XSD.string)))
            gmess.add((activitat, OWN.Tipus_Activitats, Literal("Ludic", datatype=XSD.string)))
            gmess.add((activitat, RDF.rest, RDF.nil))

    except ResponseError as error:
        print(error)

    gr = build_message(gmess,
                       ACL['inform'],
                       sender=AgentExternActivitats.uri,
                       content=reg_obj,
                       msgcnt=mss_cnt)
    mss_cnt += 1
    return gr


@app.route("/")
def isAlive():
    text = 'Hi i\'m AgentExternVols o/, if you wanna travel go to <a href= /flights?origin_lat=51.5074&origin_long=0.1278&destination_lat=41.397158&destination_long=2.160873&start_date=2021-06-23&end_date=2021-06-28&price_max=500&num=1>here</a>'
    return text


@app.route("/comm")
def get_activities():   

    global mss_cnt
    message = request.args['content']
    gm = Graph()
    gm.parse(data=message)
    msgdic = get_message_properties(gm)

    # Comprobamos que sea un mensaje FIPA ACL
    if not msgdic:        
        # Si no es, respondemos que no hemos entendido el mensaje
        gr = build_message(Graph(),
                           ACL['not-understood'],
                           sender=AgentExternActivitats.uri,
                           msgcnt=mss_cnt)
    else:
        if msgdic['performative'] != ACL.request:            
            # Si no es un request, respondemos que no hemos entendido el mensaje
            gr = build_message(Graph(),
                               ACL['not-understood'],
                               sender=AgentExternActivitats.uri,
                               msgcnt=mss_cnt)
        else:
            
            # Extraemos el objeto del contenido que ha de ser una accion de la ontologia
            # de registro            
            content = msgdic['content']
            # Averiguamos el tipo de la accion
            accion = gm.value(subject=content, predicate=RDF.type)
            # Accion de restriccion
            if accion == OWN.Restriccions_Activitats:
                
                restriccions = gm.value(predicate=RDF.type, object=OWN.Restriccions_Activitats)
                ciutat_desti = gm.value(subject=restriccions, predicate=OWN.Destí)                                
                gr = get_activities_amadeus(ciutat_desti)

            # No habia ninguna accion en el mensaje
            else:
                
                gr = build_message(Graph(),
                                   ACL['not-understood'],
                                   sender=AgentExternActivitats.uri,
                                   msgcnt=mss_cnt)

    mss_cnt += 1

    return gr.serialize(format='xml')


if __name__ == '__main__':
    # parsing de los parametros de la linea de comandos
    args = parser.parse_args()

    # Ponemos en marcha el servidor
    app.run(host=hostname, port=port)

    # logger.info('The End')
