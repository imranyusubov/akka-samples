package com.count.prime.counter;

import akka.actor.*;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.pattern.Patterns;
import akka.util.Duration;
import akka.util.Timeout;
import messages.Result;
import messages.Size;
import java.util.concurrent.CountDownLatch;

/**
 * Created by imran on 1/7/17.
 */
public class Main {
    private static CountDownLatch countDownLatch;
    private static Integer numberOfCores;
    private static Integer number;

    private static Timeout timeout;
    private static ActorSystem system;

    public static void main(String args[]) throws Exception {
        if(args.length!=2) {
            showUsage();
            return;
        }
        System.out.println("Computation started.");
        numberOfCores=Integer.parseInt(args[0]);
        number=Integer.parseInt(args[1]);

        countDownLatch=new CountDownLatch(number);
        concurrentCall();
    }

    private static void showUsage() {
        System.out.println("Usage: \n" +
                "arg0 : number of cores \n" +
                "arg1 : number (size)");
    }

    private static void concurrentCall() throws Exception {
        timeout = new Timeout(Duration.parse("500 seconds"));
        system=ActorSystem.create("my-actor-system");
        ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
            public UntypedActor create() {
                return new MasterActor(countDownLatch);
            }
        }), "master");
        master.tell(new Size(number));
        countDownLatch.await();
        Future<Object> future = Patterns.ask(master, new
                Result(), timeout);
        Integer result = (Integer) Await.result(future,
                timeout.duration());

        System.out.println(result);
        system.shutdown();
    }
}
