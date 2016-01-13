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
<%@ page import="se.skltp.tak.core.entity.PubVersion"%>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'pubVersion.label', default: 'PubVersion')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
		<r:require module="select2" />
	</head>
	<body>
		<a href="#create-pubVersion" class="skip" tabindex="-1">
			<g:message code="default.link.skip.label" default="Skip to content&hellip;" />
		</a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}">
						<g:message code="default.home.label" /></a>
				</li>
				<li><g:link class="list" action="list">
						<g:message code="default.list.label" args="[entityName]" />
					</g:link>
				</li>
			</ul>
		</div>

		<div id="publish-header-list" class="content scaffold-list" role="main">
			<table>
				<thead>
					<tr>
						<th width="2%">x</th>
						<g:sortableColumn property="namn" title="${message(code: 'default.entity.label')}" width="83%"/>
						<th />
						<g:sortableColumn property="user" title="${message(code: 'pubVersion.updatedBy.label')}" />
					</tr>
				</thead>
				<tbody>
					<g:if test="${flash.message}">
						<div class="message" role="status">
							${flash.message}
						</div>
					</g:if>
				</tbody>
			</table>
		</div>
		
		<g:render template="pubcreatedlist" />
		<g:render template="pubupdatedlist" />
		<g:render template="pubdeletedlist" />
	
		<div id="create-pubVersion" class="content scaffold-create" role="main">
			<h1>
				<g:message code="default.create.label" args="[entityName]" />
			</h1>
			
			<g:hasErrors bean="${pubVersionInstance}">
				<ul class="errors" role="alert">
					<g:eachError bean="${pubVersionInstance}" var="error">
						<li
							<g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
								error="${error}" /></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:form action="save">
				<fieldset class="form">
					<g:render template="form" />
				</fieldset>
				<fieldset class="buttons">
					<g:if test="${enablePublish}">
						<g:submitButton name="create" class="save"
							value="${message(code: 'pubVersion.publish', default: 'x_Publicera')}" />
					</g:if>
					<g:else>
						<g:message code="pubVersion.publish.disable" />
					</g:else>
				</fieldset>
			</g:form>
		</div>

	</body>
</html>