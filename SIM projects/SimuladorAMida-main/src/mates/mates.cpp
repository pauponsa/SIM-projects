#include "mates.h"
#include "../includeEveryThing.h"
default_random_engine mates::generator(0);
void mates::setSeed(unsigned long n){generator.seed(n);}
float mates::getRandomNormalFloat(float mean, float desv)
{
    normal_distribution<double> distribution(mean,desv);
    return distribution(generator);
}
