package com.count.prime.counter;

import akka.actor.UntypedActor;
import messages.Size;

/**
 * Created by imran on 1/7/17.
 */
public class MapActor extends UntypedActor{

    public void onReceive(Object message) throws Exception {
       if(message instanceof Size){
           Size counter=(Size) message;
           for(Long i=0l;i<counter.getSize();i++){
              getSender().tell(i);
           }
       }else{
           unhandled(message);
       }
    }
}
