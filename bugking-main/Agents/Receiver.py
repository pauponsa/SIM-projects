# -*- coding: utf-8 -*-
"""
Created on Mon May 17 11:43:52 2021

@author: aleix
"""

from multiprocessing import Process, Queue
import argparse
import logging

from flask import Flask, request, render_template
from rdflib import Graph, RDF, Namespace, RDFS, Literal
from rdflib.namespace import FOAF

from AgentUtil.ACL import ACL
from AgentUtil.FlaskServer import shutdown_server
from AgentUtil.Agent import Agent
from AgentUtil.ACLMessages import build_message, get_message_properties
from AgentUtil.Logging import config_logger
from AgentUtil.DSO import DSO
from AgentUtil.OWN import OWN
from AgentUtil.Util import gethostname
import socket

__author__ = 'javier'

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
    port = 5000
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
Receiver = Agent('Receiver',
                       agn.Receiver,
                       'http://%s:%d/receiver' % (hostaddr, port),
                       'http://%s:%d/Stop' % (hostaddr, port))
app = Flask(__name__)

mss_cnt = 0



@app.route("/")
def isAlive():
    text = 'Hi i\'m AgentExternVols o/, if you wanna travel go to <a href= /flights?origin_lat=51.5074&origin_long=0.1278&destination_lat=41.397158&destination_long=2.160873&start_date=2021-06-23&end_date=2021-06-28&price_max=500&num=1>here</a>'
    return text


@app.route("/receiver")
def ProvaComunicació():   
    
    global mss_cnt
    message = request.args['content']
    gm = Graph()
    gm.parse(data=message)
    print("PREUMAXIM") 
    direccion = gm.subjects(object=OWN.Demanar_pla_viatge)
    for d in direccion:
        tarjeta = gm.value(subject=d, predicate=OWN.PreuMàxim)
        print(str(tarjeta))
    msgdic = get_message_properties(gm)

    # Comprobamos que sea un mensaje FIPA ACL
    if not msgdic:
        # Si no es, respondemos que no hemos entendido el mensaje
        gr = build_message(Graph(),
                           ACL['not-understood'],
                           sender=Receiver.uri,
                           msgcnt=mss_cnt)
    else:
        # Obtenemos la performativa
        if msgdic['performative'] != ACL.request:
            # Si no es un request, respondemos que no hemos entendido el mensaje
            gr = build_message(Graph(),
                               ACL['not-understood'],
                               sender=Receiver.uri,
                               msgcnt=mss_cnt)
        else:
            # Extraemos el objeto del contenido que ha de ser una accion de la ontologia
            # de registro            
            content = msgdic['content']          
            # Averiguamos el tipo de la accion
            accion = gm.value(subject=content, predicate=RDF.type)
            # Accion de registro
            if accion == OWN.Buscar_activitat:
                print("register")
            # Accion de busqueda
            elif accion == OWN.Demanar_pla_viatge:
                print("search")
               
                gr = build_message(Graph(),
                                   ACL['confirm'],
                                   sender=Receiver.uri,
                                   msgcnt=mss_cnt)
            # No habia ninguna accion en el mensaje
            else:
                gr = build_message(Graph(),
                                   ACL['not-understood'],
                                   sender=Receiver.uri,
                                   msgcnt=mss_cnt)
    
    mss_cnt+=1
    return gr.serialize(format='xml')
 
    
if __name__ == '__main__':
    
    # parsing de los parametros de la linea de comandos

    # Ponemos en marcha el servidor
    app.run(host=hostname, port=port)

    # Esperamos a que acaben los behaviors
    print('The End')