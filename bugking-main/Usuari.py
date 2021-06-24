# -*- coding: utf-8 -*-
"""
Created on Tue May 18 08:39:37 2021

@author: aleix
"""

from multiprocessing import Process, Queue
import logging
import datetime
from datetime import date
from dateutil.parser import parse
import argparse
from flask import Flask, request, redirect, render_template
from rdflib import Graph, Namespace, Literal, XSD
from rdflib.namespace import FOAF, RDF
from AgentUtil.OntoNamespaces import ACL, DSO, TIO
from AgentUtil.FlaskServer import shutdown_server
from AgentUtil.ACLMessages import build_message, send_message, get_message_properties
from AgentUtil.Agent import Agent
from AgentUtil.Logging import config_logger
from AgentUtil.DSO import DSO
from AgentUtil.Util import gethostname
from AgentUtil.OWN import OWN
import socket
from wtforms import Form, BooleanField, TextField, SelectField, IntegerField, StringField, validators
from wtforms.fields.html5 import DateField
from wtforms.validators import DataRequired

ciudades = ['Londres', 'Paris']
zona = ['Centro', 'Afueras']
cantidadActividades = ['Nada','Algo','Normal','Mucho']
mensaje = ""
class MyForm (Form):
    ciudadD = SelectField('Ciudad destino', choices=[(0,'Londres'),(1,'Paris')], validators=[DataRequired()])
    zonaAloj = SelectField('Zona alojamiento', choices=[(0,'Centro'),(1,'Afueras')], validators=[DataRequired()])
    
    FechaIda = DateField ('Fecha ida', format='%d/%m/%Y', validators=[DataRequired()])
    FechaVuelta = DateField ('Fecha vuelta', format='%d/%m/%Y', validators=[DataRequired()])
    
    PrecioMax = IntegerField ('Precio Max', validators=[DataRequired()])

    TiposActividadesLudicas = SelectField('Lúdicas', choices=[(0,'Nada'),(1,'Algo'),(2,'Normal'),(3,'Mucho')],validators=[DataRequired()])
    TiposActividadesCulturales = SelectField('Lúdicas', choices=[(0,'Nada'),(1,'Algo'),(2,'Normal'),(3,'Mucho')],validators=[DataRequired()])
    TiposActividadesFestivas = SelectField('Lúdicas', choices=[(0,'Nada'),(1,'Algo'),(2,'Normal'),(3,'Mucho')],validators=[DataRequired()])

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
    hostaddr = hostname = socket.gethostname()

print('DS Hostname =', hostaddr)
if args.dport is None:
    dport = 5002
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

Usuari= Agent('Usuari',
                  agn.Usuari,
                  'http://%s:%d/plaviatge' % (hostaddr, port),
                  'http://%s:%d/Stop' % (hostaddr, port))


AgentGestorPaquets=Agent('AgentGestorPaquetss',
                       agn.AgentGestorPaquets,
                       'http://%s:%d/comm' % (dhostname, dport),
                       'http://%s:%d/Stop' % (dhostname, dport))

# Global dsgraph triplestore
dsgraph = Graph()
mss_cnt=0
form = MyForm()

