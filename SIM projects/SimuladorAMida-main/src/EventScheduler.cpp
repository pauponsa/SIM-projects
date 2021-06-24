#include <iostream>

#include "EventScheduler.h"
#include "./includeEveryThing.h"
using namespace std;
int main(int argc, char * argv[])
{
	EventScheduler ev = EventScheduler();
	ev.run();
}
//Fincions Event List
//Bucle principal
void EventScheduler::run()
{
    configurarModel();
    string stop = "a";
	while(llargariaCua()>0 && currentTime<tempsMaximExecucio&& stop != "s")
	{
	    cout<<"TRACTANT NOU ESDEVENIMENT..."<<endl;
	    Esdeveniment* current = donamEsdeveniment();
        currentTime = current->getTime();
        current->getObjecte()->tractarEsdeveniment(current);
		cout<<"Tipus esdeveniment: "<<current->getTipus()<<endl;
		cout<<"Temps esdeveniment finalitzat: "<<current->getTime()<<endl<<endl;
		delete current;
		std::this_thread::sleep_for(
             std::chrono::milliseconds(timeBetweenEvents));
        if(tempsMaximExecucio == INT_MAX)getline(std::cin, stop);

	}
	getStatistics();
}
//Configura el model abans de que el començem a fer servir (demana també l'input de l'usuari)
void EventScheduler::configurarModel()
{

    llistaObjectes.clear();

	//Configuració de la source
	Source* source = new Source(this);
	float centreTempsEntreArribades, desviacioEntreArribades;
	llistaObjectes.push_back(source);
	cout<<"indica la mitjana de la distribució normal del temps entre arribades: "<<endl;
	cin>>centreTempsEntreArribades;
	cout<<"indica la desviació de la distribució normal del temps entre arribades: "<<endl;
	cin>>desviacioEntreArribades;
    source->setDistribution(centreTempsEntreArribades, desviacioEntreArribades);
    Esdeveniment* aux = new Esdeveniment(source, Esdeveniment::Tipus::SIMULATION_START, currentTime);
    afegirEsdeveniment(aux);

    //Configuracio de la cua
    Queue* cua = new Queue(this);
    aux = new Esdeveniment(cua, Esdeveniment::Tipus::SIMULATION_START, currentTime);
    llistaObjectes.push_back(cua);
    afegirEsdeveniment(aux);
    source->crearConnexio(cua);
    cua->crearConnexio(source);
    //Configuració dels peatges
	float centreTempsProcessament, desviacioTempsProcessament;
	cout<<"indica la mitjana de la distribucio normal del temps de processament: "<<endl;
	cin>>centreTempsProcessament;
	cout<<"indica la desviacio de la distribucio normal del temps de processament: "<<endl;
	cin>>desviacioTempsProcessament;

	int numPeatges;
	cout<<"indica el nombre de peatges"<<endl;
	cin>>numPeatges;
    list<Peatge*> peatges;
    for(int i = 0; i<numPeatges; i++)
    {
        Peatge* peatge = new Peatge(this);
        peatge->id = i+1;
        peatge->setDistribution(centreTempsProcessament, desviacioTempsProcessament);
        cua->crearConnexio(peatge);
        peatge->crearConnexio(cua);
        peatges.push_back(peatge);
        aux = new Esdeveniment(peatge, Esdeveniment::Tipus::SIMULATION_START, currentTime);
        llistaObjectes.push_back(peatge);
        afegirEsdeveniment(aux);
    }
    //Connexio operaris
    int numOperaris;
	cout<<"indica el nombre d'operaris"<<endl;
	cin>>numOperaris;
    list<Operari*> operaris;
    for(int i = 0; i<numOperaris; i++)
    {
        Operari* operari= new Operari(this);
        aux = new Esdeveniment(operari, Esdeveniment::Tipus::SIMULATION_START, currentTime);
        llistaObjectes.push_back(operari);
        afegirEsdeveniment(aux);
        operari->id = i+1;
        operari->crearConnexio(peatges);
        operaris.push_back(operari);
    }
    std::list<Peatge*>::iterator it;
    for (it = peatges.begin(); it != peatges.end(); ++it)
    {
        (*it)->crearConnexio(operaris);
    }

    //Configuracio sink
    Sink* sink = new Sink(this);
    aux = new Esdeveniment(sink, Esdeveniment::Tipus::SIMULATION_START, currentTime);
    llistaObjectes.push_back(sink);
    afegirEsdeveniment(aux);
    for (it = peatges.begin(); it != peatges.end(); ++it)
    {
        (*it)->crearConnexio(sink);
    }
    //Configuracio seed
    unsigned long n;
    cout<<"indica la llavor del generador de nombres aleatoris"<<endl;
    cin>>n;
    mates::setSeed(n);

    float tempsMaximExecucioAux;
    cout<<"indica el temps maxim d'execucio (-1 és infinit)"<<endl;
    cin>>tempsMaximExecucioAux;
    if(tempsMaximExecucioAux == -1) tempsMaximExecucio = INT_MAX;
    else tempsMaximExecucio = tempsMaximExecucioAux;

    int tbe = 0;
    cout<<"Indica el temps entre la mostra dels esdeveniments"<<endl;
    cin>>tbe;
}
//Afegeix un esdeveniment a la cua d'esdeveniments
void EventScheduler::afegirEsdeveniment(Esdeveniment* aux)
{
	eventList.push(aux);
}
//Retorna la llargaria de la cua d'esdeveniments
int EventScheduler::llargariaCua()
{
	return eventList.size();
}
//Esborra tots els elements de la cua d'esdeveniments
void EventScheduler::reiniciarCua()
{
    //Cua d'esdeveniments ordenada sobre un metode propi(veure classe Esdeveniment)
	eventList = priority_queue<Esdeveniment*, vector<Esdeveniment*>, CompareEsdeveniment>();
}
//Retorna el temps actual de simulació (el de l'ultim esdeveniment)
float EventScheduler::getCurrentTime()
{
    return currentTime;
}
//Retorna el pròxim esdeveniment de la cua i l'esborra d'aquesta
Esdeveniment* EventScheduler::donamEsdeveniment()
{
    Esdeveniment* aux = eventList.top();
    eventList.pop();
    return aux;
}
//Escriu el current time i dispara l'esdeveniment de final de simulacio a tots els objectes.
void EventScheduler::getStatistics()
{
    reiniciarCua();
	cout<<"currentTime: "<<currentTime<<endl;
	std::list<Object*>::iterator it;
    for (it = llistaObjectes.begin(); it != llistaObjectes.end(); ++it)
    {
        Esdeveniment* aux = new Esdeveniment((*it), Esdeveniment::Tipus::SIMULATION_END, currentTime);
        aux->getObjecte()->tractarEsdeveniment(aux);
		delete aux;
    }
}
