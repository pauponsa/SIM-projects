# -*- coding: utf-8 -*-
"""
Created on Mon May 17 12:21:32 2021

@author: aleix

from multiprocessing import Process, Queue
import logging
import argparse
import Agents.Receiver
from flask import Flask, request
from rdflib import Graph, Namespace, Literal, XSD
from rdflib.namespace import FOAF, RDF

from AgentUtil.ACL import ACL
from AgentUtil.FlaskServer import shutdown_server
from AgentUtil.ACLMessages import build_message, send_message, get_message_properties
from AgentUtil.Agent import Agent
from AgentUtil.Logging import config_logger
from AgentUtil.DSO import DSO
from AgentUtil.Util import gethostname
from AgentUtil.OWN import OWN
import socket



parser = argparse.ArgumentParser()
parser.add_argument('--open', help="Define si el servidor esta abierto al exterior o no", action='store_true',
                    default=False)
parser.add_argument('--verbose', help="Genera un log de la comunicacion del servidor web", action='store_true',
                        default=False)
parser.add_argument('--port', type=int, help="Puerto de comunicacion del agente")
parser.add_argument('--dhost', help="Host del agente de directorio")
parser.add_argument('--dport', type=int, help="Puerto de comunicacion del agente de directorio")

logger = config_logger(level=1)

# parsing de los parametros de la linea de comandos
args = parser.parse_args()

# Configuration stuff
if args.port is None:
    port = 5001
else:
    port = args.port

if args.open:
    hostname = '0.0.0.0'
    hostaddr = gethostname()
else:
    hostaddr = hostname = '0.0.0.0'

print('DS Hostname =', hostaddr)
if args.dport is None:
    dport = 5000
else:
    dport = args.dport

if args.dhost is None:
    dhostname = socket.gethostname()
else:
    dhostname = args.dhost

print('DS DHostname =', dhostname)
# Flask stuff
app = Flask(__name__)

agn = Namespace("http://www.agentes.org#")

Main= Agent('Main',
                  agn.Main,
                  'http://%s:%d/main' % (hostaddr, port),
                  'http://%s:%d/Stop' % (hostaddr, port))

# Directory agent address
Receiver = Agent('Receiver',
                       agn.Receiver,
                       'http://%s:%d/receiver' % (dhostname, dport),
                       'http://%s:%d/Stop' % (dhostname, dport))

# Global dsgraph triplestore
dsgraph = Graph()

# Cola de comunicacion entre procesos
mss_cnt=0
@app.route("/main")
def ProvaComunicacio():
    
    
    global mss_cnt
    
    
    gmess = Graph()
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN)
        
    reg_obj=OWN['Demana']
    gmess.add((reg_obj, RDF.type, OWN.Demanar_pla_viatge))
    gmess.add((reg_obj, OWN.PreuMàxim, Literal(580, datatype=XSD.float)))
    reg_obj2=OWN['Busca']
    gmess.add((reg_obj2, RDF.type, OWN.Demanar_pla_viatge))
    gmess.add((reg_obj2, OWN.PreuMàxim, Literal(600, datatype=XSD.float)))
    gr =send_message(
        build_message(gmess,
                       perf=ACL.request,
                       sender=Main.uri,
                       receiver=Receiver.uri,
                       content=reg_obj,
                       msgcnt=mss_cnt),
                    Receiver.address)
        

    

    mss_cnt+=1
    
    return gr.serialize(format='xml')



if __name__ == '__main__':
    
    # parsing de los parametros de la linea de comandos

    # Ponemos en marcha el servidor
    app.run(host=hostname, port=port)

    # Esperamos a que acaben los behaviors
    print('The End')
"""