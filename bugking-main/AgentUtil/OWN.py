"""
.. module:: OWN

 Translated by owl2rdflib

 Translated to RDFlib from ontology http://www.semanticweb.org/aleix/ontologies/2021/4/ProjecteECSDI

 :Date 30/05/2021 17:37:27
"""
from rdflib import URIRef
from rdflib.namespace import ClosedNamespace

OWN =  ClosedNamespace(
    uri=URIRef('http://www.semanticweb.org/aleix/ontologies/2021/4/ProjecteECSDI'),
    terms=[
        # Classes
        'Recollir_informació_meteor.',
        'Informació_vols',
        'Informació_activitats',
        'Sol·licitar_mètode_pagament',
        'Recomanacions',
        'Restriccions_Activitats',
        'Valoracions',
        'Activitats',
        'Allotjament',
        'Cerca_allotjaments',
        'Cerca_vols',
        'Restriccions_Allotjament',
        'Demanar_alternativa',
        'Accions',
        'Percepcions',
        'Canvi_a_realitzar',
        'Factura',
        'Informació_allotjaments',
        'Fer_pagament',
        'Informació_meteorologica',
        'Paquet',
        'Restriccions_Transport',
        'Demanar_satisfacció',
        'Transport',
        'Demanar_pla_viatge',
        'Buscar_activitat',
        'Usuari',
        'Restriccions',
        'Demanar_restriccions',
        'Meteorologia',

        # Object properties
        'Busca',
        'Restringeix',
        'Paga',
        'Restringeix_Transport',
        'Valora',
        'Compost_per',
        'Restringeix_Allotjament',
        'Abona',
        'Recomana',
        'Demana',
        'Restringeix_Activitat',
        'Retorna',
        'Afecta',
        'Informa_sobre',

        # Data properties
        'Vaixell',
        'Data_Inici_Reserva',
        'Franja_activitat',
        'HoraArribadaAnada',
        'DataFinal',
        'Tipus_de_Transport',
        'Metode_de_pagament',
        'PreuMínim',
        'Lúdic',
        'Tipus_Activitats',
        'Destí',
        'Data_Fi_Reserva',
        'Tren',
        'Temps_atmosfèric',
        'Preu_Allotjament',
        'DataInici',
        'PreuMàxim',
        'Hora_Fi_Reserva',
        'TerminalTornada',
        'Temps_necessari_per_activitat',
        'Nom_Allotjament',
        'Origen',
        'Adreça_allotjament',
        'Hora_Inici_Reserva',
        'Email',
        'Festiu',
        'Nom_Activitat',
        'TerminalAnada',
        'AeroportArribadaAnada',
        'Cultural',
        'Nota',
        'Lloc_Activitat',
        'Autocar',
        'Areoport',
        'HoraSortidaTornada',
        'AeroportArribadaTornada',
        'Preu',
        'Dia_activitat',
        'HoraArribadaTornada',
        'Avió',
        'Preu_Transport',
        'Nom_Reserva',
        'AeroportSortidaAnada',
        'Nom',
        'AeroportSortidaTornada',
        'Zona_Allotjament',
        'HoraSortidaAnada',
        'Habitació'

        # Named Individuals
    ]
)