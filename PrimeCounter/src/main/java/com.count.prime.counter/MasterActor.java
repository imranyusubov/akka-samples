package com.count.prime.counter;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import messages.Number;
import messages.Size;

/**
 * Created by imran on 1/7/17.
 */
public class MasterActor extends UntypedActor {
    private ActorRef mapActor=getContext().actorOf(new Props(MapActor.class),"map");
    private ActorRef primeVerifierActor=getContext()
            .actorOf(new Props(PrimeVerifierActor.class)
                    .withRouter(new RoundRobinRouter(16)),"prime-verifier");

    private Long primeCount=0l;
    private Long computed=0l;
    private Size size=new Size(0l);
    private Long started;
    private Long completed=0l;

    @Override
    public void preStart() {
        started=System.nanoTime();
    }

    public void onReceive(Object message) throws Exception {
        if(message instanceof Size){
            System.out.println("Computation started.");
            this.size=(Size) message;
            mapActor.tell(size,self());
        }else if(message instanceof Long){
             primeVerifierActor.tell(message,self());
        }else if(message instanceof Number){
            Number number=(Number) message;
            computed++;
            if(number.getPrime()){
                primeCount++;
            }

            if(computed==size.getSize()-1){
                completed=System.nanoTime();
                long elapsedTime=completed-started;
                double seconds = (double)elapsedTime / 1000000000.0;
                System.out.println("Computation completed, Prime Numbers="+primeCount+" Duration ="+seconds+" seconds.");
            }
        }
    }

}
