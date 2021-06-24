#ifndef SOURCE_H_
#define SOURCE_H_
#include "Object.h"
#include "../forwardDeclarations.h"
//Classe que representa la source del model (generador de cotxes)
class Source: public Object
{
    //Nombre d'entitats creades al model
    int entitatsCreades = 0;
    //Cua a la que s'han d'enviar els cotxes
    Queue* cua;
    //Configuració del generador de nombres aleatoris
    float centreTempsEntreArribades = 0, desviacioTempsEntreArribades = 0;
public:
    //Creadora
    Source(EventScheduler* ev);

    //Configura les dades que es passaran al generador de nombres aleatoris
    void setDistribution(float cTEA, float dTEA);
    //Crea la connexio amb la cua a la que s'han de passar els cotxes (setter de cua)
    void crearConnexio(Queue* q);
    //Tracta un esdeveniment
    void tractarEsdeveniment(Esdeveniment* esd) override;
    //Funcio a executar al iniciar la simulacio. Prepara els estadistics i l'estat inicial
    void simulationStart();
    //Funcio a executar al acabar la simulacio. Escriu els estadistics per pantalla
    void simulationEnd();
    //Processament de la seguent arribada. Crea una entitat, la passa a la cua i programa la seguent.
    void processNextArrival(Esdeveniment* esd);
    //Retorna un esdeveniment de creacio de la proxima entitat.
    Esdeveniment* properaArribada(float time);
};

#endif // SOURCE_H_
