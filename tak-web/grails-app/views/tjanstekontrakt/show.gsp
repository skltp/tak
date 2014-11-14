<%--

    Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
    					<http://cehis.se/>

    This file is part of SKLTP.

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

--%>

<%@ page import="se.skltp.tak.core.entity.Tjanstekontrakt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-tjanstekontrakt" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-tjanstekontrakt" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list tjanstekontrakt">
			
				<g:if test="${tjanstekontraktInstance?.namnrymd}">
				<li class="fieldcontain">
					<span id="namnrymd-label" class="property-label"><g:message code="tjanstekontrakt.namnrymd.label" default="Namnrymd" /></span>
					
						<span class="property-value" aria-labelledby="namnrymd-label"><g:fieldValue bean="${tjanstekontraktInstance}" field="namnrymd"/></span>
					
				</li>
				</g:if>
				
				<li class="fieldcontain">
					<span id="majorVersion-label" class="property-label"><g:message code="tjanstekontrakt.majorVersion.label" default="Major version" /></span>
					<span class="property-value" aria-labelledby="majorVersion-label"><g:fieldValue bean="${tjanstekontraktInstance}" field="majorVersion"/></span>
					<span id="minorVersion-label" class="property-label"><g:message code="tjanstekontrakt.minorVersion.label" default="Minor version" /></span>
					<span class="property-value" aria-labelledby="minorVersion-label"><g:fieldValue bean="${tjanstekontraktInstance}" field="minorVersion"/></span>
				</li>
			
				<g:if test="${tjanstekontraktInstance?.beskrivning}">
				<li class="fieldcontain">
					<span id="beskrivning-label" class="property-label"><g:message code="tjanstekontrakt.beskrivning.label" default="Beskrivning" /></span>
					
						<span class="property-value" aria-labelledby="beskrivning-label"><g:fieldValue bean="${tjanstekontraktInstance}" field="beskrivning"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${tjanstekontraktInstance?.anropsbehorigheter}">
				<li class="fieldcontain">
					<span id="anropsbehorigheter-label" class="property-label"><g:message code="tjanstekontrakt.anropsbehorigheter.label" default="Anropsbehorigheter" /></span>
					
						<g:each in="${tjanstekontraktInstance.anropsbehorigheter}" var="a">
						<span class="property-value" aria-labelledby="anropsbehorigheter-label"><g:link controller="anropsbehorighet" action="show" id="${a.id}">
							<% 
								def logiskAdressBeskrivning = a?.logiskAdress?.beskrivning?.size() > 30? a?.logiskAdress?.beskrivning.substring(0, 30) : a?.logiskAdress?.beskrivning
								def tjansteKonsumentBeskrivning = a?.tjanstekonsument?.beskrivning.size() > 30? a?.tjanstekonsument?.beskrivning.substring(0, 30) : a?.tjanstekonsument?.beskrivning
							%>
							${"${a?.tjanstekonsument?.encodeAsHTML()} [${tjansteKonsumentBeskrivning?.encodeAsHTML()}] - ${a?.logiskAdress?.encodeAsHTML()} [${logiskAdressBeskrivning?.encodeAsHTML()}"}]</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${tjanstekontraktInstance?.vagval}">
				<li class="fieldcontain">
					<span id="logiskaAdresser-label" class="property-label"><g:message code="tjanstekontrakt.logiskaAdresser.label" default="Logiska Adresser" /></span>
					
						<g:each in="${tjanstekontraktInstance.vagval}" var="l">
						<span class="property-value" aria-labelledby="logiskaAdresser-label">
						
						<g:link controller="vagval" action="show" id="${l.id}">
						<% 
								def logiskAdressBeskrivning = l?.logiskAdress?.beskrivning?.size() > 30? l?.logiskAdress?.beskrivning.substring(0, 30) : l?.logiskAdress?.beskrivning
								def anropsAdressAdress = l?.anropsAdress?.adress.size() > 30? l?.anropsAdress?.adress.substring(0, 30) : l?.anropsAdress?.adress
							%>
						${"${l?.logiskAdress?.encodeAsHTML()} [${logiskAdressBeskrivning?.encodeAsHTML()}] - ${l?.anropsAdress?.encodeAsHTML()} [${anropsAdressAdress?.encodeAsHTML()}"}]
						</g:link>
						
						</span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${tjanstekontraktInstance?.id}" />
					<g:link class="edit" action="edit" id="${tjanstekontraktInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
