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

<%@ page import="se.skltp.tak.core.entity.Filter" %>
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
					
						<span class="property-value" aria-labelledby="anropsbehorighet-label">
							<tmpl:/chooseEntityIconCRUD entity="${filterInstance.anropsbehorighet}" />
							<g:link controller="anropsbehorighet" action="show" id="${filterInstance?.anropsbehorighet?.id}">${filterInstance?.anropsbehorighet?.encodeAsHTML()}</g:link>
						</span>
					
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
						<g:if test="${!c.isDeletedInPublishedVersion()}">
							<span class="property-value" aria-labelledby="filter-label">
								<tmpl:/chooseEntityIconCRUD entity="${c}" />
								<g:link controller="filtercategorization" action="show" id="${c.id}">${c?.id?.encodeAsHTML()} - ${c?.category?.encodeAsHTML()}</g:link>
							</span>
						</g:if>
					</g:each>
					
				</li>
				</g:if>
				
				<g:if test="${filterInstance?.id}">
					<li class="fieldcontain">
						<span id="uniqueid-label" class="property-label"><g:message code="default.uniqueId.label" /></span>
						<span class="property-value" aria-labelledby="uniqueid-label"><g:fieldValue bean="${filterInstance}" field="id"/></span>					
					</li>
				</g:if>
			
				<g:if test="${filterInstance?.pubVersion}">
					<li class="fieldcontain">
						<span id="pubVersion-label" class="property-label"><g:message code="default.pubVersion.label" /></span>
						<span class="property-value" aria-labelledby="pubVersion-label"><g:fieldValue bean="${filterInstance}" field="pubVersion"/></span>					
					</li>
				</g:if>
			
				<g:if test="${filterInstance?.updatedTime}">
					<li class="fieldcontain">
						<g:if test="${flash.isCreated}">
							<span id="updatedTime-label" class="property-label"><g:message code="default.createdTime.label" /></span>
						</g:if>
						<g:else>
							<span id="updatedTime-label" class="property-label"><g:message code="default.updatedTime.label" /></span>
    					</g:else>
						<span class="property-value" aria-labelledby="updatedTime-label"><g:formatDate date="${filterInstance?.updatedTime}" /></span>					
					</li>
				</g:if>
			
				<g:if test="${filterInstance?.updatedBy}">
					<li class="fieldcontain">
						<g:if test="${flash.isCreated}">
							<span id="updatedBy-label" class="property-label"><g:message code="default.createdBy.label" /></span>
						</g:if>
						<g:else>
							<span id="updatedBy-label" class="property-label"><g:message code="default.updatedBy.label" /></span>
						</g:else>
						<span class="property-value" aria-labelledby="updatedBy-label"><g:fieldValue bean="${filterInstance}" field="updatedBy"/></span>					
					</li>
				</g:if>
			
				<g:if test="${filterInstance?.deleted}">
					<li class="fieldcontain">
						<span id="deleted-label" class="property-label"><g:message code="default.deleted.label" /></span>
						<span class="property-value" aria-labelledby="deleted-label"><g:formatBoolean boolean="${filterInstance?.deleted}" /></span>					
					</li>
				</g:if>
							
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${filterInstance?.id}" />
					<g:link class="edit" action="edit" id="${filterInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:if test="${!filterInstance?.deleted}">
						<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</g:if>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
