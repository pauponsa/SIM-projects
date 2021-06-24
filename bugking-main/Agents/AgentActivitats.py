from multiprocessing import Process, Queue, Pipe
import socket
import sys
from dateutil.parser import parse
import os
import datetime
import argparse
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
    port = 5007
else:
    port = args.port

if args.open:
    hostname = '0.0.0.0'
    hostaddr = gethostname()
else:
    hostaddr = hostname = socket.gethostname()

if args.dport is None:
    dport = 5008
else:
    dport = args.dport

if args.dhost is None:
    dhostname = socket.gethostname()
else:
    dhostname = args.dhost
    
dport3=5020
dhostname3=socket.gethostname()
dport4=5021
dhostname4=socket.gethostname()

agn = Namespace("http://www.agentes.org#")

# Contador de mensajes
mss_cnt = 0

# Datos del Agente

AgentActivitats = Agent('AgentActivitats',
                       agn.AgentActivitats,
                       'http://%s:%d/comm' % (hostname, port),
                       'http://%s:%d/Stop' % (hostname, port))

AgentExternActivitats = Agent('AgentExternActivitats',
                       agn.AgentExternAgentActivitats,
                       'http://%s:%d/comm' % (dhostname, dport),
                       'http://%s:%d/Stop' % (dhostname, dport))

AgentExternActivitatsCulturals = Agent('AgentExternActivitatsCulturals',
                       agn.AgentExternActivitatsCulturals,
                       'http://%s:%d/comm' % (dhostname3, dport3),
                       'http://%s:%d/Stop' % (dhostname3, dport3))
AgentExternActivitatsAttraction = Agent('AgentExternActivitatsAttraction',
                       agn.AgentExternActivitatsAttraction,
                       'http://%s:%d/comm' % (dhostname4, dport4),
                       'http://%s:%d/Stop' % (dhostname4, dport4))

# Global triplestore graph


# Flask stuff
app = Flask(__name__)

numf = 0
numl = 0
numc = 0

