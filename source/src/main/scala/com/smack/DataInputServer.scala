package com.smack

import akka.actor.{ActorSystem, DeadLetter, Props}
import akka.event.Logging
import com.smack.actor.{DataInputActor, DeadLetterListener}
import com.typesafe.config.ConfigFactory

object DataInputServer extends App {
	var port = "2551"
	if (!args.isEmpty) port=args(0)
	System.setProperty("akka.remote.netty.tcp.port", port)
	val config = ConfigFactory.load("data-input.conf")
	implicit val system = ActorSystem("DataInputServer", config)
	val log = Logging(system, "")
	
	//define dead letter deal strategy
	val listener = system.actorOf(Props[DeadLetterListener], "deadLetter")
    system.eventStream.subscribe(listener, classOf[DeadLetter])
    
    //define main actor
    system.actorOf(Props(new DataInputActor(config)), "datainput")
	log.info("Starting Data Input Server Ok!")
}