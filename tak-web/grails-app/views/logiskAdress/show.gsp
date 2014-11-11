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

<%@ page import="se.skltp.tak.core.entity.LogiskAdress" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'logiskAdress.label', default: 'LogiskAdress')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-logiskAdress" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-logiskAdress" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list logiskAdress">
			
				<g:if test="${logiskAdressInstance?.rivVersion}">
				<li class="fieldcontain">
					<span id="rivVersion-label" class="property-label"><g:message code="logiskAdress.rivVersion.label" default="Riv Version" /></span>
					
						<span class="property-value" aria-labelledby="rivVersion-label"><g:link controller="rivVersion" action="show" id="${logiskAdressInstance?.rivVersion?.id}">${logiskAdressInstance?.rivVersion?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.tjanstekontrakt}">
				<li class="fieldcontain">
					<span id="tjanstekontrakt-label" class="property-label"><g:message code="logiskAdress.tjanstekontrakt.label" default="Tjanstekontrakt" /></span>
					
						<span class="property-value" aria-labelledby="tjanstekontrakt-label"><g:link controller="tjanstekontrakt" action="show" id="${logiskAdressInstance?.tjanstekontrakt?.id}">${logiskAdressInstance?.tjanstekontrakt?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.logiskAdressat}">
				<li class="fieldcontain">
					<span id="logiskAdressat-label" class="property-label"><g:message code="logiskAdress.logiskAdressat.label" default="Logisk Adressat" /></span>
					
						<span class="property-value" aria-labelledby="logiskAdressat-label"><g:link controller="logiskAdressat" action="show" id="${logiskAdressInstance?.logiskAdressat?.id}">${logiskAdressInstance?.logiskAdressat?.encodeAsHTML()}</g:link> - ${logiskAdressInstance?.logiskAdressat?.beskrivning?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.tjansteproducent}">
				<li class="fieldcontain">
					<span id="tjansteproducent-label" class="property-label"><g:message code="logiskAdress.tjansteproducent.label" default="Tjansteproducent" /></span>
					
						<span class="property-value" aria-labelledby="tjansteproducent-label"><g:link controller="tjanstekomponent" action="show" id="${logiskAdressInstance?.tjansteproducent?.id}">${logiskAdressInstance?.tjansteproducent?.encodeAsHTML()}</g:link> - ${logiskAdressInstance?.tjansteproducent?.beskrivning?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.fromTidpunkt}">
				<li class="fieldcontain">
					<span id="fromTidpunkt-label" class="property-label"><g:message code="logiskAdress.fromTidpunkt.label" default="From Tidpunkt" /></span>
					
						<span class="property-value" aria-labelledby="fromTidpunkt-label"><g:formatDate date="${logiskAdressInstance?.fromTidpunkt}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.tomTidpunkt}">
				<li class="fieldcontain">
					<span id="tomTidpunkt-label" class="property-label"><g:message code="logiskAdress.tomTidpunkt.label" default="Tom Tidpunkt" /></span>
					
						<span class="property-value" aria-labelledby="tomTidpunkt-label"><g:formatDate date="${logiskAdressInstance?.tomTidpunkt}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${logiskAdressInstance?.id}" />
					<g:link class="edit" action="edit" id="${logiskAdressInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
