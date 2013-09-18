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

<%@ page import="se.skl.tp.vagval.admin.core.entity.LogiskAdressat" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'logiskAdressat.label', default: 'LogiskAdressat')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-logiskAdressat" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-logiskAdressat" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list logiskAdressat">
			
				<g:if test="${logiskAdressatInstance?.hsaId}">
				<li class="fieldcontain">
					<span id="hsaId-label" class="property-label"><g:message code="logiskAdressat.hsaId.label" default="Hsa Id" /></span>
					
						<span class="property-value" aria-labelledby="hsaId-label"><g:fieldValue bean="${logiskAdressatInstance}" field="hsaId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressatInstance?.beskrivning}">
				<li class="fieldcontain">
					<span id="beskrivning-label" class="property-label"><g:message code="logiskAdressat.beskrivning.label" default="Beskrivning" /></span>
					
						<span class="property-value" aria-labelledby="beskrivning-label"><g:fieldValue bean="${logiskAdressatInstance}" field="beskrivning"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressatInstance?.anropsbehorigheter}">
				<li class="fieldcontain">
					<span id="anropsbehorigheter-label" class="property-label"><g:message code="logiskAdressat.anropsbehorigheter.label" default="Anropsbehorigheter" /></span>
					
						<g:each in="${logiskAdressatInstance.anropsbehorigheter}" var="a">
						<span class="property-value" aria-labelledby="anropsbehorigheter-label">
							<g:link controller="anropsbehorighet" action="show" id="${a.id}">
								<% 
									def tjanstekonsumentBeskrivning = a?.tjanstekonsument?.beskrivning.size() > 30? a?.tjanstekonsument.beskrivning.substring(0, 30) : a?.tjanstekonsument?.beskrivning
								%>
								${"${a?.tjanstekonsument?.encodeAsHTML()} [${tjanstekonsumentBeskrivning?.encodeAsHTML()}] - ${a?.tjanstekontrakt?.encodeAsHTML()}"}
							</g:link>
						</span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressatInstance?.logiskaAdresser}">
				<li class="fieldcontain">
					<span id="logiskaAdresser-label" class="property-label"><g:message code="logiskAdressat.logiskaAdresser.label" default="Logiska Adresser" /></span>
					
						<g:each in="${logiskAdressatInstance.logiskaAdresser}" var="l">
						<span class="property-value" aria-labelledby="logiskaAdresser-label">
							<g:link controller="logiskAdress" action="show" id="${l.id}">
								<% 
									def tjansteproducentBeskrivning = l?.tjansteproducent?.beskrivning.size() > 30? l?.tjansteprodcuent.beskrivning.substring(0, 30) : l?.tjansteproducent?.beskrivning
								%>
								${"${l?.tjansteproducent?.encodeAsHTML()} [${tjansteproducentBeskrivning?.encodeAsHTML()}] - ${l?.tjanstekontrakt?.encodeAsHTML()}"}
							</g:link>
						</span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${logiskAdressatInstance?.id}" />
					<g:link class="edit" action="edit" id="${logiskAdressatInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
