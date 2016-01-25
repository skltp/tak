/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.tak

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import se.skltp.tak.utils.Conf
import se.skltp.tak.utils.Headers

object Scenarios {

    val rampUpTimeSecs = 10
    val minWaitMs      = 500 milliseconds
    val maxWaitMs      = 1500 milliseconds

	/*
	 *	PingForConfigurationResponse
     */	
	val scn_PingOkHttp = scenario("Ping OK http scenario")
      .during(Conf.testTimeSecs) {     
        exec(
          http("Ping")
            .post("/tak-services/itintegration/monitoring/pingForConfiguration/1/rivtabp21")
            .headers(Headers.pingHttp_header)
            .body(RawFileBody("data/Ping_OK.xml")).asXML
            .check(status.is(200))
            .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
            .check(xpath("//pr:PingForConfigurationResponse", List("pr" -> "urn:riv:itintegration:monitoring:PingForConfigurationResponder:1")).count.is(1))
          )
        .pause(minWaitMs, maxWaitMs)
    }
	
	// GetLogicalAddressessByServiceContract
	val scn_GetLogicalAddressessByServiceContractHttp = scenario("GetLogicalAddressessByServiceContract OK")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("GetLogicalAddressessByServiceContract")
	        .post("/tak-services/GetLogicalAddresseesByServiceContract/v2")
			 .headers(Headers.getLogicalAddressessByServiceContract_header)
		     .body(RawFileBody("data/GetLogicalAddressessByServiceContract_5565594230_ProcessNotification_Mock.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:GetLogicalAddresseesByServiceContractResponse", List("pr" -> "urn:riv:infrastructure:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:2")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// GetSupportedServiceContracts v1
	val scn_GetSupportedServiceContractsHttp_v1 = scenario("GetSupportedServiceContracts v1 OK")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("GetSupportedServiceContracts v1")
	        .post("/tak-services/GetSupportedServiceContracts")
			 .headers(Headers.getSupportedServiceContracts_header_v1)
		     .body(RawFileBody("data/GetSupportedServiceContract_v1_SE5565594230-B9P_Mock.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:GetSupportedServiceContractsResponse", List("pr" -> 
			 "urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	// GetSupportedServiceContracts v2
	val scn_GetSupportedServiceContractsHttp_v2 = scenario("GetSupportedServiceContracts v2 OK")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("GetSupportedServiceContracts/v2 v2")
	        .post("/tak-services/GetSupportedServiceContracts/v2")
			 .headers(Headers.getSupportedServiceContracts_header_v2)
		     .body(RawFileBody("data/GetSupportedServiceContract_v2_SE5565594230-B9P_Mock.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:GetSupportedServiceContractsResponse", List("pr" -> 
			 "urn:riv:infrastructure:itintegration:registry:GetSupportedServiceContractsResponder:2")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	val scn_HamtaAllaAnropsBehorigheter = scenario("hamtaAllaAnropsBehorigheter OK")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("hamtaAllaAnropsBehorigheter")
	        .post("/tak-services/SokVagvalsInfo/v2")
			 .headers(Headers.hamtaAllaAnropsBehorigheter_header)
		     .body(RawFileBody("data/SokVagvalsInfo_hamtaAllaAnropsBehorigheter.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:hamtaAllaAnropsBehorigheterResponse", List("pr" -> 
			 "urn:skl:tp:vagvalsinfo:v2")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	val scn_HamtaAllaVirtualiseringar = scenario("hamtaAllaVirtualiseringar OK")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("hamtaAllaVirtualiseringar")
	        .post("/tak-services/SokVagvalsInfo/v2")
			 .headers(Headers.hamtaAllaVirtualiseringar_header)
		     .body(RawFileBody("data/SokVagvalsInfo_hamtaAllaVirtualiseringar.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:hamtaAllaVirtualiseringarResponse", List("pr" -> 
			 "urn:skl:tp:vagvalsinfo:v2")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	val scn_HamtaAllaTjanstekomponenter = scenario("hamtaAllaTjanstekomponenter OK")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("hamtaAllaTjanstekomponenter")
	        .post("/tak-services/SokVagvalsInfo/v2")
			 .headers(Headers.hamtaAllaTjanstekomponenter_header)
		     .body(RawFileBody("data/SokVagvalsInfo_hamtaAllaTjanstekomponenter.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:hamtaAllaTjanstekomponenterResponse", List("pr" -> 
			 "urn:skl:tp:vagvalsinfo:v2")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
	val scn_HamtaAllaTjanstekontrakt = scenario("hamtaAllaTjanstekontrakt OK")
	  .during(Conf.testTimeSecs) { 		
	    exec(
	      http("hamtaAllaTjanstekontrakt")
	        .post("/tak-services/SokVagvalsInfo/v2")
			 .headers(Headers.hamtaAllaTjanstekontrakt_header)
		     .body(RawFileBody("data/SokVagvalsInfo_hamtaAllaTjanstekontrakt.xml")).asXML
	  		 .check(status.is(200))
	         .check(xpath("soap:Envelope", List("soap" -> "http://schemas.xmlsoap.org/soap/envelope/")).exists)
	         .check(xpath("//pr:hamtaAllaTjanstekontraktResponse", List("pr" -> 
			 "urn:skl:tp:vagvalsinfo:v2")).count.is(1))
	      )
	    .pause(minWaitMs, maxWaitMs)
	}
	
}