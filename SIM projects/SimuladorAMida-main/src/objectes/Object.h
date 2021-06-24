#ifndef OBJECT_H_
#define OBJECT_H_

#include "../forwardDeclarations.h"
#include <iostream>
using namespace std;
//Classe de la que deriven tots els objectes (entitats de simulacio, tant temporals com fixes).
class Object{
	protected:
	    //Referencia a l'eventScheduler del model sobre el cual es programaran nous esdeveniments
		EventScheduler* eventScheduler;
	public:
	    virtual ~Object(){}
	    //Configura l'eventShceduler sobre el que programar nous esdeveniments
		void setEventScheduler(EventScheduler* ev);
		//Funció que s'executa al tractar l'esdeveniment
		virtual void tractarEsdeveniment(Esdeveniment* esd);
};
#endif
