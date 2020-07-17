package com.smack.cassandra

import com.smack.util.JsonUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

object TestCassandra extends App {
	
	val result = EventService.getListByserverName("test","a1",10)
	result onComplete {
		case Success(eventList) => print(JsonUtil.toJson(eventList))
		case _ => print("error")
	}
}