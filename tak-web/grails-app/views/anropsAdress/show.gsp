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

<%@ page import="se.skltp.tak.core.entity.AnropsAdress" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anropsAdress.label', default: 'AnropsAdress')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-anropsAdress" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-anropsAdress" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list anropsAdress">
			
				<g:if test="${anropsAdressInstance?.adress}">
					<li class="fieldcontain">
						<span id="adress-label" class="property-label"><g:message code="default.adress.label" default="Adress" /></span>
						<span class="property-value" aria-labelledby="adress-label" style="word-wrap:break-word; max-width:800px;">
						  <g:fieldValue bean="${anropsAdressInstance}" field="adress"/>
						</span>
					</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.tjanstekomponent}">
				<li class="fieldcontain">
					<span id="tjanstekomponent-label" class="property-label"><g:message code="default.tjanstekomponent.label" default="Tjanstekomponent" /></span>
					
						<span class="property-value" aria-labelledby="tjanstekomponent-label"><g:link controller="tjanstekomponent" action="show" id="${anropsAdressInstance?.tjanstekomponent?.id}">${anropsAdressInstance?.tjanstekomponent?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.rivTaProfil}">
				<li class="fieldcontain">
					<span id="rivTaProfil-label" class="property-label"><g:message code="default.rivTaProfil.label" default="Riv Ta Profil" /></span>
					
						<span class="property-value" aria-labelledby="rivTaProfil-label"><g:link controller="rivTaProfil" action="show" id="${anropsAdressInstance?.rivTaProfil?.id}">${anropsAdressInstance?.rivTaProfil?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.vagVal}">
				<li class="fieldcontain">
					<span id="vagVal-label" class="property-label"><g:message code="default.vagVal.label" default="Vag Val" /></span>
					
						<g:each in="${anropsAdressInstance.vagVal}" var="v">
						<span class="property-value" aria-labelledby="vagVal-label"><g:link controller="vagval" action="show" id="${v.id}">${v?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>

				<g:if test="${anropsAdressInstance?.pubVersion}">
					<li class="fieldcontain">
						<span id="pubVersion-label" class="property-label"><g:message code="default.pubVersion.label" /></span>
						<span class="property-value" aria-labelledby="pubVersion-label"><g:fieldValue bean="${anropsAdressInstance}" field="pubVersion"/></span>					
					</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.updatedTime}">
					<li class="fieldcontain">
						<g:if test="${flash.isCreated}">
							<span id="updatedTime-label" class="property-label"><g:message code="default.createdTime.label" /></span>
						</g:if>
						<g:else>
							<span id="updatedTime-label" class="property-label"><g:message code="default.updatedTime.label" /></span>
    					</g:else>
						<span class="property-value" aria-labelledby="updatedTime-label"><g:formatDate date="${anropsAdressInstance?.updatedTime}" /></span>					
					</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.updatedBy}">
					<li class="fieldcontain">
						<g:if test="${flash.isCreated}">
							<span id="updatedBy-label" class="property-label"><g:message code="default.createdBy.label" /></span>
						</g:if>
						<g:else>
							<span id="updatedBy-label" class="property-label"><g:message code="default.updatedBy.label" /></span>
						</g:else>
						<span class="property-value" aria-labelledby="updatedBy-label"><g:fieldValue bean="${anropsAdressInstance}" field="updatedBy"/></span>					
					</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.deleted}">
					<li class="fieldcontain">
						<span id="deleted-label" class="property-label"><g:message code="default.deleted.label" /></span>
						<span class="property-value" aria-labelledby="deleted-label"><g:formatBoolean boolean="${anropsAdressInstance?.deleted}" /></span>					
					</li>
				</g:if>
							
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${anropsAdressInstance?.id}" />
					<g:link class="edit" action="edit" id="${anropsAdressInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
