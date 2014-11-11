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

<%@ page import="se.skltp.tak.core.entity.Anropsbehorighet" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-anropsbehorighet" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-anropsbehorighet" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list anropsbehorighet">
			
				<g:if test="${anropsbehorighetInstance?.integrationsavtal}">
				<li class="fieldcontain">
					<span id="integrationsavtal-label" class="property-label"><g:message code="anropsbehorighet.integrationsavtal.label" default="Integrationsavtal" /></span>
					
						<span class="property-value" aria-labelledby="integrationsavtal-label"><g:fieldValue bean="${anropsbehorighetInstance}" field="integrationsavtal"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsbehorighetInstance?.tjanstekonsument}">
				<li class="fieldcontain">
					<span id="tjanstekonsument-label" class="property-label"><g:message code="anropsbehorighet.tjanstekonsument.label" default="Tjanstekonsument" /></span>
					
						<span class="property-value" aria-labelledby="tjanstekonsument-label"><g:link controller="tjanstekomponent" action="show" id="${anropsbehorighetInstance?.tjanstekonsument?.id}">${anropsbehorighetInstance?.tjanstekonsument?.encodeAsHTML()}</g:link> - ${anropsbehorighetInstance?.tjanstekonsument?.beskrivning?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsbehorighetInstance?.tjanstekontrakt}">
				<li class="fieldcontain">
					<span id="tjanstekontrakt-label" class="property-label"><g:message code="anropsbehorighet.tjanstekontrakt.label" default="Tjanstekontrakt" /></span>
					
						<span class="property-value" aria-labelledby="tjanstekontrakt-label"><g:link controller="tjanstekontrakt" action="show" id="${anropsbehorighetInstance?.tjanstekontrakt?.id}">${anropsbehorighetInstance?.tjanstekontrakt?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsbehorighetInstance?.logiskAdressat}">
				<li class="fieldcontain">
					<span id="logiskAdressat-label" class="property-label"><g:message code="anropsbehorighet.logiskAdressat.label" default="Logisk Adressat" /></span>
					
						<span class="property-value" aria-labelledby="logiskAdressat-label"><g:link controller="logiskAdressat" action="show" id="${anropsbehorighetInstance?.logiskAdressat?.id}">${anropsbehorighetInstance?.logiskAdressat?.encodeAsHTML()}</g:link> - ${anropsbehorighetInstance?.logiskAdressat?.beskrivning?.encodeAsHTML()}</span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsbehorighetInstance?.fromTidpunkt}">
				<li class="fieldcontain">
					<span id="fromTidpunkt-label" class="property-label"><g:message code="anropsbehorighet.fromTidpunkt.label" default="From Tidpunkt" /></span>
					
						<span class="property-value" aria-labelledby="fromTidpunkt-label"><g:formatDate date="${anropsbehorighetInstance?.fromTidpunkt}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsbehorighetInstance?.tomTidpunkt}">
				<li class="fieldcontain">
					<span id="tomTidpunkt-label" class="property-label"><g:message code="anropsbehorighet.tomTidpunkt.label" default="Tom Tidpunkt" /></span>
					
						<span class="property-value" aria-labelledby="tomTidpunkt-label"><g:formatDate date="${anropsbehorighetInstance?.tomTidpunkt}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsbehorighetInstance?.filter}">
				<li class="fieldcontain"><span id="filter-label"
							class="property-label"><g:message
									code="anropsbehorighet.filter.label"
									default="Filter" /></span> <g:each
								in="${anropsbehorighetInstance.filter}" var="f">
								<span class="property-value"
									aria-labelledby="filter-label"><g:link
										controller="filter" action="show" id="${f.id}">
										${f?.servicedomain?.encodeAsHTML()}
									</g:link></span>
							</g:each></li>
				</g:if>
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${anropsbehorighetInstance?.id}" />
					<g:link class="edit" action="edit" id="${anropsbehorighetInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
