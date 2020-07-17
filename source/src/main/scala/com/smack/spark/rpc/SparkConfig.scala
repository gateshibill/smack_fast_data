package com.smack.spark.rpc

import org.apache.spark.{SparkConf, SparkContext}

object SparkConfig {
	val conf = new SparkConf().setAppName("CassandraConsumer")
	conf.set("spark.cassandra.connection.host", "cassandra")
	val sc = new SparkContext(conf)
}