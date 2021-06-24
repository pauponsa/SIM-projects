#ifndef EVENTSCHEDULER_H_
#define EVENTSCHEDULER_H_

#include <queue>
#include "forwardDeclarations.h"
#include "Esdeveniment.h"
#include <bits/stdc++.h>
#include <unistd.h>
       #include <fcntl.h>
using namespace std;
class EventScheduler{
	float currentTime =0;
	int timeBetweenEvents = 0;
	float tempsMaximExecucio = FLT_MAX;
	//queue<Esdeveniment*> eventList;
	priority_queue<Esdeveniment*, vector<Esdeveniment*>, CompareEsdeveniment> eventList;
	void configurarModel();
	list<Object*> llistaObjectes;
	public:
	    float getCurrentTime();
	    Esdeveniment* donamEsdeveniment();
	    void run();
		void afegirEsdeveniment(Esdeveniment* aux);
		int llargariaCua();
		void reiniciarCua();
		void getStatistics();
};
#endif