@app.route('/solucio', methods=['POST'])
def solucio():
   
    if not form.validate():
    
        ciutat_desti = ciudades[int(request.form['ciudadD'])]
        zona_allotj = zona[int(request.form['zonaAloj'])]
        start_date = str(request.form['FechaIda'])
        end_date = str(request.form['FechaVuelta'])
        preu_max = request.form['PrecioMax']
        activ_ludicas = cantidadActividades[int(request.form['TiposActividadesLudicas'])]
        activ_culturales = cantidadActividades[int(request.form['TiposActividadesCulturales'])]
        activ_festivas = cantidadActividades[int(request.form['TiposActividadesFestivas'])]



        if activ_culturales == activ_ludicas or activ_culturales == activ_festivas or activ_festivas == activ_ludicas:
           global  mensaje 
           mensaje = "PROBLEMA: No se puede escoger la misma cantidad de lúdicas, festivas y culturales"
           return redirect('/main')

        if int(preu_max) <= 0:
            mensaje = "PROBLEMA: Has introducido un precio erróneo"
            return redirect('/main')
        
        today = date.today()
        t1 = today.strftime('%Y-%m-%d')
        d1 = parse(t1)
        d2 = parse(start_date)
        d3 = parse(end_date)
        
        if d2 < d1 or d3 < d1:
            mensaje = "PROBLEMA: Ha escogido una fecha anterior a la actual"
            return redirect('/main')
        
        if d3 < d2:
            mensaje = "PROBLEMA: la fecha final no puede ser anterior a la inicial"
            return redirect('/main')

        

        mensaje = ""

        #mensaje a enviar
        global mss_cnt
        gmess = Graph()
        gmess.bind('foaf', FOAF)
        gmess.bind('dso', DSO)
        gmess.bind('own', OWN)        
        reg_obj=OWN['Demana']
        gmess.add((reg_obj, RDF.type, OWN.Demanar_pla_viatge))
        gmess.add((reg_obj, OWN.DataInici, Literal(start_date, datatype=XSD.date)))
        gmess.add((reg_obj, OWN.DataFinal, Literal(end_date, datatype=XSD.date)))
        gmess.add((reg_obj, OWN.PreuMàxim, Literal(preu_max, datatype=XSD.real)))
        gmess.add((reg_obj, OWN.Destí, Literal(ciutat_desti, datatype=XSD.string)))
        gmess.add((reg_obj, OWN.Zona_Allotjament, Literal(zona_allotj, datatype=XSD.string)))
        gmess.add((reg_obj, OWN.Lúdic, Literal(activ_ludicas, datatype=XSD.string)))
        gmess.add((reg_obj, OWN.Cultural, Literal(activ_culturales, datatype=XSD.string)))
        gmess.add((reg_obj, OWN.Festiu, Literal(activ_festivas, datatype=XSD.string)))

        
        gr = send_message(build_message(gmess,
                            perf=ACL.request,
                            sender=Usuari.uri,
                            receiver=AgentGestorPaquets.uri,
                            content=reg_obj,
                            msgcnt=mss_cnt),
                        AgentGestorPaquets.address)
       

        #return gr.serialize(format='xml')
        pep=gr.value(predicate=RDF.type, object=OWN.Transport)       
        pep2=gr.value(predicate=RDF.type, object=OWN.Allotjament)
        AeroportSortidaAnada = gr.value(subject=pep,predicate=OWN.AeroportSortidaAnada)
        Preu_Transport = gr.value(subject=pep,predicate=OWN.Preu_Transport)
        Preu_Allotjament = gr.value(subject=pep,predicate=OWN.Preu_Allotjament)
        AeroportArribadaAnada = gr.value(subject=pep,predicate=OWN.AeroportArribadaAnada)
        HoraSortidaAnada = gr.value(subject=pep,predicate=OWN.HoraSortidaAnada)
        HoraArribadaAnada = gr.value(subject=pep,predicate=OWN.HoraArribadaAnada)
        AeroportSortidaTornada = gr.value(subject=pep,predicate=OWN.AeroportSortidaTornada)
        AeroportArribadaTornada = gr.value(subject=pep,predicate=OWN.AeroportArribadaTornada)
        HoraSortidaTornada = gr.value(subject=pep,predicate=OWN.HoraSortidaTornada)
        HoraArribadaTornada = gr.value(subject=pep,predicate=OWN.HoraArribadaTornada)
        NomAllotjament = gr.value(subject=pep2,predicate=OWN.Nom_Allotjament)
        Adreça_allotjament = gr.value(subject=pep2,predicate=OWN.Adreça_allotjament)
        if Adreça_allotjament == "None" or str(Adreça_allotjament) == "" or str(AeroportArribadaAnada) == "None" or str(AeroportSortidaAnada) == "None":
            mensaje = "¡¡HA OCURRIDO UN PROBLEMA!! Porfavor revise las fechas o cambie el precio"
            return redirect('/main') 
         
        Activitats_mati=[]
        Activitats_tarda=[]
        Activitats_nit=[]
        Tipus_activitats_mati=[]
        Tipus_activitats_tarda=[]
        Tipus_activitats_nit=[]
        Lloc_activitats_mati=[]
        Lloc_activitats_tarda=[]
        Lloc_activitats_nit=[]
        HoraSortidaAnada = datetime.datetime.strptime(HoraSortidaAnada,"%Y-%m-%dT%H:%M:%S")
        HoraArribadaAnada = datetime.datetime.strptime(HoraArribadaAnada,"%Y-%m-%dT%H:%M:%S")
        HoraSortidaTornada = datetime.datetime.strptime(HoraSortidaTornada,"%Y-%m-%dT%H:%M:%S")
        HoraArribadaTornada = datetime.datetime.strptime(HoraArribadaTornada,"%Y-%m-%dT%H:%M:%S")
        for t in gr.subjects(RDF.first, OWN.Activitats):
            if str(gr.value(subject=t, predicate=OWN.Franja_activitat)) == "Mañana":
                Tipus_activitats_mati.append(gr.value(subject=t, predicate=OWN.Tipus_Activitats))
                Activitats_mati.append(gr.value(subject=t, predicate=OWN.Nom_Activitat))
                Lloc_activitats_mati.append(gr.value(subject=t, predicate=OWN.Lloc_Activitat))
            if str(gr.value(subject=t, predicate=OWN.Franja_activitat)) == "Tarde":
                Tipus_activitats_tarda.append(gr.value(subject=t, predicate=OWN.Tipus_Activitats))
                Activitats_tarda.append(gr.value(subject=t, predicate=OWN.Nom_Activitat))
                Lloc_activitats_tarda.append(gr.value(subject=t, predicate=OWN.Lloc_Activitat))
            if str(gr.value(subject=t, predicate=OWN.Franja_activitat)) == "Noche":
                Tipus_activitats_nit.append(gr.value(subject=t, predicate=OWN.Tipus_Activitats))
                Activitats_nit.append(gr.value(subject=t, predicate=OWN.Nom_Activitat))
                Lloc_activitats_nit.append(gr.value(subject=t, predicate=OWN.Lloc_Activitat))


        return render_template('resultat.html',transport=AeroportSortidaAnada,
        Nom_Allotjament = NomAllotjament, Activitats_mati = Activitats_mati, Activitats_tarda = Activitats_tarda, Activitats_nit = Activitats_nit, Preu_Transport = Preu_Transport,
        AeroportArribadaAnada = AeroportArribadaAnada, HoraSortidaAnada = HoraSortidaAnada, Lloc_activitats_nit = Lloc_activitats_nit, Lloc_activitats_tarda = Lloc_activitats_tarda, Lloc_activitats_mati = Lloc_activitats_mati,
        HoraArribadaAnada = HoraArribadaAnada, AeroportSortidaTornada = AeroportSortidaTornada, 
        AeroportArribadaTornada = AeroportArribadaTornada, HoraSortidaTornada = HoraSortidaTornada,
        HoraArribadaTornada = HoraArribadaTornada, Adresa_allotjament = Adreça_allotjament, Preu_Allotjament = Preu_Allotjament, len = len(Tipus_activitats_mati), Tipus_activitats_mati = Tipus_activitats_mati, Tipus_activitats_tarda = Tipus_activitats_tarda, Tipus_activitats_nit = Tipus_activitats_nit)



       
    else:
        print( "Algo ha fallat en el Form. Torna a intentar-ho")
        return "Algo ha fallat en el Form. Torna a intentar-ho"
    

@app.route("/inici", methods=['GET'])
def inici():
    if request.args:
        return 'rrrg'
    else:
        return render_template('inici.html', form=form)
    
@app.route("/main", methods=['GET'])
def main():
    if request.args:
        return 'rrrg'
    else:
        global mensaje
        return render_template('main.html', form=form, mensaje= mensaje)

@app.route('/disfruta', methods=['GET'])
def disfruta():
    return render_template('disfruta.html')  
  

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
    """
    Entrypoint de comunicacion del agente
    """
    return "Hola"


def tidyup():
    """
    Acciones previas a parar el agente
    """
    pass


def agentbehavior1():
    """
    Un comportamiento del agente
    :return:
    """    

if __name__ == '__main__':
    # Ponemos en marcha los behaviors
    #ab1 = Process(target=agentbehavior1)
    #ab1.start()

    # Ponemos en marcha el servidor
    app.run(host='0.0.0.0', port=port)

    # Esperamos a que acaben los behaviors
    #sab1.join()
    #logger.info('The End')