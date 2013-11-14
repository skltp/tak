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

<%@ page import="se.skl.tp.vagval.admin.core.entity.Filter" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'filter.label', default: 'Filter')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-filter" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-filter" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list filter">
			
				<g:if test="${filterInstance?.anropsbehorighet}">
				<li class="fieldcontain">
					<span id="anropsbehorighet-label" class="property-label"><g:message code="filter.anropsbehorighet.label" default="Anropsbehorighet" /></span>
					
						<span class="property-value" aria-labelledby="anropsbehorighet-label"><g:link controller="anropsbehorighet" action="show" id="${filterInstance?.anropsbehorighet?.id}">${filterInstance?.anropsbehorighet?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${filterInstance?.servicedomain}">
				<li class="fieldcontain">
					<span id="servicedomain-label" class="property-label"><g:message code="filter.servicedomain.label" default="Servicedomain" /></span>
					
						<span class="property-value" aria-labelledby="servicedomain-label"><g:fieldValue bean="${filterInstance}" field="servicedomain"/></span>
					
				</li>
				</g:if>
				<g:if test="${filterInstance?.categorization}">
				<li class="fieldcontain">
					<span id="categorization-label" class="property-label"><g:message code="filter.categorization.label" default="Categorization" /></span>
					
						<g:each in="${filterInstance.categorization}" var="c">
						<span class="property-value" aria-labelledby="categorization-label"><g:link controller="filtercategorization" action="show" id="${c.id}">${c?.category?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${filterInstance?.id}" />
					<g:link class="edit" action="edit" id="${filterInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
