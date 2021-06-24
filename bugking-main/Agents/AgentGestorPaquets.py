from multiprocessing import Process, Queue, Pipe
import socket
import sys
import os
import datetime
import argparse
import logging
import datetime
import time

sys.path.append(os.path.relpath("../AgentUtil"))
sys.path.append(os.path.relpath("../Utils"))

from rdflib import Namespace, Graph, Literal, term, XSD
from flask import Flask, request, redirect, render_template
from AgentUtil.Logging import config_logger
from AgentUtil.FlaskServer import shutdown_server
from AgentUtil.Agent import Agent
from AgentUtil.ACLMessages import build_message, send_message, get_message_properties
from AgentUtil.OntoNamespaces import ACL, DSO, TIO
from rdflib.namespace import FOAF, RDF
from flask_wtf import Form
from AgentUtil.Util import gethostname
from wtforms import Form, BooleanField, TextField, DateField, SelectField, IntegerField, StringField, validators
from wtforms.validators import DataRequired
from AgentUtil.OWN import OWN
__author__ = 'Bugking'

   # Definimos los parametros de la linea de comandos
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
    port = 5002
else:
    port = args.port

if args.open:
    hostname = '0.0.0.0'
    hostaddr = gethostname()
else:
    hostaddr = hostname = socket.gethostname()

if args.dport is None:
    dport = 5003
else:
    dport = args.dport

if args.dhost is None:
    dhostname = socket.gethostname()
else:
    dhostname = args.dhost
    
dport2=5005
dhostname2=socket.gethostname()
dport3=5007
dhostname3=socket.gethostname()
   
app = Flask(__name__, template_folder='templates')


agn = Namespace("http://www.agentes.org#")

  
mss_cnt = 0

AgentGestorPaquets = Agent('AgentGestorPaquets', agn.AgentGestorPaquets,
                        'http://%s:%d/comm' % (hostname, port),
                        'http://%s:%d/Stop' % (hostname, port))

AgentTransport = Agent('AgentTransport', agn.AgentTransport,
                        'http://%s:%d/comm' % (dhostname, dport),
                        'http://%s:%d/Stop' % (dhostname, dport))

AgentAllotjament = Agent('AgentAllotjament', agn.AgentAllotjament,
                        'http://%s:%d/comm' % (dhostname2, dport2),
                        'http://%s:%d/Stop' % (dhostname2, dport2))

AgentActivitats = Agent('AgentActivitat', agn.AgentActivitats,
                        'http://%s:%d/comm' % (dhostname3, dport3),
                        'http://%s:%d/Stop' % (dhostname3, dport3))
    
agn = Namespace("http://www.agentes.org#")

gv= Graph()
ga=Graph()
gc=Graph()
def busquedaVols(ciutatd, preumax, startd, endd, conn1):
    
    
    global mss_cnt
    global gv
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

       
    
    gv = send_message(build_message(gmess,
                        perf=ACL.request,
                        sender=AgentGestorPaquets.uri,
                        receiver=AgentTransport.uri,
                        content=reg_obj,
                        msgcnt=mss_cnt),
                    AgentTransport.address)
    conn1.send(gv)     

def busquedaAllotj(ciutatd, preumax, startd, endd, zonaa, conn2):
    
    
    global mss_cnt
    global ga
    gmess = Graph()
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN)        
    reg_obj=OWN['Restringeix_Allotjament']
    gmess.add((reg_obj, RDF.type, OWN.Restriccions_Allotjament))
    gmess.add((reg_obj, OWN.DataInici, Literal(startd, datatype=XSD.date)))
    gmess.add((reg_obj, OWN.DataFinal, Literal(endd, datatype=XSD.date)))
    gmess.add((reg_obj, OWN.PreuMàxim, Literal(preumax, datatype=XSD.real)))
    gmess.add((reg_obj, OWN.Destí, Literal(ciutatd, datatype=XSD.string)))
    gmess.add((reg_obj, OWN.Zona_Allotjament, Literal(zonaa, datatype=XSD.string)))       
    
    ga = send_message(build_message(gmess,
                        perf=ACL.request,
                        sender=AgentGestorPaquets.uri,
                        receiver=AgentAllotjament.uri,
                        content=reg_obj,
                        msgcnt=mss_cnt),
                    AgentAllotjament.address)  
    conn2.send(ga)
    
