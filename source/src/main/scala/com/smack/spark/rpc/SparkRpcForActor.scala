package com.smack.spark.rpc

import akka.actor.{ActorSystem, Props}
import com.smack.spark.rpc.actor.RpcActor
import com.typesafe.config.ConfigFactory

object SparkRpcForActor extends App {
	SparkConfig
	var host = "0.0.0.0"
	if (!args.isEmpty) {
		host = args(0)
	} else {
		System.out.println("need start parameter !")
		System.exit(0)
	}
	System.setProperty("akka.remote.netty.tcp.hostname", host)
	System.setProperty("akka.remote.netty.tcp.port", "12551")
	val config = ConfigFactory.load("akka.conf")
	implicit val system = ActorSystem("RpcForActor", config)
	val actor = system.actorOf(Props[RpcActor], "rpcActor")
	System.out.println(actor)
	System.out.println("Starting Spark Rpc func !")
}