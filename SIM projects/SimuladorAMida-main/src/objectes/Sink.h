#ifndef SINK_H_
#define SINK_H_
#include "Object.h"
#include "../forwardDeclarations.h"
class Sink: public Object
{
    public:
        //Creadora de la classe
        Sink(EventScheduler* ev);
        //Nombre d'entitats destruides
        int entitatsDestruides = 0;
        //Rep una entitat i la destrueix
        void rebreEntitat(Entitat* ent);
        //Tracta un esdeveniment
        void tractarEsdeveniment(Esdeveniment* esd) override;
        //Funcio a executar al rebre l'esdeveniment simulation start. Posa entiatsDestruides a 0
        void simulationStart();
        //Funcio a executar al rebre l'esdeveniment simulation end. Escriu els estadistics per  pantalla.
        void simulationEnd();
};
#endif
