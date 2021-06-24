import argparse

from amadeus import Client, ResponseError
from flask import Flask, request
from multiprocessing import Process, Queue
import socket
import sys
import os
sys.path.append(os.path.relpath("../AgentUtil"))
sys.path.append(os.path.relpath("../Utils"))
from AgentUtil.Logging import config_logger
from rdflib import Namespace, Graph, Literal, XSD, FOAF, RDF, BNode
from flask import Flask, request
from AgentUtil.FlaskServer import shutdown_server
from AgentUtil.ACLMessages import build_message, send_message, get_message_properties
from AgentUtil.OntoNamespaces import ACL, DSO
from AgentUtil.Agent import Agent
from AgentUtil.OWN import OWN
from AgentUtil.Util import gethostname

amadeus = Client(
    client_id='U44xrEbn4gCntvQ4wEAAoWxvSQwGjzDZ',
    client_secret='JHzxXjzI6uT9sYOs'
)

# Definimos los parametros de la linea de comandos
parser = argparse.ArgumentParser()
parser.add_argument('--open', help="Define si el servidor est abierto al exterior o no", action='store_true',
                    default=False)
parser.add_argument('--host', default='localhost', help="Host del agente")
parser.add_argument('--port', type=int, help="Puerto de comunicacion del agente")
parser.add_argument('--acomm', help='Direccion del agente con el que comunicarse')
parser.add_argument('--aport', type=int, help='Puerto del agente con el que comunicarse')
parser.add_argument('--messages', nargs='+', default=[], help="mensajes a enviar")

app = Flask(__name__)

agn = Namespace("http://www.agentes.org#")

# Contador de mensajes
mss_cnt = 0

args = parser.parse_args()
if args.port is None:
    port = 5006
else:
    port = args.port

if args.open:
    hostname = '0.0.0.0'
    hostaddr = gethostname()
else:
    hostaddr = hostname = socket.gethostname()
    
AgentExternHotels = Agent('AgentExternHotels',
                       agn.AgentExternHotels,
                       'http://%s:%d/comm' % (hostname, port),
                       'http://%s:%d/Stop' % (hostname, port))

def get_hotels(ciutat_desti, data_ini, data_fi, preu_max,zona_a):

    global mss_cnt
    gmess=Graph() 
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN)      
    radi = 8
    if(str(zona_a)=="Afueras"):
        radi = 20        
    reg_obj=OWN['Informa_sobre']    
    city = ""
    if str(ciutat_desti)=="Londres":             
        city = "LON"
    if str(ciutat_desti)=="Paris":
        city = "PAR"
        
    try:
        
        hotel_offers = amadeus.shopping.hotel_offers.get(cityCode=city,
                                                     checkInDate=data_ini,
                                                     checkOutDate=data_fi,
                                                     radius= radi,
                                                     radiusUnit ="KM",
                                                     priceRange=str(int(0.75*float(preu_max)))+"-" +str(int(1.25*float(preu_max))),
                                                     currency="EUR")
        for hotel in hotel_offers.data:
            Allotjament=BNode()
            gmess.add((Allotjament, RDF.first, OWN.Allotjament))
            gmess.add((Allotjament, OWN.Preu_Allotjament, Literal(float(hotel['offers'][0]['price']['total']), datatype=XSD.float)))
            gmess.add((Allotjament, OWN.Adreça_allotjament, Literal(hotel['hotel']["address"]["lines"][0], datatype=XSD.string)))
            gmess.add((Allotjament, OWN.Nom_Allotjament, Literal(hotel['hotel']['name'], datatype=XSD.string)))            

    except ResponseError as error:
            print(error)
            
    gr = build_message(gmess,
                        ACL['inform'],
                        sender=AgentExternHotels.uri,
                        content=reg_obj,
                        msgcnt=mss_cnt)
    
    mss_cnt+=1
    return gr

@app.route("/")
def isAlive():
    text = 'Hi i\'m AgentExternHotels o/, if you wanna travel go to <a href= /flights?origin_lat=51.5074&origin_long=0.1278&destination_lat=41.397158&destination_long=2.160873&start_date=2021-06-23&end_date=2021-06-28&price_max=500&num=1>here</a>'
    return text




@app.route("/comm")
def getHotels():
    
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
                           sender=AgentExternHotels.uri,
                           msgcnt=mss_cnt)
    else:
         if msgdic['performative'] != ACL.request:
             # Si no es un request, respondemos que no hemos entendido el mensaje
            gr = build_message(Graph(),
                               ACL['not-understood'],
                               sender=AgentExternHotels.uri,
                               msgcnt=mss_cnt)
         else:
            
            # Extraemos el objeto del contenido que ha de ser una accion de la ontologia
            # de registro            
            content = msgdic['content']          
            # Averiguamos el tipo de la accion
            accion = gm.value(subject=content, predicate=RDF.type)
            # Accion de restriccion
            if accion == OWN.Restriccions_Allotjament:           
                restriccions=gm.value(predicate=RDF.type, object=OWN.Restriccions_Allotjament)
                ciutat_desti=gm.value(subject=restriccions, predicate=OWN.Destí)             
                data_ini=gm.value(subject=restriccions, predicate=OWN.DataInici)                
                data_fi=gm.value(subject=restriccions, predicate=OWN.DataFinal)              
                preu_max=gm.value(subject=restriccions, predicate=OWN.PreuMàxim) 
                zona_a=gm.value(subject=restriccions, predicate=OWN.Zona_Allotjament)                
                gr=get_hotels(ciutat_desti, data_ini, data_fi, preu_max,zona_a)
               
            # No habia ninguna accion en el mensaje
            else:
                gr = build_message(Graph(),
                                   ACL['not-understood'],
                                   sender=AgentExternHotels.uri,
                                   msgcnt=mss_cnt)
    
    mss_cnt+=1 
    
    return gr.serialize(format='xml')


if __name__ == '__main__':
    # parsing de los parametros de la linea de comandos
    args = parser.parse_args()

    # Ponemos en marcha el servidor
    app.run(host=hostname, port=port)
   
