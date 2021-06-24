from multiprocessing import Process, Queue
import argparse
import logging

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
    port = 5004
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
AgentExternVols = Agent('AgentExternVols',
                       agn.AgentExternVols,
                       'http://%s:%d/comm' % (hostaddr, port),
                       'http://%s:%d/Stop' % (hostaddr, port))
app = Flask(__name__)

mss_cnt = 0
#BERLIN = "52.52991178975366, 13.423325128546951"
#LONDRES = "51.50694394191481, -0.1702972518658655"
#BARCELONA "41.38781457206419, 2.1381222732350844"

def donam_aeroport(codi):
    
    if codi == "STN":
        return "London Stansted Airport"
    elif codi == "LHR":
        return "London Heathrow Airport"
    elif codi == "LCY":
        return "London City Airport"
    elif codi == "LGW":
        return "London Gatwick Airport"
    elif codi == "CDG":
        return "Charles de Gaulle Airport"
    elif codi == "ORY":
        return "Orly Airport"
    elif codi == "BVA":
        return "Beauvais-Tillé Airport"
    elif codi == "LBG":
        return "Paris–Le Bourget Airport"

def obtenir_vols(ciutat_desti, data_ini, data_fi, preu_max):
    """
    if(ciutat_desti == "londres"):
        destination_lat = "51.50694394191481"
        destination_long = "-0.1702972518658655"
    else:
        destination_lat = "52.52991178975366"
        destination_long = "13.423325128546951"
    """
    #de moment nomes busquem londres
    destination_lat = "51.50694394191481"
    destination_long = "-0.1702972518658655"
    origin_lat = "41.38781457206419"
    origin_long = "2.1381222732350844"
    start_date = str(data_ini)
    end_date = str(data_fi)
    price_max = preu_max
    people = 1
    global mss_cnt
    cont = 0
    cont2 = 0
    c=0
    gmess=Graph() 
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN) 
    llista=[]    
    if str(ciutat_desti)=="Londres":             
        llista = llista + ["STN","LHR","LCY", "LGW"]   
    if str(ciutat_desti)=="Paris":
        llista= llista + ["CDG", "ORY", "BVA", "LBG"]  
    
    #origin = amadeus.reference_data.locations.airports.get(longitude=origin_long, latitude=origin_lat)
    #destination_airports = amadeus.reference_data.locations.airports.get(longitude=destination_long,
                                                                         #latitude=destination_lat)
    #origin_iata = origin.data[0]['iataCode']

    #for destination in destination_airports.data:
     #   destinations_iata = destination['iataCode']

        #try:
    reg_obj=OWN['Informa_sobre']
    for l in llista:
        try:            
            departure_response = amadeus.shopping.flight_offers_search.get(
                originLocationCode="BCN",
                destinationLocationCode=l,
                departureDate=start_date,
                adults=people)
        
            return_response = amadeus.shopping.flight_offers_search.get(
                originLocationCode=l,
                destinationLocationCode="BCN",
                departureDate=end_date,
                adults=people)
            
            for outbound_flight in departure_response.data:
                for inbound_flight in return_response.data:
                     preu_anada = float(outbound_flight['price']['total'])
                     preu_tornada = float(inbound_flight['price']['total'])
                     preu_total = preu_anada + preu_tornada
                     
                     if preu_total <= float(price_max):                         
                        
                        reg_obj=OWN['Informa_sobre']
                        hora_sortida = outbound_flight['itineraries'][0]['segments'][0]['departure']['at']
                        hora_arribada = outbound_flight['itineraries'][0]['segments'][0]['arrival']['at']
                        hora_sortidat = inbound_flight['itineraries'][0]['segments'][0]['departure']['at']
                        hora_arribadat = inbound_flight['itineraries'][0]['segments'][0]['arrival']['at']  
                        
                       
                        Aeroport_extern = donam_aeroport(l)
                        Transport=BNode()
                        gmess.add((Transport, RDF.first, OWN.Transport))                        
                        gmess.add((Transport, OWN.Preu_Transport, Literal(float(outbound_flight['price']['total']) +float(inbound_flight['price']['total']), datatype=XSD.float)))                                             
                        gmess.add((Transport, OWN.AeroportSortidaAnada, Literal("El Prat Airport", datatype=XSD.string)))
                        gmess.add((Transport, OWN.AeroportArribadaAnada, Literal(Aeroport_extern, datatype=XSD.string)))
                        gmess.add((Transport, OWN.HoraSortidaAnada, Literal(hora_sortida, datatype=XSD.dateTime)))
                        gmess.add((Transport, OWN.HoraArribadaAnada, Literal(hora_arribada, datatype=XSD.dateTime)))                       
                        gmess.add((Transport, OWN.AeroportSortidaTornada, Literal(Aeroport_extern, datatype=XSD.string)))
                        gmess.add((Transport, OWN.AeroportArribadaTornada, Literal("El Prat Airport", datatype=XSD.string)))
                        gmess.add((Transport, OWN.HoraSortidaTornada, Literal(hora_sortidat, datatype=XSD.dateTime)))
                        gmess.add((Transport, OWN.HoraArribadaTornada, Literal(hora_arribadat, datatype=XSD.dateTime)))                                               
                        ++c
                        
                     if cont2 == 5:
                        break
                     else:
                        cont2 = cont2 + 1
                if cont == 0:
                    break
                else:
                    cont = cont + 1
       
        except ResponseError as error:
           print(error)
        
    gr = build_message(gmess,
                        ACL['inform'],
                        sender=AgentExternVols.uri,
                        content=reg_obj,
                        msgcnt=mss_cnt)
    
    mss_cnt+=1
    return gr

