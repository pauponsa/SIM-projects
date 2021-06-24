#include "./Peatge.h"
#include "../includeEveryThing.h"
Peatge::Peatge(EventScheduler* ev)
{
    setEventScheduler(ev);
    state =  State::IDLE;
}
void Peatge::crearConnexio(Queue* cua)
{
    this->cua = cua;
}
void Peatge::crearConnexio(list<Operari*> op)
{
    operaris = op;
}
void Peatge::crearConnexio(Sink* s)
{
    sink = s;
}
void Peatge::setDistribution(float cTP, float dTP)
{
    centreTempsProcessament = cTP;
    desviacioTempsProcessament  = dTP;
}
void Peatge::recullEntitat(Entitat* ent, float time)
{
    entitatActiva = ent;
    demanarOperari(time);
}
void Peatge::tractarEsdeveniment(Esdeveniment* esd)
{
    cout<<"PEATGE "<<id<<": tractant esdeveniment "<<endl;
    switch(esd->getTipus()){
        case Esdeveniment::Tipus::SIMULATION_START:
            simulationStart();
            break;
        case Esdeveniment::Tipus::END_SERVICE:
            processarFiServei(esd);
            break;
        case Esdeveniment::Tipus::SIMULATION_END:
            simulationEnd(esd);
            break;
        default: break;
    }
}
void Peatge::simulationStart()
{
    state = State::IDLE;
    entitatsProcessades = 0;
    timeWorking = timeCurrentTask = timeWaiting = 0;
    timeCurrentWaiting = -1;
}
void Peatge::simulationEnd(Esdeveniment* esd)
{
    cout<<"Mostrant estadistics PEATGE: "<<id<<endl;
    cout<<"entitatsProcessades: "<<entitatsProcessades<<endl;
    float currentTime = esd->getTime();
    cout<<"pergentatge de temps ocupat: "<<timeWorking*100/currentTime<<"%"<<endl;
    cout<<"pergentatge de temps esperant operari: "<<timeWaiting*100/currentTime<<"%"<<endl;
}
void Peatge::demanarOperari(float time)
{

    bool trobat = false;
    std::list<Operari*>::iterator it=operaris.begin();
    while(it != operaris.end() && !trobat)
    {
        trobat = (*it)->iNeedYou(time);
        if(!trobat) it++;
    }
    if(trobat)
    {
        llancarIniciServei(*it, time);
    }
    else
    {
        Operari::operariNoTrobat(this);
        state = State::WAITING;
        timeCurrentWaiting = time;
    }
}
void Peatge::llancarIniciServei(Operari* op, float time)
{
    operariActiu = op;
    Esdeveniment* esdevAux = programarFinalServei(time);
    //Ens tornara el temps final de l'esdeveniment, ja que li hem passat per referencia.
    eventScheduler->afegirEsdeveniment(esdevAux);
    cout<<"PEATGE "<<id<<": id operari actiu: "<<operariActiu->Operari::id<<endl;
    esdevAux = operariActiu->Operari::programarFinalServei(time);
    eventScheduler->afegirEsdeveniment(esdevAux);
}
Esdeveniment* Peatge::programarFinalServei(float &time/*, Entitat* ent*/)
{
    timeCurrentTask = time;
    float tempsProcessament =  mates::getRandomNormalFloat(centreTempsProcessament, desviacioTempsProcessament);
    cout<<"PEATGE "<<id<<": nou fi servei programat. t: "<<tempsProcessament<<endl;
    state = State::BUSY;
    time += tempsProcessament;
    if(timeCurrentWaiting >= 0)
    {
        timeWaiting += timeCurrentTask- timeCurrentWaiting;
        timeCurrentWaiting = -1;
    }
    return new Esdeveniment(this, Esdeveniment::Tipus::END_SERVICE, time);
}

void Peatge::processarFiServei(Esdeveniment* esd)
{
    cout<<"PEATGE "<<id<<": servei acabat"<<endl;
    entitatsProcessades++;
    //Mirar de moure la entitat a la sink
    sink->rebreEntitat(entitatActiva);

    //Agafem nova entitat de la cua
    entitatActiva = cua->alliberaEntitat(esd->getTime());
    if(entitatActiva != nullptr)
    {
        demanarOperari(esd->getTime());
    }
    else state = State::IDLE;

    //Processar estadistics
    float tempsProces = esd->getTime()-timeCurrentTask;
    timeWorking += tempsProces;
    operariActiu->timeWorking += tempsProces;
}
