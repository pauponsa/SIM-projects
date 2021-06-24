# -*- coding: utf-8 -*-

from multiprocessing import Process, Queue
import socket
import sys
import os
import argparse
sys.path.append(os.path.relpath("../AgentUtil"))
sys.path.append(os.path.relpath("../Utils"))
from AgentUtil.Logging import config_logger
from rdflib import Namespace, Graph, Literal, XSD, FOAF, RDF
from flask import Flask, request
from AgentUtil.FlaskServer import shutdown_server
from AgentUtil.ACLMessages import build_message, send_message, get_message_properties
from AgentUtil.OntoNamespaces import ACL, DSO
from AgentUtil.Agent import Agent
from AgentUtil.OWN import OWN
from AgentUtil.Util import gethostname


parser = argparse.ArgumentParser()
parser.add_argument('--open', help="Define si el servidor esta abierto al exterior o no", action='store_true',
default=False)
parser.add_argument('--host', default='localhost', help="Host del agente")
parser.add_argument('--port', type=int, help="Puerto de comunicacion del agente")
parser.add_argument('--acomm', help='Direccion del agente con el que comunicarse')
parser.add_argument('--dhost', help="Host del agente de directorio")
parser.add_argument('--dport', type=int, help='Puerto del agente con el que comunicarse')
    
    # Logging
logger = config_logger(level=1)

    # parsing de los parametros de la linea de comandos
args = parser.parse_args()

if args.port is None:
    port = 5003
else:
    port = args.port

if args.open:
    hostname = '0.0.0.0'
    hostaddr = gethostname()
else:
    hostaddr = hostname = socket.gethostname()

if args.dport is None:
    dport = 5004
else:
    dport = args.dport

if args.dhost is None:
    dhostname = socket.gethostname()
else:
    dhostname = args.dhost

agn = Namespace("http://www.agentes.org#")

# Contador de mensajes
mss_cnt = 0

# Datos del Agente

AgentTransport = Agent('AgentTransport',
                       agn.AgentTransport,
                       'http://%s:%d/comm' % (hostname, port),
                       'http://%s:%d/Stop' % (hostname, port))

AgentExternVols = Agent('AgentExternVols',
                       agn.AgentExternVols,
                       'http://%s:%d/comm' % (dhostname, dport),
                       'http://%s:%d/Stop' % (dhostname, dport))


# Global triplestore graph
dsgraph = Graph()

cola1 = Queue()

# Flask stuff
app = Flask(__name__)