@app.route("/")
def isAlive():
    text = 'Hi i\'m AgentExternVols o/, if you wanna travel go to <a href= /flights?origin_lat=51.5074&origin_long=0.1278&destination_lat=41.397158&destination_long=2.160873&start_date=2021-06-23&end_date=2021-06-28&price_max=500&num=1>here</a>'
    return text


@app.route("/comm")
def getFlights():
    
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
                           sender=AgentExternVols.uri,
                           msgcnt=mss_cnt)
    else:
        if msgdic['performative'] != ACL.request:          
            # Si no es un request, respondemos que no hemos entendido el mensaje
            gr = build_message(Graph(),
                               ACL['not-understood'],
                               sender=AgentExternVols.uri,
                               msgcnt=mss_cnt)
        else:
            
            # Extraemos el objeto del contenido que ha de ser una accion de la ontologia
            # de registro            
            content = msgdic['content']          
            # Averiguamos el tipo de la accion
            accion = gm.value(subject=content, predicate=RDF.type)
            # Accion de restriccion
            if accion == OWN.Restriccions_Transport:           
                restriccions=gm.value(predicate=RDF.type, object=OWN.Restriccions_Transport)
                ciutat_desti=gm.value(subject=restriccions, predicate=OWN.Destí)             
                data_ini=gm.value(subject=restriccions, predicate=OWN.DataInici)                
                data_fi=gm.value(subject=restriccions, predicate=OWN.DataFinal)              
                preu_max=gm.value(subject=restriccions, predicate=OWN.PreuMàxim)
                gr=obtenir_vols(ciutat_desti, data_ini, data_fi, preu_max)
               
            # No habia ninguna accion en el mensaje
            else:
                gr = build_message(Graph(),
                                   ACL['not-understood'],
                                   sender=AgentExternVols.uri,
                                   msgcnt=mss_cnt)
    
    mss_cnt+=1 
    
    return gr.serialize(format='xml')

if __name__ == '__main__':
    # parsing de los parametros de la linea de comandos
    args = parser.parse_args()

    # Ponemos en marcha el servidor
    app.run(host='0.0.0.0', port=port)

    #logger.info('The End')
