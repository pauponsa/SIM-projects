#ifndef QUEUE_H_
#define QUEUE_H_
#include <queue>
#include <list>
#include "Object.h"
#include "../forwardDeclarations.h"
class Queue: public Object
{
    //Cua d'entitats que hi ha a la queue
    queue<Entitat*> cua;
    //Temps al que han entrat les entitats que hi ha a la cua.
    queue<float> entryCurrentTime;
    //Source que envia les entitats
    Source* source;
    //Llista de peatges als que esta connectada la cua
    list<Peatge*> peatges;
    //Nombre d'entiats creuades
    int entitatsCreuades;
    //Suma del temps que han estat a la cua de totes les entitats. Temps maxim i minim que una entitat ha estat a la cua.
    float totalStayTime, maxStayTime, minStayTime;
    public:
        //Diferents estats als que pot estar
        enum State { EMPTY, NO_EMPTY};
        //Creadora
        Queue(EventScheduler* ev);
        //Estat al que esta
        State state;
        //Setter de source
        void crearConnexio(Source* s);
        //Afegeix p a peatges
        void crearConnexio(Peatge* p);
        //Tracta un esdeveniment
        void tractarEsdeveniment(Esdeveniment* esd) override;
        //Recull una entitat de la source
        void recullEntitat(float time, Entitat* et);
        //Funcio a executar al principi de la simulacio. Configura els estadistics i l'estat inicial
        void simulationStart();
        //Funcio a executar al final de la simulacio. Escriu els estadistics per pantalla.
        void simulationEnd();
        //Allibera una entitat (returna la ultima entitat de la cua i l'esborra d'aquesta). Tambe actualitza els estadistics corresponets. Aquesta funcio es cridada des dels peatges
        Entitat* alliberaEntitat(float time);
        int getSize();
};

#endif // QUEUE_H_
