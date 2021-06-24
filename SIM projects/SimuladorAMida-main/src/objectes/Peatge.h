#ifndef PEATGE_H_
#define PEATGE_H_
#include "../forwardDeclarations.h"
#include "Object.h"
#include <list>
class Peatge: public Object{
        //Nombre d'entitats processades pel peatge
        int entitatsProcessades = 0;
        //Configuració del generador de nombres aleatoris
        float centreTempsProcessament = 0, desviacioTempsProcessament  = 0;
        //Cua a la que el peatge esta connectat
        Queue* cua;
        //Sink a la que el peatge esta connectat
        Sink* sink;
        //Llista d'operaris als que el peatge esta connectat
        list<Operari*> operaris;
    public:
        //Id del peatge (serveix unicament pels logs)
        int id = 0;
        //Possibles estats del peatge. Waiting vol dir que esta esperant un operari
        enum State { IDLE, BUSY, WAITING};
        //Creadora
        Peatge(EventScheduler* ev);
        //Estat actual del peatge
        State state;
        //Entitat que es troba en aquells moments al peatge
        Entitat* entitatActiva;
        //Estadistics que controlen el temps que ha iniciat el processament/espera actual i el total de temps que el peatge ha estat treballant/esperant
        float  timeCurrentTask, timeWorking, timeCurrentWaiting, timeWaiting;

        //Operari que esta treballant en el peatge en aquell moment
        Operari* operariActiu;
        //Setter de la cua
        void crearConnexio(Queue* cua);
        //Setter d'operaris
        void crearConnexio(list<Operari*> op);
        //Setter de sink
        void crearConnexio(Sink* s);
        //Configura les dades que es passaran al generador de nombres aleatoris
        void setDistribution(float cTP, float dTP);
        //Carrega una nova entitat al peatge com a entitat activa i demana un operari.
        void recullEntitat(Entitat* ent, float time);
        //Tracta un esdeveniment
        void tractarEsdeveniment(Esdeveniment* esd) override;
        //Funcio a executar al principi de la simulacio. Configura l'estat i estadistics inicials,
        void simulationStart();
        //Final de la simulacio. Escriu els estadistics per pantalla
        void simulationEnd(Esdeveniment* esd);
        //Busca a la llista d'operaris un que estigui lliure. Si el troba,comença a processar l'entitat, si no,
        //s'apunta a la cua de peatges que no han trobat operaris i s'espera a que n'hi hagi un de lliure.
        //En els dos casos s'actualitzen els estadistics
        void demanarOperari(float time);
        //Comença a processar l'entitat, programant el final del servei.
        //També indica a l'operari designat que programi el seu final de servei fent servir el mateix temps d'acabament.
        void llancarIniciServei(Operari* op, float time);
        //Retorna un esdeveniment que conte el final d'un servei. També actualitza els estadistics corresponents.
        //El parametre time queda actualitzat amb el temps de finalitzacio d'aquest esdeveniment
        Esdeveniment* programarFinalServei(float& time);
        //Processa el final d'un servei, demanant una nova entitat a la cua i, si la troba, començant a processar-la.
        //També actualitza els estadistics.
        void processarFiServei(Esdeveniment* esd);
};

#endif
