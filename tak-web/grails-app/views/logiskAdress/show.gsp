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
			
				<g:if test="${logiskAdressInstance?.hsaId}">
				<li class="fieldcontain">
					<span id="hsaId-label" class="property-label"><g:message code="logiskAdress.hsaId.label" default="Hsa Id" /></span>
					
						<span class="property-value" aria-labelledby="hsaId-label"><g:fieldValue bean="${logiskAdressInstance}" field="hsaId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.beskrivning}">
				<li class="fieldcontain">
					<span id="beskrivning-label" class="property-label"><g:message code="logiskAdress.beskrivning.label" default="Beskrivning" /></span>
					
						<span class="property-value" aria-labelledby="beskrivning-label"><g:fieldValue bean="${logiskAdressInstance}" field="beskrivning"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.anropsbehorigheter}">
				<li class="fieldcontain">
					<span id="anropsbehorigheter-label" class="property-label"><g:message code="logiskAdress.anropsbehorigheter.label" default="Anropsbehorigheter" /></span>
					
						<g:each in="${logiskAdressInstance.anropsbehorigheter}" var="a">
						<span class="property-value" aria-labelledby="anropsbehorigheter-label">
							<g:link controller="anropsbehorighet" action="show" id="${a.id}">
								<% 
									def tjanstekonsumentBeskrivning = a?.tjanstekonsument?.beskrivning.size() > 30? a?.tjanstekonsument?.beskrivning.substring(0, 30) : a?.tjanstekonsument?.beskrivning
								%>
								${"${a?.tjanstekonsument?.encodeAsHTML()} [${tjanstekonsumentBeskrivning?.encodeAsHTML()}] - ${a?.tjanstekontrakt?.encodeAsHTML()}"}
							</g:link>
						</span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.vagval}">
				<li class="fieldcontain">
					<span id="vagval-label" class="property-label"><g:message code="logiskAdress.vagval.label" default="Vägval" /></span>
					
						<g:each in="${logiskAdressInstance.vagval}" var="l">
						<span class="property-value" aria-labelledby="vagval-label">
							<g:link controller="vagval" action="show" id="${l.id}">
								<% 
									def anropsAdressAdress = l?.anropsAdress?.adress.size() > 30? l?.anropsAdress?.adress.substring(0, 30) : l?.anropsAdress?.adress
								%>
								${"${l?.anropsAdress?.encodeAsHTML()} [${anropsAdressAdress?.encodeAsHTML()}] - ${l?.tjanstekontrakt?.encodeAsHTML()}"}
							</g:link>
						</span>
						</g:each>
					
				</li>
				</g:if>
				
				<g:if test="${logiskAdressInstance?.id}">
					<li class="fieldcontain">
						<span id="uniqueid-label" class="property-label"><g:message code="default.uniqueId.label" /></span>
						<span class="property-value" aria-labelledby="uniqueid-label"><g:fieldValue bean="${logiskAdressInstance}" field="id"/></span>					
					</li>
				</g:if>

				<g:if test="${logiskAdressInstance?.pubVersion}">
					<li class="fieldcontain">
						<span id="pubVersion-label" class="property-label"><g:message code="default.pubVersion.label" /></span>
						<span class="property-value" aria-labelledby="pubVersion-label"><g:fieldValue bean="${logiskAdressInstance}" field="pubVersion"/></span>					
					</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.updatedTime}">
					<li class="fieldcontain">
						<g:if test="${flash.isCreated}">
							<span id="updatedTime-label" class="property-label"><g:message code="default.createdTime.label" /></span>
						</g:if>
						<g:else>
							<span id="updatedTime-label" class="property-label"><g:message code="default.updatedTime.label" /></span>
    					</g:else>
						<span class="property-value" aria-labelledby="updatedTime-label"><g:formatDate date="${logiskAdressInstance?.updatedTime}" /></span>					
					</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.updatedBy}">
					<li class="fieldcontain">
						<g:if test="${flash.isCreated}">
							<span id="updatedBy-label" class="property-label"><g:message code="default.createdBy.label" /></span>
						</g:if>
						<g:else>
							<span id="updatedBy-label" class="property-label"><g:message code="default.updatedBy.label" /></span>
						</g:else>
						<span class="property-value" aria-labelledby="updatedBy-label"><g:fieldValue bean="${logiskAdressInstance}" field="updatedBy"/></span>					
					</li>
				</g:if>
			
				<g:if test="${logiskAdressInstance?.deleted}">
					<li class="fieldcontain">
						<span id="deleted-label" class="property-label"><g:message code="default.deleted.label" /></span>
						<span class="property-value" aria-labelledby="deleted-label"><g:formatBoolean boolean="${logiskAdressInstance?.deleted}" /></span>					
					</li>
				</g:if>
							
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${logiskAdressInstance?.id}" />
					<g:link class="edit" action="edit" id="${logiskAdressInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:if test="${!logiskAdressInstance?.deleted}">
						<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</g:if>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
