main: EventScheduler.o Esdeveniment.o Object.o Entitat.o Source.o Peatge.o Queue.o Sink.o Operari.o mates.o
	#g++ -g -Wall -o main.exe ./compiled/EventScheduler.o ./compiled/Object.o ./compiled/Entitat.o ./compiled/Source.o ./compiled/Esdeveniment.o ./compiled/Peatge.o ./compiled/Operari.o ./compiled/mates.o ./compiled/Queue.o
	g++ -g -Wall -o main.exe ./compiled/*.o
EventScheduler.o: EventScheduler.cpp
	g++ -g -Wall -c -o ./compiled/EventScheduler.o EventScheduler.cpp
Entitat.o: ./objectes/Entitat.cpp
	g++ -g -Wall -c -o ./compiled/Entitat.o ./objectes/Entitat.cpp

Object.o: ./objectes/Object.cpp
	g++ -g -Wall -c -o ./compiled/Object.o ./objectes/Object.cpp

Source.o: ./objectes/Source.cpp
	g++ -g -Wall -c -o ./compiled/Source.o ./objectes/Source.cpp

Peatge.o: ./objectes/Peatge.cpp
	g++ -g -Wall -c -o ./compiled/Peatge.o ./objectes/Peatge.cpp

Queue.o: ./objectes/Queue.cpp
	g++ -g -Wall -c -o ./compiled/Queue.o ./objectes/Queue.cpp

Sink.o: ./objectes/Sink.cpp
	g++ -g -Wall -c -o ./compiled/Sink.o ./objectes/Sink.cpp

Operari.o: ./objectes/Operari.cpp
	g++ -g -Wall -c -o ./compiled/Operari.o ./objectes/Operari.cpp

Esdeveniment.o: Esdeveniment.cpp
	g++ -g -Wall -c -o ./compiled/Esdeveniment.o Esdeveniment.cpp

mates.o: ./mates/mates.cpp
	g++ -g -Wall -c -o ./compiled/mates.o ./mates/mates.cpp

.PHONY: clean
clean:
	rm -rf *.o
