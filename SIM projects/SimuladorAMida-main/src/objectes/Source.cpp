#include "./Source.h"
#include "../includeEveryThing.h"
Source::Source(EventScheduler* ev)
{
    setEventScheduler(ev);
}

void Source::crearConnexio(Queue* p)
{
    cua = p;
}
void Source::setDistribution(float cTEA, float dTEA)
{
    centreTempsEntreArribades = cTEA;
    desviacioTempsEntreArribades = dTEA;
}
void Source::tractarEsdeveniment(Esdeveniment* esd)
{
    cout<<"SOURCE: tractant esdeveniment"<<endl;
    switch(esd->getTipus()){
        case Esdeveniment::Tipus::SIMULATION_START:
            simulationStart();
            break;
        case Esdeveniment::Tipus::NEXT_ARRIVAL:
            processNextArrival(esd);
            break;
        case Esdeveniment::Tipus::SIMULATION_END:
            simulationEnd();
            break;
        default: break;
    }
}
void Source::simulationStart()
{
    Esdeveniment* nou = properaArribada(eventScheduler->getCurrentTime());
    eventScheduler->afegirEsdeveniment(nou);
    entitatsCreades = 0;
}
void Source::simulationEnd()
{
    cout<<"mostrant estadistics de la SOURE"<<endl;
    cout<<"entitatsCreades: "<<entitatsCreades<<endl;
}
void Source::processNextArrival(Esdeveniment* esd)
{
    cout << "SOURCE: Creant nou objecte: ";
    Entitat* ent = new Entitat();
    //Enviar a la cua
    cua->recullEntitat(esd->getTime(), ent);
    Esdeveniment* nou = properaArribada(eventScheduler->getCurrentTime());
    eventScheduler->afegirEsdeveniment(nou);
}
Esdeveniment* Source::properaArribada(float time)
{
    cout << "SOURCE: Creant nova arribada: ";
    float tempsEntreArribades = mates::getRandomNormalFloat(centreTempsEntreArribades, desviacioTempsEntreArribades);
    if(tempsEntreArribades<0) tempsEntreArribades = 0;
    entitatsCreades++;
    cout << "t: "<<tempsEntreArribades<<endl;
    return new Esdeveniment(this, Esdeveniment::Tipus::NEXT_ARRIVAL , time+tempsEntreArribades);

}
