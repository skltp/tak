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