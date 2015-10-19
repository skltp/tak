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
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-pubVersion" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="pubVersion.preview.label" /></g:link></li>
			</ul>
		</div>
		<div id="list-pubVersion" class="content scaffold-list" role="main">
			<h1><g:message code="pubVersion.list.label" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="formatVersion" title="${message(code: 'pubVersion.formatVersion.label', default: 'x_Format version')}" />
					
						<g:sortableColumn property="kommentar" title="${message(code: 'pubVersion.kommentar.label', default: 'x_Kommentar')}" />
					
						<g:sortableColumn property="time" title="${message(code: 'pubVersion.time.label', default: 'x_Tid')}" />
					
						<g:sortableColumn property="utforare" title="${message(code: 'pubVersion.utforare.label', default: 'x_Utforare')}" />
						
						<g:sortableColumn property="id" title="${message(code: 'pubVersion.download.label', default: 'x_Laddaner')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${pubVersionInstanceList}" status="i" var="pubVersionInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${pubVersionInstance.id}">${fieldValue(bean: pubVersionInstance, field: "formatVersion")}</g:link></td>
					
						<td>${fieldValue(bean: pubVersionInstance, field: "kommentar")}</td>
					
						<td><g:formatDate date="${pubVersionInstance.time}" /></td>
					
						<td>${fieldValue(bean: pubVersionInstance, field: "utforare")}</td>
						
						<td><a href="_blank"><img src="${resource(dir:'images',file:'download.png')}" alt="Laddaner" /></a></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${pubVersionInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
