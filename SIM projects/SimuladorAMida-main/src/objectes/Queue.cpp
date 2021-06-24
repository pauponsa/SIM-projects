#include "./Queue.h"
#include "../includeEveryThing.h"
Queue::Queue(EventScheduler* ev)
{
    setEventScheduler(ev);
    state =  State::EMPTY;
}

void Queue::crearConnexio(Source* s)
{
    source = s;
}
void Queue::crearConnexio(Peatge* p)
{
    peatges.push_back(p);
}
void Queue::tractarEsdeveniment(Esdeveniment* esd)
{
    cout<<"QUEUE: tractant esdeveniment queue"<<endl;
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
void Queue::simulationStart()
{
    state =  State::EMPTY;
    entitatsCreuades= 0;
    totalStayTime = maxStayTime = 0;
    minStayTime = FLT_MAX;
}
void Queue::simulationEnd()
{
    cout<<"Mostrant els estadistics de la QUEUE"<<endl;
    cout<<"QUEUE: entitatsCreuades: "<<entitatsCreuades<<endl;
    cout<<"QUEUE: average stay time: "<<totalStayTime/float(entitatsCreuades)<<endl;
    cout<<"QUEUE: max stay time: "<<maxStayTime<<endl;
    cout<<"QUEUE: min stay time: "<<minStayTime<<endl;

}
void Queue::recullEntitat(float time, Entitat* et)
{
    entitatsCreuades++;
    //Intentem enviar l'entitat per algun peatge
    list<Peatge*>::const_iterator it = peatges.begin();
    while(it != peatges.end() && (*it)->state != Peatge::State::IDLE) ++it;
    if(it != peatges.end())
    {
        cout<<"QUEUE: Entitat enviada al peatge a la cua"<<endl;
        (*it)->recullEntitat(et, time);
        minStayTime = 0;
    }
    else
    {
        cout<<"QUEUE: Entitat recollida a la cua"<<endl;
        cua.push(et);
        entryCurrentTime.push(time);
    }
    if(cua.size() != 0) state =  State::NO_EMPTY;
    else state =  State::EMPTY;
}
Entitat* Queue::alliberaEntitat(float time)
{

    if(!cua.empty())
    {
        cout<<"QUEUE: Entitat alliberada de la cua"<<endl;
        Entitat* aux = cua.front();
        cua.pop();
        if(cua.size() == 0) state =  State::EMPTY;
        //Tractem els estadistics
        float entryTime = entryCurrentTime.front();
        entryCurrentTime.pop();
        float totalTime = time-entryTime;
        totalStayTime += totalTime;
        if(totalTime>maxStayTime) maxStayTime = totalTime;
        if(totalTime<minStayTime) minStayTime = totalTime;

        return aux;
    }
    cout<<"QUEUE: Cua buida. Entitat no alliberada"<<endl;
    return nullptr;
}
int Queue::getSize()
{
    return cua.size();
}