def num_activitats(festiun, ludicn, culturaln, activitats):
    
    global numf
    global numl
    global numc    
    festiu=str(festiun)
    ludic=str(ludicn)
    cultural=str(culturaln)    
    if(festiu=="Mucho"):                                             
        if(ludic=="Normal"):
            if(cultural=="Algo"):
                numf=int(activitats*0.45)
                numl = int(activitats*0.45)  
            elif(cultural=="Nada"):
                numf=int(activitats*0.60)
                numl = int(activitats*0.40)             
        elif(ludic=="Algo"):
            if(cultural=="Normal"):
                numf=int(activitats*0.45) 
                numl = int(activitats*0.25)  
            elif(cultural=="Nada"):
                numf=int(activitats*0.70)
                numl = int(activitats*0.30)         
        elif(ludic=="Nada"):
            if(cultural=="Normal"):
                numf=int(activitats*0.60) 
                numl = int(activitats*0) 
            elif(cultural=="Algo"):
                numf=int(activitats*0.75)
                numl =int( activitats*0)
        numc = int(activitats) -(numl+numf)
        
    elif(ludic=="Mucho"):                                             
        if(festiu=="Normal"):
            if(cultural=="Algo"):
                numl=int(activitats*0.45)
                numf =int( activitats*0.45)
            elif(cultural=="Nada"):
                numl=int(activitats*0.60)
                numf =int( activitats*0.40)             
        elif(festiu=="Algo"):
            if(cultural=="Normal"):
                numl=int(activitats*0.45)
                numf = int(activitats*0.25) 
            elif(cultural=="Nada"):
                numl=int(activitats*0.70)
                numf =int( activitats*0.30)         
        elif(festiu=="Nada"):
            if(cultural=="Normal"):
                numl=int(activitats*0.60)
                numf = int(activitats*0)
            elif(cultural=="Algo"):
                numl=int(activitats*0.75)
                numf = int(activitats*0.0)
        numc = int(activitats  )-(numf+numl)
        
    elif(cultural=="Mucho"):                                             
        if(festiu=="Normal"):
            if(ludic=="Algo"):
                numc=int(activitats*0.45)
                numf = int(activitats*0.45) 
            elif(ludic=="Nada"):
                numc=int(activitats*0.60)
                numf = int(activitats*0.40)           
        elif(festiu=="Algo"):
            if(ludic=="Normal"):
                numc=int(activitats*0.45) 
                numf = int(activitats*0.25) 
            elif(ludic=="Nada"):
                numc=int(activitats*0.70)
                numf = int(activitats*0.30)        
        elif(festiu=="Nada"):
            if(ludic=="Normal"):
                numc=int(activitats*0.60) 
                numf = int(activitats*0) 
            elif(ludic=="Algo"):
                numc=int(activitats*0.75)
                numf = int(activitats*0.0)
        numl = int(activitats) -(numf+numc)
    elif(cultural=="Normal"):                                             
        if(festiu=="Algo"):            
            if(ludic=="Nada"):
                numc=int(activitats*0.60)
                numf = int(activitats*0.40)             
        elif(festiu=="Nada"):
            if(ludic=="Algo"):
                numc=int(activitats*0.75)
                numf = int(activitats*0.0)
        numl = int(activitats) -(numf+numc)
    elif(ludic=="Normal"):                                             
        if(festiu=="Algo"):            
            if(cultural=="Nada"):
                numl=int(activitats*0.60)
                numf = int(activitats*0.40)             
        elif(festiu=="Nada"):
            if(cultural=="Algo"):
                numl=int(activitats*0.75)
                numf = int(activitats*0.0)
        numc = int(activitats) -(numf+numl)
    elif(festiu=="Normal"):                                             
        if(cultural=="Algo"):            
            if(ludic=="Nada"):
                numf=int(activitats*0.60)
                numc = int(activitats*0.40)             
        elif(cultural=="Nada"):
            if(ludic=="Algo"):
                numf=int(activitats*0.75)
                numc = int(activitats*0.0)
        numl = int(activitats) -(numf+numc)
        
    if numf==1:
        numf -=1
        numc+=1
    if numc==1:
        numc -=1
        numf+=1
    if numl==1:
        numl-=1
        numc+=1    
    
def busquedaActivLudiques(ciutatd, conn1,numl):
    
    global mss_cnt
    gmess = Graph()
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN)        
    reg_obj=OWN['Restringeix_Activitat']
    gmess.add((reg_obj, RDF.type, OWN.Restriccions_Activitats))       
    gmess.add((reg_obj, OWN.Destí, Literal(ciutatd, datatype=XSD.string)))    
    
    if(numl>0):        
        gr = send_message(build_message(gmess,
                            perf=ACL.request,
                            sender=AgentActivitats.uri,
                            receiver=AgentExternActivitats.uri,
                            content=reg_obj,
                            msgcnt=mss_cnt),
                        AgentExternActivitats.address)              
    if numl>0:
        conn1.send(gr)
    else:
        graph_buit = Graph()
        conn1.send(graph_buit)
    
    
def busquedaActivCulturals(ciutatd, conn2, numc):
    gmess2 = Graph()
    gmess2.bind('foaf', FOAF)
    gmess2.bind('dso', DSO)
    gmess2.bind('own', OWN)        
    reg_obj=OWN['Restringeix_Activitat']
    gmess2.add((reg_obj, RDF.type, OWN.Restriccions_Activitats))   
    if(str(ciutatd)=="Londres"):
        gmess2.add((reg_obj, OWN.Destí, Literal("London", datatype=XSD.string)))
    elif(str(ciutatd)=="Paris"):
        gmess2.add((reg_obj, OWN.Destí, Literal("Paris", datatype=XSD.string)))
    
    if(numc>0):
        gl = send_message(build_message(gmess2,
                                perf=ACL.request,
                                sender=AgentActivitats.uri,
                                receiver=AgentExternActivitatsCulturals.uri,
                                content=reg_obj,
                                msgcnt=mss_cnt),
                            AgentExternActivitatsCulturals.address)
    
    if numc>0:
        conn2.send(gl)
    else:
        graph_buit = Graph()
        conn2.send(graph_buit)
    
