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
package se.skltp.tak.utils

object Headers {

	// HTTP Headers
	val pingHttp_header = Map(
      "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:itintegration:monitoring:PingForConfigurationResponder:1:PingForConfiguration",
	  "x-vp-sender-id" -> "tp",
	  "x-vp-instance-id" -> "THIS_VP_INSTANCE_ID",
	  "Keep-Alive" -> "115")

	val getLogicalAddressessByServiceContract_header = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:infrastructure:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:2",
	  "Keep-Alive" -> "115")
	  
	val getSupportedServiceContracts_header_v1 = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1",
	  "Keep-Alive" -> "115")
	  
	val getSupportedServiceContracts_header_v2 = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:riv:infrastructure:itintegration:registry:GetSupportedServiceContractsResponder:2",
	  "Keep-Alive" -> "115")
	  
	val hamtaAllaAnropsBehorigheter_header = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:skl:tp:vagvalsinfo:v2",
	  "Keep-Alive" -> "115")
	  
	val hamtaAllaVirtualiseringar_header = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:skl:tp:vagvalsinfo:v2",
	  "Keep-Alive" -> "115")
	
	val hamtaAllaTjanstekontrakt_header = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:skl:tp:vagvalsinfo:v2",
	  "Keep-Alive" -> "115")
	  
	val hamtaAllaTjanstekomponenter_header = Map(
	  "Accept-Encoding" -> "gzip,deflate",
	  "Content-Type" -> "text/xml;charset=UTF-8",
	  "SOAPAction" -> "urn:skl:tp:vagvalsinfo:v2",
	  "Keep-Alive" -> "115")
  }