# -*- coding: utf-8 -*-
"""
Created on Sat Jun  5 10:05:06 2021

@author: aleix
"""

def cosa_de_molt():
    
    print("funciona")
    ludic=input()
    cultural=input()
    festiu=input()
    dies = input()
    activitats =  float(dies)*float(3)
        
    if(festiu=="molt"):                                             
        if(ludic=="normal"):
            if(cultural=="algo"):
                festiu=int(activitats*0.45)
                ludic = int(activitats*0.45)  
            elif(cultural=="res"):
                festiu=int(activitats*0.60)
                ludic = int(activitats*0.40)             
        elif(ludic=="algo"):
            if(cultural=="normal"):
                festiu=int(activitats*0.45) 
                ludic = int(activitats*0.25)  
            elif(cultural=="res"):
                festiu=int(activitats*0.70)
                ludic = int(activitats*0.30)         
        elif(ludic=="res"):
            if(cultural=="normal"):
                festiu=int(activitats*0.60) 
                ludic = int(activitats*0) 
            elif(cultural=="algo"):
                festiu=int(activitats*0.75)
                ludic =int( activitats*0)
        cultural = int(activitats) -(ludic+festiu)
        
    elif(ludic=="molt"):                                             
        if(festiu=="normal"):
            if(cultural=="algo"):
                ludic=int(activitats*0.45)
                festiu =int( activitats*0.45)
            elif(cultural=="res"):
                ludic=int(activitats*0.60)
                festiu =int( activitats*0.40)             
        elif(festiu=="algo"):
            if(cultural=="normal"):
                ludic=int(activitats*0.45)
                festiu = int(activitats*0.25) 
            elif(cultural=="res"):
                ludic=int(activitats*0.70)
                festiu =int( activitats*0.30)         
        elif(festiu=="res"):
            if(cultural=="normal"):
                ludic=int(activitats*0.60)
                festiu = int(activitats*0)
            elif(cultural=="algo"):
                ludic=int(activitats*0.75)
                festiu = int(activitats*0.0)
        cultural = int(activitats  )-(festiu+ludic)
        
    elif(cultural=="molt"):                                             
        if(festiu=="normal"):
            if(ludic=="algo"):
                cultural=int(activitats*0.45)
                festiu = int(activitats*0.45) 
            elif(ludic=="res"):
                cultural=int(activitats*0.60)
                festiu = int(activitats*0.40)           
        elif(festiu=="algo"):
            if(ludic=="normal"):
                cultural=int(activitats*0.45) 
                festiu = int(activitats*0.25) 
            elif(cultural=="res"):
                cultural=int(activitats*0.70)
                festiu = int(activitats*0.30)        
        elif(festiu=="res"):
            if(ludic=="normal"):
                cultural=int(activitats*0.60) 
                festiu = int(activitats*0) 
            elif(ludic=="algo"):
                cultural=int(activitats*0.75)
                festiu = int(activitats*0.0)
        ludic = int(activitats) -(festiu+cultural)
    
    
    
    print("festiu " + str(festiu))
    print("ludic " + str(ludic))
    print("cultural " + str(cultural))
    
            
    
if __name__ == '__main__':
    # Ponemos en marcha los behaviors
   # ab1 = Process(target=agentbehavior1, args=(cola1,))
    #ab1.start()
    cosa_de_molt()
   
    #ab1.join()
    print('The End')