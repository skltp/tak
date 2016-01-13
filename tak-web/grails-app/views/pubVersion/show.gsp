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

<%@ page import="se.skltp.tak.core.entity.PubVersion" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'pubVersion.label', default: 'PubVersion')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-pubVersion" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-pubVersion" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list pubVersion">
			
				<g:if test="${pubVersionInstance?.formatVersion}">
				<li class="fieldcontain">
					<span id="formatVersion-label" class="property-label"><g:message code="pubVersion.formatVersion.label" /></span>
					
						<span class="property-value" aria-labelledby="formatVersion-label"><g:fieldValue bean="${pubVersionInstance}" field="formatVersion"/></span>					
				</li>
				</g:if>
			
				<g:if test="${pubVersionInstance?.kommentar}">
				<li class="fieldcontain">
					<span id="kommentar-label" class="property-label"><g:message code="pubVersion.kommentar.label" /></span>
					
						<span class="property-value" aria-labelledby="kommentar-label"><g:fieldValue bean="${pubVersionInstance}" field="kommentar"/></span>					
				</li>
				</g:if>
			
				<g:if test="${pubVersionInstance?.time}">
				<li class="fieldcontain">
					<span id="time-label" class="property-label"><g:message code="pubVersion.time.label" /></span>
					
						<span class="property-value" aria-labelledby="time-label"><g:formatDate date="${pubVersionInstance?.time}" /></span>					
				</li>
				</g:if>
			
				<g:if test="${pubVersionInstance?.utforare}">
				<li class="fieldcontain">
					<span id="utforare-label" class="property-label"><g:message code="pubVersion.utforare.label" /></span>
					
						<span class="property-value" aria-labelledby="utforare-label"><g:fieldValue bean="${pubVersionInstance}" field="utforare"/></span>					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${pubVersionInstance?.id}" />
					<g:link class="edit" action="edit" id="${pubVersionInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<%-- 
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					--%>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
