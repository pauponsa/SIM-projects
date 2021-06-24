#include "Esdeveniment.h"
#include "./includeEveryThing.h"
Esdeveniment::Esdeveniment(Object* ob, Tipus tipus, float temps)
{
	time = temps;
	objecte = ob;
	this->tipus = tipus;
}
float Esdeveniment::getTime(){ return time;}
Esdeveniment::Tipus Esdeveniment::getTipus(){return tipus;}
Object* Esdeveniment::getObjecte(){return objecte;}

bool CompareEsdeveniment::operator()(Esdeveniment* lhs, Esdeveniment* rhs) const
    {
        float lhst = lhs->getTime();
        float rhst = rhs->getTime();
        if(lhst != rhst) return lhst>rhst;
        else
        {
            if(lhs->getTipus() == Esdeveniment::Tipus::SIMULATION_START) return false;
            if(rhs->getTipus() == Esdeveniment::Tipus::SIMULATION_START) return true;
            if(lhs->getTipus() == Esdeveniment::Tipus::NEXT_ARRIVAL) return false;
            if(rhs->getTipus() == Esdeveniment::Tipus::NEXT_ARRIVAL) return true;
            if(lhs->getTipus() == Esdeveniment::Tipus::END_SERVICE) return false;
            if(lhs->getTipus() == Esdeveniment::Tipus::END_SERVICE) return true;
            if(lhs->getTipus() == Esdeveniment::Tipus::SIMULATION_END) return false;
            return true;
        }
    }
