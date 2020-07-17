package com.smack.cassandra

import scala.concurrent.Future

trait EventService extends ProductionDatabase {

	def getListByserverName(namespace: String, serverName: String, num: Integer): Future[List[Event]] = {
		database.eventModel.getListByCondition(namespace, serverName, num)
	}
}

object EventService extends EventService with ProductionDatabase
