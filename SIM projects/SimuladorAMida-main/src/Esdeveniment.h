#ifndef ESDEVENIMENT_H_
#define ESDEVENIMENT_H_


#include "forwardDeclarations.h"
using namespace std;
class Esdeveniment{
	public:
		enum Tipus { SIMULATION_START, NEXT_ARRIVAL, END_SERVICE, SIMULATION_END};
		Esdeveniment(Object* ob, Tipus tipus, float temps);
		//Retorna el temps en el que s'ha programat l'esdeveniment
		float getTime();
		//REtorna el tipus de l'esdeveniment
		Tipus getTipus();
		//Retorna l'objecte que ha de llançar l'esdeveniment
		Object* getObjecte();

	private:
		Object* objecte;
		Tipus tipus;
		float time;
};
//Comparador d'esdeveniments per poder fer una cua ordenada
struct CompareEsdeveniment
{
    bool operator()(Esdeveniment* lhs, Esdeveniment* rhs) const;
};
#endif

