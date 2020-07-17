package com.smack.cassandra

import scala.concurrent.Future

trait RequestBathStaticsService extends ProductionDatabase {

	def getListByserverName(namespace: String, servername: String, num: Integer): Future[List[RequestBathStatics]] = {
		database.requestBathStaticsModel.getListByCondition(namespace, servername, num)
	}
}

object RequestBathStaticsService extends RequestBathStaticsService with ProductionDatabase
