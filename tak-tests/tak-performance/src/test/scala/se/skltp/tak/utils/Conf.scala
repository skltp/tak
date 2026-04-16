package se.skltp.tak.utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

object Conf {
	var noOfUsers = System.getProperty("noOfUsers", "10").toInt
	val baseUrl = System.getProperty("baseUrl", "NOT_CONFIGURED")
    var httpConf = http.baseURL(baseUrl)	
	var testTimeSecs = System.getProperty("testTimeSecs", "30").toInt
}