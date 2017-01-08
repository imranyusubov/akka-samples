<b>PrimeCounter</b><br/>
PrimeCounter is an Akka actor based concurrent application that computes the number of primes between 0 and the given number. Figuring out if a number is prime or not a time consuming effort; hence, it is a good candidate for concurrency. The application demonstrates simple usage of Akka actor model.

MasterActor, is the root actor (supervisor) that rotes messages to child actors.
MapActor, loops through 0 to the given number and creates messages of type Long and passes them back to MasterActor.
MasterActor, forwards the messages to PrimeVerifierActor which verifies if the given number is prime or not. This is where the computation is done, and we wish to achieve maximum concurrency. Once, PrimeVerifierActor completes the computation, it generates a new message of type Number and passes it back to MasterActor. Finally, the MasterActor counts the number of primes in the given range as the messages of type Number are received.
