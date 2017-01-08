<h1>Akka Prime Counter</h1><br/>
PrimeCounter is an Akka actor based concurrent application that computes the number of primes between 0 and the given number, and demonstrates a simple use case of Akka actors. Akka is a toolkit for developing concurrent, and distributed applications.Actor based design promets lock free concurrency, as a result the application becomes more reliable and scalable. For more info, http://akka.io/.  

Verifying if a number is prime or not is a time consuming task; hence, it is a good candidate for concurrency. So the actor based approach can help to scale the process up and out. The application contains a few actors that handles the process.
<b>MasterActor</b>, is the root actor (supervisor) that rotes received messages to child actors.
```java 
if(message instanceof Size){
            System.out.println("Computation started.");
            this.size=(Size) message;
            mapActor.tell(size,self());
        }else if(message instanceof Long){
             primeVerifierActor.tell(message,self());
        }else if(message instanceof Number){
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
MasterActor, forwards the messages to PrimeVerifierActor which verifies if the given number is prime or not. This is where the computation is done, and we wish to achieve maximum concurrency. Once, <b>PrimeVerifierActor</b> completes the computation, it generates a new message of type Number and passes it back to MasterActor.
 
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
Finally, the MasterActor counts the number of primes in the given range as the messages of type Number are received. Once the count is equals the initial size/range, it prints out the duration of the process.
 
 ```java 
 if(message instanceof Number){
            Number number=(Number) message;
            computed++;
            if(number.getPrime()){
                primeCount++;
            }

            if(computed==size.getSize()-1){
                completed=System.nanoTime();
                long elapsedTime=completed-started;
                double seconds = (double)elapsedTime / 1000000000.0;
                System.out.println("Computation completed, Prime Numbers="
                +primeCount+" Duration ="+seconds+" seconds.");
            }
        }  
       ```     
The most time consuming part is the verification part. Therefore, we want maximum concurrency fo PrimeVerifierActor. On the reference machine, I had 16 cores, so initialization of the PrimeVerifierActor with the following syntax keeps the CPU busiest and gives best performance (43-46 seconds).

```java
private ActorRef primeVerifierActor=getContext()
            .actorOf(new Props(PrimeVerifierActor.class)
                    .withRouter(new RoundRobinRouter(16)),"prime-verifier"); 
``` 
A none concurrent version of the application with one PrimeVerifierActor, or no actor version, took around 115 to 120 seconds for the same range.   

                    