def busquedaActv(ciutatd, startd, endd, conn3, activ_ludicas, activ_culturales, activ_festivas):   
    
    global mss_cnt    
    gmess = Graph()
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN)        
    reg_obj=OWN['Restringeix_Activitat']
    gmess.add((reg_obj, RDF.type, OWN.Restriccions_Activitats))
    gmess.add((reg_obj, OWN.DataInici, Literal(startd, datatype=XSD.date)))
    gmess.add((reg_obj, OWN.DataFinal, Literal(endd, datatype=XSD.date)))
    gmess.add((reg_obj, OWN.Destí, Literal(ciutatd, datatype=XSD.string)))  
    gmess.add((reg_obj, OWN.Lúdic, Literal(activ_ludicas, datatype=XSD.string)))
    gmess.add((reg_obj, OWN.Cultural, Literal(activ_culturales, datatype=XSD.string)))
    gmess.add((reg_obj, OWN.Festiu, Literal(activ_festivas, datatype=XSD.string))) 
    
    gc = send_message(build_message(gmess,
                        perf=ACL.request,
                        sender=AgentGestorPaquets.uri,
                        receiver=AgentActivitats.uri,
                        content=reg_obj,
                        msgcnt=mss_cnt),
                    AgentActivitats.address)  
    conn3.send(gc)

    
@app.route("/stop")
def stop():
    """
        Entrypoint que para el agente
        :return:
        """
    tidyup()
    shutdown_server()
    return "Parando Servidor"

@app.route("/comm")
def comunicacion():
    
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
                           sender=AgentGestorPaquets.uri,
                           msgcnt=mss_cnt)
    else:
        # Obtenemos la performativa
        if msgdic['performative'] != ACL.request:
            # Si no es un request, respondemos que no hemos entendido el mensaje
            gr = build_message(Graph(),
                               ACL['not-understood'],
                               sender=AgentGestorPaquets.uri,
                               msgcnt=mss_cnt)
        else:
            # Extraemos el objeto del contenido que ha de ser una accion de la ontologia
            # de registro            
            content = msgdic['content']          
            # Averiguamos el tipo de la accion
            accion = gm.value(subject=content, predicate=RDF.type)
          
            # Accion de busqueda
            if accion == OWN.Demanar_pla_viatge:
                onj=gm.value(predicate=RDF.type, object=OWN.Demanar_pla_viatge)                                  
                desti=gm.value(subject=onj, predicate=OWN.Destí)               
                preu_max=gm.value(subject=onj, predicate=OWN.PreuMàxim)
                preu_m=float(preu_max)
                start_date=gm.value(subject=onj, predicate=OWN.DataInici)
                end_date=gm.value(subject=onj, predicate=OWN.DataFinal)
                zona_allotj=gm.value(subject=onj, predicate=OWN.Zona_Allotjament)
                activ_ludicas = gm.value(subject=onj, predicate=OWN.Lúdic)
                activ_culturales = gm.value(subject=onj, predicate=OWN.Cultural)
                activ_festivas= gm.value(subject=onj, predicate=OWN.Festiu)
                conn1_in, conn1_out = Pipe()
                conn2_in, conn2_out = Pipe()
                conn3_in, conn3_out = Pipe()                
                p1 = Process(target=busquedaVols, args=(desti, preu_m/3, start_date, end_date, conn1_in))
                p2 = Process(target=busquedaAllotj, args=(desti, preu_m/4, start_date, end_date, zona_allotj, conn2_in))
                p3 = Process(target=busquedaActv, args=(desti, start_date, end_date, conn3_in,activ_ludicas, activ_culturales, activ_festivas))
                p1.start()
                p2.start()
                p3.start()
                gv=conn1_out.recv()
                ga=conn2_out.recv()
                gc=conn3_out.recv()
                conn1_in.close()
                conn1_out.close()
                conn2_in.close()
                conn2_out.close()
                conn3_in.close()
                conn3_out.close()
                p1.join()
                p2.join()
                p3.join()
                
                gr = build_message(gv+ga+gc,
                                 ACL['confirm'],
                                 sender=AgentGestorPaquets.uri,
                                 msgcnt=mss_cnt)
            # No habia ninguna accion en el mensaje
            else:
                gr = build_message(Graph(),
                                   ACL['not-understood'],
                                   sender=AgentGestorPaquets.uri,
                                   msgcnt=mss_cnt)
            
      
        mss_cnt+=1 
    
    return gr.serialize(format='xml')   
def tidyup():
    """
    Acciones previas a parar el agente
    """


def agentbehavior1(cola):
    """
    Un comportamiento del agente    port = 9001
    :return:
    """
    # Registramos el agente
    # gr = register_message()


 
if __name__ == '__main__':
     # Ponemos en marcha los behaviors
      #ab1 = Process(target=agentbehavior1, args=(cola1,))
        #ab1.start()
     #cont = message_dialogador();
     # Ponemos en marcha el servidor
        #message_dialogador()
    app.run(host=hostname, port=port)

    logger.info('The End')
            