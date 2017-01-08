package com.count.prime.counter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import messages.Size;

/**
 * Created by imran on 1/7/17.
 */
public class Main {

    public static void main(String args[]){
       concurrentCall();
    }

    private static void concurrentCall(){
        ActorSystem actorSystem=ActorSystem.create("my-actor-system");
        ActorRef actorRef = actorSystem.actorOf(new Props(MasterActor.class), "master");
        actorRef.tell(new Size(500000l));
    }
}
