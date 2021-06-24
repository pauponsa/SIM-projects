#ifndef OPERARI_H_
#define OPERARI_H_

#include "Object.h"
#include "../forwardDeclarations.h"
#include <list>
#include <queue>
class Operari: public Object
{
    //nombre de clients servits
    int clientsServits = 0;
    //Llista de peatges als que l'operari esta connectat
    list<Peatge*> peatges;
    //Cua de peatges que no han trobat operari i que s'estan esperant
    static queue<Peatge*> cuaServei;

public:
    //Possibles estats
    enum State { IDLE, BUSY};
    //Creadora
    Operari(EventScheduler* ev);
    //Estat actual
    State state;
    //Temps total que l'operari ha estat treballant. Es actualitzat des del peatge
    float timeWorking;
    //Id de l'operari (serveix unicament pels logs)
    int id;

    //Setter de peatges
    void crearConnexio(list<Peatge*> p);
    //Afegeix un peatge a peatges
    void addPeatge(Peatge* p);
    //Tracta un esdeveniment
    void tractarEsdeveniment(Esdeveniment* esd);
    //Funcio a executar al principi de la simulaio. Posa els estadistics i l'estat al seu estat inicial.
    void simulationStart();
    //Funcio a executar al finalitzar l'execucio. Escriu els estadistics per pantalla.
    void simulationEnd(Esdeveniment* esd);
    //Processa el final del servei, posant l'estat a IDLE si no hi ha peatges esperant o movent-se a un altre d'aquests
    //comunicant-li que ja pot començar a processar en cas contrari. També actualitza les estadistiques
    void processarFiServei(Esdeveniment* esd);
    //Retorna un esdeveniment de final de servei que acabara al finishTime i actualitza l'estat.
    Esdeveniment* programarFinalServei(float finishTime);
    //Retorna si esta disponible
    bool iNeedYou(float time);
    //Funcio que afegeix un peatge a la cua de peatges esperant un operari. Es crida des de peatge
    static void operariNoTrobat(Peatge* p);

};

#endif // OPERARI_H_
