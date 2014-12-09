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

<%@ page import="se.skltp.tak.core.entity.Vagval" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'vagval.label', default: 'Vagval')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="vagval" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-vagval" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list vagval">
			
				<g:if test="${vagvalInstance?.tjanstekontrakt}">
				<li class="fieldcontain">
					<span id="tjanstekontrakt-label" class="property-label"><g:message code="vagval.tjanstekontrakt.label" default="Tjanstekontrakt" /></span>
					<span class="property-value" aria-labelledby="tjanstekontrakt-label"><g:link controller="tjanstekontrakt" action="show" id="${vagvalInstance?.tjanstekontrakt?.id}">${vagvalInstance?.tjanstekontrakt?.encodeAsHTML()}</g:link></span>
				</li>
				</g:if>
				
				<g:if test="${vagvalInstance?.logiskAdress}">
				<li class="fieldcontain">
					<span id="logiskAdress-label" class="property-label"><g:message code="vagval.logiskAdress.label" default="Logisk Adress" /></span>
					<span class="property-value" aria-labelledby="logiskAdress-label"><g:link controller="logiskAdress" action="show" id="${vagvalInstance?.logiskAdress?.id}">${vagvalInstance?.logiskAdress?.encodeAsHTML()}</g:link> - ${vagvalInstance?.logiskAdress?.beskrivning?.encodeAsHTML()}</span>
				</li>
				</g:if>
				
				<g:if test="${vagvalInstance?.anropsAdress?.tjanstekomponent}">
				<li class="fieldcontain">
					<span id="tjansteproducent-label" class="property-label"><g:message code="vagval.tjansteproducent.label" default="Tjansteproducent" /></span>
					<span class="property-value" aria-labelledby="tjansteproducent-label"><g:link controller="tjanstekomponent" action="show" id="${vagvalInstance?.anropsAdress?.tjanstekomponent?.id}">${vagvalInstance?.anropsAdress?.tjanstekomponent?.encodeAsHTML()}</g:link> - ${vagvalInstance?.anropsAdress?.tjanstekomponent?.beskrivning?.encodeAsHTML()}</span>
				</li>
				</g:if>
				
				<g:if test="${vagvalInstance?.anropsAdress}">
				<li class="fieldcontain">
					<span id="anropsAdress-label" class="property-label"><g:message code="vagval.anropsAdress.label" default="Anropsadress" /></span>
					<span class="property-value" aria-labelledby="anropsAdress-label">${vagvalInstance?.anropsAdress?.adress?.encodeAsHTML()} [${vagvalInstance?.anropsAdress?.rivTaProfil?.encodeAsHTML()}]</span>
				</li>
				</g:if>
				
				<g:if test="${vagvalInstance?.fromTidpunkt}">
				<li class="fieldcontain">
					<span id="fromTidpunkt-label" class="property-label"><g:message code="vagval.fromTidpunkt.label" default="From Tidpunkt" /></span>
					<span class="property-value" aria-labelledby="fromTidpunkt-label"><g:formatDate date="${vagvalInstance?.fromTidpunkt}" /></span>
				</li>
				</g:if>

				<g:if test="${vagvalInstance?.tomTidpunkt}">
				<li class="fieldcontain">
					<span id="tomTidpunkt-label" class="property-label"><g:message code="vagval.tomTidpunkt.label" default="Tom Tidpunkt" /></span>
					<span class="property-value" aria-labelledby="tomTidpunkt-label"><g:formatDate date="${vagvalInstance?.tomTidpunkt}" /></span>
				</li>
				</g:if>		
						
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${vagvalInstance?.id}" />
					<g:link class="edit" action="edit" id="${vagvalInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
