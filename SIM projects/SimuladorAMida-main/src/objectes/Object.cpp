#include "Object.h"
#include "../includeEveryThing.h"

void Object::setEventScheduler(EventScheduler* ev)
{
	eventScheduler = ev;
}
void Object::tractarEsdeveniment(Esdeveniment* esd)
{
	//Tractar l'esdeveniment
	cout<<"tractant esdeveniment objecte"<<endl;
}