def busquedaVols(ciutatd, preumax, startd, endd):
    
    global mss_cnt
    gmess = Graph()
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN)        
    reg_obj=OWN['Restringeix_Transport']
    gmess.add((reg_obj, RDF.type, OWN.Restriccions_Transport))
    gmess.add((reg_obj, OWN.DataInici, Literal(startd, datatype=XSD.date)))
    gmess.add((reg_obj, OWN.DataFinal, Literal(endd, datatype=XSD.date)))
    gmess.add((reg_obj, OWN.PreuMàxim, Literal(preumax, datatype=XSD.real)))
    gmess.add((reg_obj, OWN.Destí, Literal(ciutatd, datatype=XSD.string)))
       
    
    gr = send_message(build_message(gmess,
                        perf=ACL.request,
                        sender=AgentTransport.uri,
                        receiver=AgentExternVols.uri,
                        content=reg_obj,
                        msgcnt=mss_cnt),
                    AgentExternVols.address)
    
    preu_min=float("inf")
    preu_tmp= 0
    hora_s_a = ""
    hora_a_a_anada=""
    origen_anada=""
    desti_anada=""
    hora_s_t=""
    hora_a_t=""
    origen_t=""
    desti_t=""
    for t in gr.subjects(RDF.first, OWN.Transport):
        preu_possible=gr.value(subject=t, predicate=OWN.Preu_Transport)
        if float(preu_possible)<float(preu_min):            
            preu_tmp=gr.value(subject=t, predicate=OWN.Preu_Transport)
            hora_s_a=gr.value(subject=t, predicate=OWN.HoraSortidaAnada)
            hora_a_a_anada=gr.value(subject=t, predicate=OWN.HoraArribadaAnada)
            origen_anada=gr.value(subject=t, predicate=OWN.AeroportSortidaAnada)
            desti_anada=gr.value(subject=t, predicate=OWN.AeroportArribadaAnada)  
            #terminal_anada=gr.value(subject=t, predicate=OWN.TerminalAnada)
            hora_s_t=gr.value(subject=t, predicate=OWN.HoraSortidaTornada)
            hora_a_t=gr.value(subject=t, predicate=OWN.HoraArribadaTornada)
            origen_t=gr.value(subject=t, predicate=OWN.AeroportSortidaTornada)
            desti_t=gr.value(subject=t, predicate=OWN.AeroportArribadaTornada)  
           # terminal_t=gr.value(subject=t, predicate=OWN.TerminalTornada)
    gc = Graph()
    gc.bind('foaf', FOAF)
    gc.bind('dso', DSO)
    gc.bind('own', OWN)        
    reg_obj=OWN['Retorna']
    gc.add((reg_obj,RDF.type,OWN.Transport))
    gc.add((reg_obj, OWN.Preu_Transport, Literal(preu_tmp, datatype=XSD.float)))                                             
    #gc.add((reg_obj, OWN.TerminalAnada, Literal(terminal_anada, datatype=XSD.integer)))
    gc.add((reg_obj, OWN.AeroportSortidaAnada, Literal(origen_anada, datatype=XSD.string)))
    gc.add((reg_obj, OWN.AeroportArribadaAnada, Literal(desti_anada, datatype=XSD.string)))
    gc.add((reg_obj, OWN.HoraSortidaAnada, Literal(hora_s_a, datatype=XSD.dateTime)))
    gc.add((reg_obj, OWN.HoraArribadaAnada, Literal(hora_a_a_anada, datatype=XSD.dateTime)))   
    #gc.add((reg_obj, OWN.TerminalTornada, Literal(terminal_t, datatype=XSD.integer)))
    gc.add((reg_obj, OWN.AeroportSortidaTornada, Literal(origen_t, datatype=XSD.string)))
    gc.add((reg_obj, OWN.AeroportArribadaTornada, Literal(desti_t, datatype=XSD.string)))
    gc.add((reg_obj, OWN.HoraSortidaTornada, Literal(hora_s_t, datatype=XSD.dateTime)))
    gc.add((reg_obj, OWN.HoraArribadaTornada, Literal(hora_a_t, datatype=XSD.dateTime)))
    

    return gc

@app.route("/comm")
def comunicacion():
    """
    Entrypoint de comunicacion
    """
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
                           sender=AgentTransport.uri,
                           msgcnt=mss_cnt)
    else:
        # Obtenemos la performativa
        if msgdic['performative'] != ACL.request:
            # Si no es un request, respondemos que no hemos entendido el mensaje       
            gr = build_message(Graph(),
                               ACL['not-understood'],
                               sender=AgentTransport.uri,
                               msgcnt=mss_cnt)
        else:
            # Extraemos el objeto del contenido que ha de ser una accion de la ontologia
            # de registro            
            content = msgdic['content']          
            # Averiguamos el tipo de la accion
            accion = gm.value(subject=content, predicate=RDF.type)
          
            # Accion de busqueda
            if accion == OWN.Restriccions_Transport:
                onj=gm.value(predicate=RDF.type, object=OWN.Restriccions_Transport)                              
                desti=gm.value(subject=onj, predicate=OWN.Destí)               
                preu_max=gm.value(subject=onj, predicate=OWN.PreuMàxim)
                start_date=gm.value(subject=onj, predicate=OWN.DataInici)
                end_date=gm.value(subject=onj, predicate=OWN.DataFinal)             
                graph_vols=Graph()
                graph_vols=busquedaVols(desti, preu_max, start_date, end_date)
                
                gr = build_message(graph_vols,
                                 ACL['confirm'],
                                 sender=AgentTransport.uri,
                                 msgcnt=mss_cnt)
            # No habia ninguna accion en el mensaje
            else:
                gr = build_message(Graph(),
                                   ACL['not-understood'],
                                   sender=AgentTransport.uri,
                                   msgcnt=mss_cnt)
        mss_cnt+=1 
    
    return gr.serialize(format='xml')    
    
   


@app.route("/Stop")
def stop():
    """
    Entrypoint que para el agente

    :return:
    """
    tidyup()
    shutdown_server()
    return "Parando Servidor"


def tidyup():
    """
    Acciones previas a parar el agente

    """
    pass


def agentbehavior1(cola):
    """
    Un comportamiento del agente

    :return:
    """
    pass


if __name__ == '__main__':
    # Ponemos en marcha los behaviors
    ab1 = Process(target=agentbehavior1, args=(cola1,))
    ab1.start()

    # Ponemos en marcha el servidor
    app.run(host=hostname, port=port)

    # Esperamos a que acaben los behaviors
    ab1.join()   
