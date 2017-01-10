package com.count.prime.counter;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import messages.Number;
import messages.Result;
import messages.Size;

import java.util.concurrent.CountDownLatch;

/**
 * Created by imran on 1/7/17.
 */
public class MasterActor extends UntypedActor {
    private ActorRef mapActor=getContext().actorOf(new Props(MapActor.class),"map");
    private ActorRef primeVerifierActor=getContext()
            .actorOf(new Props(PrimeVerifierActor.class)
                    .withRouter(new RoundRobinRouter(16)),"prime-verifier");
    private ActorRef counterActor=getContext()
            .actorOf(new Props(CounterActor.class),"counter-actor");

    private CountDownLatch countDownLatch;

    public MasterActor(CountDownLatch countDownLatch){
        this.countDownLatch=countDownLatch;
    }

    public void onReceive(Object message) throws Exception {
        if(message instanceof Size){
            Size size=(Size) message;
            mapActor.tell(size,self());
        }else if(message instanceof Integer){
            primeVerifierActor.tell(message,self());
        }else if(message instanceof Number){
            countDownLatch.countDown();
            counterActor.tell(message);
        }else if(message instanceof Result){
            counterActor.forward(message,getContext());
        }else {
            unhandled(message);
        }
    }

}
