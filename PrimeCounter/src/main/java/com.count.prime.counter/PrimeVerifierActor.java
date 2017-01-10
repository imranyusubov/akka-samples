package com.count.prime.counter;

import akka.actor.UntypedActor;
import messages.Number;

/**
 * Created by imran on 1/7/17.
 */
public class PrimeVerifierActor extends UntypedActor {

    public void onReceive(Object message) throws Exception {
        if(message instanceof Integer){
          Integer number=(Integer) message;
          boolean prime = PrimeVerifier.isPrime(number);
          if(prime){
              getSender().tell(new Number(number,true));
          }else{
              getSender().tell(new Number(number,false));
          }
       }else {
           unhandled(message);
       }
    }
}