def busquedaActivAttraction(ciutatd, conn3):
    gmess2 = Graph()
    gmess2.bind('foaf', FOAF)
    gmess2.bind('dso', DSO)
    gmess2.bind('own', OWN)        
    reg_obj=OWN['Restringeix_Activitat']
    gmess2.add((reg_obj, RDF.type, OWN.Restriccions_Activitats))   
    if(str(ciutatd)=="Londres"):
        gmess2.add((reg_obj, OWN.Destí, Literal("London", datatype=XSD.string)))
    elif(str(ciutatd)=="Paris"):
        gmess2.add((reg_obj, OWN.Destí, Literal("Paris", datatype=XSD.string)))
    
        
    gl = send_message(build_message(gmess2,
                            perf=ACL.request,
                            sender=AgentActivitats.uri,
                            receiver=AgentExternActivitatsAttraction.uri,
                            content=reg_obj,
                            msgcnt=mss_cnt),
                        AgentExternActivitatsAttraction.address)
    
    conn3.send(gl)

def busquedaActv(ciutatd, startd, endd, activ_ludicas, activ_culturales, activ_festivas):
    
    ds = parse(startd)
    de = parse(endd)
    dies = de-ds
    days = dies.days
    #dies = (diaend.day-diastart.day)
    days += 1
    activ = int(days)*3
    activ = float(activ)    
    num_activitats(activ_festivas, activ_ludicas, activ_culturales, activ)
    global numf    
    global numc
    global numl    
    conn1_in, conn1_out = Pipe()
    conn2_in, conn2_out = Pipe()
    conn3_in, conn3_out = Pipe()                
    p1 = Process(target=busquedaActivLudiques, args=(ciutatd, conn1_in, numl))
    p2 = Process(target=busquedaActivCulturals, args=(ciutatd, conn2_in, numc))
    p3 = Process(target=busquedaActivAttraction, args=(ciutatd,conn3_in))
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
    gdef = Graph()
    gdef = gv + ga  + gc    
    activitats_mati = dies.days +1 
    activitats_tarda = dies.days +1
    activitats_nit = dies.days +1
    gmess = Graph()
    gmess.bind('foaf', FOAF)
    gmess.bind('dso', DSO)
    gmess.bind('own', OWN)
    for a in gdef.subjects(RDF.first, OWN.Activitats):
            activitat = BNode()
            Nom = gdef.value(subject=a, predicate=OWN.Nom_Activitat)   
            Tipus = gdef.value(subject=a, predicate=OWN.Tipus_Activitats)  
            Adresa = gdef.value(subject=a, predicate=OWN.Lloc_Activitat)  
            if(str(Tipus) == "Festiu" and numf>0):
                numf -= 1
                gmess.add((activitat, RDF.first, OWN.Activitats))
                gmess.add((activitat, OWN.Nom_Activitat, Literal(Nom, datatype=XSD.string)))
                gmess.add((activitat, OWN.Lloc_Activitat, Literal(Adresa, datatype=XSD.string)))
                gmess.add((activitat, OWN.Tipus_Activitats, Literal("Festivo", datatype=XSD.string)))
                gmess.add((activitat, RDF.rest, RDF.nil)) 
                if(activitats_nit>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Noche", datatype=XSD.string)))                                      
                    activitats_nit-=1
                elif(activitats_tarda>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Tarde", datatype=XSD.string)))                                      
                    activitats_tarda -= 1
                elif(activitats_mati>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Mañana", datatype=XSD.string)))                                      
                    activitats_mati-=1
            if(str(Tipus) == "Cultural" and numc>0):
                numc -= 1
                gmess.add((activitat, RDF.first, OWN.Activitats))
                gmess.add((activitat, OWN.Nom_Activitat, Literal(Nom, datatype=XSD.string)))
                gmess.add((activitat, OWN.Lloc_Activitat, Literal(Adresa, datatype=XSD.string)))
                gmess.add((activitat, OWN.Tipus_Activitats, Literal("Cultural", datatype=XSD.string)))
                gmess.add((activitat, RDF.rest, RDF.nil)) 
                if(activitats_mati>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Mañana", datatype=XSD.string)))                                      
                    activitats_mati-=1
                elif(activitats_tarda>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Tarde", datatype=XSD.string)))                                      
                    activitats_tarda -= 1
                elif(activitats_nit>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Noche", datatype=XSD.string)))                                      
                    activitats_nit-=1             
            if(str(Tipus) == "Ludic" and numl>0):
                numl -= 1
                gmess.add((activitat, RDF.first, OWN.Activitats))
                gmess.add((activitat, OWN.Nom_Activitat, Literal(Nom, datatype=XSD.string)))
                gmess.add((activitat, OWN.Lloc_Activitat, Literal(Adresa, datatype=XSD.string)))
                gmess.add((activitat, OWN.Tipus_Activitats, Literal("Lúdico", datatype=XSD.string)))
                gmess.add((activitat, RDF.rest, RDF.nil)) 
                if(activitats_tarda>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Tarde", datatype=XSD.string)))                                      
                    activitats_tarda -= 1
                elif(activitats_mati>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Mañana", datatype=XSD.string)))                                      
                    activitats_mati-=1
                elif(activitats_nit>0): 
                    gmess.add((activitat, OWN.Franja_activitat, Literal("Noche", datatype=XSD.string)))                                      
                    activitats_nit-=1
            if(numl==0 and numc==0 and numf==0):
                break
    return gmess

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
        # Si no es, Nadapondemos que no hemos entendido el mensaje
        gr = build_message(Graph(),
                           ACL['not-understood'],
                           sender=AgentActivitats.uri,
                           msgcnt=mss_cnt)
    else:
        # Obtenemos la performativa
        if msgdic['performative'] != ACL.request:
            # Si no es un request, Nadapondemos que no hemos entendido el mensaje       
            gr = build_message(Graph(),
                               ACL['not-understood'],
                               sender=AgentActivitats.uri,
                               msgcnt=mss_cnt)
        else:
            # Extraemos el objeto del contenido que ha de ser una accion de la ontologia
            # de registro            
            content = msgdic['content']          
            # Averiguamos el tipo de la accion
            accion = gm.value(subject=content, predicate=RDF.type)
          
            # Accion de busqueda
            if accion == OWN.Restriccions_Activitats:
                onj=gm.value(predicate=RDF.type, object=OWN.Restriccions_Activitats)                              
                desti=gm.value(subject=onj, predicate=OWN.Destí)                
                start_date=gm.value(subject=onj, predicate=OWN.DataInici)
                end_date=gm.value(subject=onj, predicate=OWN.DataFinal)
                activ_ludicas = gm.value(subject=onj, predicate=OWN.Lúdic)
                activ_culturales = gm.value(subject=onj, predicate=OWN.Cultural)
                activ_festivas= gm.value(subject=onj, predicate=OWN.Festiu)                            
                graph_actv=Graph()                
                graph_actv=busquedaActv(desti, start_date, end_date, activ_ludicas,activ_culturales,activ_festivas)
                
                gr = build_message(graph_actv,
                                 ACL['confirm'],
                                 sender=AgentActivitats.uri,
                                 msgcnt=mss_cnt)
            # No habia ninguna accion en el mensaje
            else:
                gr = build_message(Graph(),
                                   ACL['not-understood'],
                                   sender=AgentActivitats.uri,
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
   # ab1 = Process(target=agentbehavior1, args=(cola1,))
    #ab1.start()

    # Ponemos en marcha el servidor
    app.run(host=hostname, port=port)

    # Esperamos a que acaben los behaviors
    #ab1.join()
    
