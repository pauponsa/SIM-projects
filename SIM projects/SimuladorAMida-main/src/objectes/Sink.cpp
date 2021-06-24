#include "./Sink.h"
#include "../includeEveryThing.h"
Sink::Sink(EventScheduler* ev)
{
    setEventScheduler(ev);
}

void Sink::rebreEntitat(Entitat* ent)
{
    entitatsDestruides++;
    delete ent;
}

void Sink::simulationEnd()
{
    cout<<"Mostrant esdadistics de la SINK"<<endl;
    cout<<"Entitats destruides: "<<entitatsDestruides<<endl;
}
void Sink::simulationStart()
{
    entitatsDestruides = 0;
}
void Sink::tractarEsdeveniment(Esdeveniment* esd)
{
    switch(esd->getTipus()){
        case Esdeveniment::Tipus::SIMULATION_START:
            simulationStart();
            break;
        case Esdeveniment::Tipus::SIMULATION_END:
            simulationEnd();
            break;
        default: break;
    }
}
