<h1>Akka Prime Counter Sample</h1><br/>
PrimeCounter is an Akka actor based concurrent application that computes the number of primes between 0 and the given number, and demonstrates a simple use case of Akka actors. Akka is a toolkit for developing concurrent, and distributed applications. Actors are asynchronous, non-blocking, event-driven programming model, which is higly scalable and reliable. For more info, http://akka.io/.  

Verifying if a number is prime or not is a time consuming task; hence, it is a good candidate for concurrency. So the actor based approach can help to scale the process up and out. The application contains a few actors that handles the process.
<b>MasterActor</b>, is the root actor (supervisor) that initializes child actors 
```java 
private ActorRef mapActor=getContext().actorOf(new Props(MapActor.class),"map");
    private ActorRef primeVerifierActor=getContext()
            .actorOf(new Props(PrimeVerifierActor.class)
                    .withRouter(new RoundRobinRouter(16)),"prime-verifier");
    private ActorRef counterActor=getContext()
            .actorOf(new Props(CounterActor.class),"counter-actor");
```

and rotes received messages to them.
```java 
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
```
<b>MapActor</b>, loops through 0 to the given number and creates messages of type Long and passes them back to MasterActor.
```java   
 if(message instanceof Size){
           Size counter=(Size) message;
           for(Long i=0l;i<counter.getSize();i++){
              getSender().tell(i);
           }
       }else{
           unhandled(message);
       }
```
MasterActor, forwards the messages to <b>PrimeVerifierActor</b> which verifies if the given number is prime or not.This is where the computation is done, hence we wish to achieve maximum concurrency here. Once, PrimeVerifierActor completes the computation, it generates a new message of type Number with a boolean flag indicating if the number is prime or not, and passes it back to MasterActor.
```java 
 if(message instanceof Long){
          long number=(Long) message;
          boolean prime = PrimeVerifier.isPrime(number);
          if(prime){
              getSender().tell(new Number(number,true));
          }else{
              getSender().tell(new Number(number,false));
          }
       }else {
           unhandled(message);
       } 
```
Finally, the MasterActor in passes the received messages of type Number, to <b>CounterActor</b> which counts the number of primes and holds the count in a state variable. 
```java 
private Integer primes=0;

    public void onReceive(Object message) throws Exception {
        if(message instanceof Number){
            Number number=(Number) message;
            if(number.getPrime()){
                primes++;
            }
        }else if(message instanceof Result){
            getSender().tell(primes);
        }else {
            unhandled(message);
        }
    }
```
Once the count is equals the initial size/range, it prints out the duration of the process. For that, a count down latch is used, and it the main thread is blocked until all messages are processed.

```java 
   countDownLatch.await();
```   
The time consuming part of the process is the verification part. Therefore, we want maximum concurrency for PrimeVerifierActor. The test machine had 16 cores, so initialization of the PrimeVerifierActor with a RoundRobinRouter of 16 keeps the CPU busiest and gives the best performance (43-46 seconds).

```java
private ActorRef primeVerifierActor=getContext()
            .actorOf(new Props(PrimeVerifierActor.class)
                    .withRouter(new RoundRobinRouter(16)),"prime-verifier"); 
``` 
Where a none concurrent version of the application, takes roughly around 115 to 120 seconds for the same range.   

                    
