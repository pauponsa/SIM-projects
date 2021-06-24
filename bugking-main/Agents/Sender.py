# -*- coding: utf-8 -*-
"""
Created on Mon May 17 11:14:37 2021

@author: aleix
"""

import socket
import argparse
import requests
from flask import Flask, request

# Definimos los parametros de la linea de comandos
parser = argparse.ArgumentParser()
parser.add_argument('--host', default='localhost', help="Host del agente")
parser.add_argument('--port', type=int, help="Puerto de comunicacion del agente")
parser.add_argument('--acomm', help='Direccion del agente con el que comunicarse')
parser.add_argument('--aport', type=int, help='Puerto del agente con el que comunicarse')
parser.add_argument('--messages', nargs='+', default=[], help="mensajes a enviar")

app = Flask(__name__)


@app.route("/")
def isAlive():
    text = 'Hi i\'m AgentExternVols o/, if you wanna travel go to <a href= /flights?origin_lat=51.5074&origin_long=0.1278&destination_lat=41.397158&destination_long=2.160873&start_date=2021-06-23&end_date=2021-06-28&price_max=500&num=1>here</a>'
    return text


@app.route("/prova")
def ProvaComunicació():
    
    global mss_cnt

    gmess = Graph()
    
            
    return "FUNCA2" 


if __name__ == '__main__':
    # parsing de los parametros de la linea de comandos
    args = parser.parse_args()

    # Ponemos en marcha el servidor
    app.run(host=args.host, port=args.port)

    print('The End')