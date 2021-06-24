#ifndef MATES_H_
#define MATES_H_

#include <random>
using namespace std;
class mates
{
public:
    static default_random_engine generator;
    static void setSeed(unsigned long n);
    static float getRandomNormalFloat(float mean, float desv);
};


#endif
