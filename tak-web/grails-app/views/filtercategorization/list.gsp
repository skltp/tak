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

<%@ page import="se.skltp.tak.core.entity.Filtercategorization" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'filtercategorization.label', default: 'Filtercategorization')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="filterpane" />
	</head>
	<body>
		<a href="#list-filtercategorization" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-filtercategorization" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<th/>
						<g:sortableColumn property="category" title="${message(code: 'filtercategorization.category.label', default: 'Category')}" params="${filterParams}"/>
						<th><g:message code="filtercategorization.filter.label" default="Filter" /></th>
					</tr>
				</thead>
				<tbody>
				<g:each in="${filtercategorizationInstanceList}" status="i" var="filtercategorizationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${filtercategorizationInstance.id}">Visa</g:link></td>
						<td>${fieldValue(bean: filtercategorizationInstance, field: "category")}</td>
						<td><g:link action="show" id="${filtercategorizationInstance.id}">${fieldValue(bean: filtercategorizationInstance, field: "filter.servicedomain")}</g:link></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<filterpane:paginate total="${filterInstanceTotal}" domainBean="se.skltp.tak.core.entity.Filtercategorization"/>
				<filterpane:isFiltered>Ett filter är applicerat!</filterpane:isFiltered>
				<filterpane:isNotFiltered>Inget filter finns!</filterpane:isNotFiltered>
				<filterpane:filterButton text="Filtrera lista" appliedText="Ändra filter"/>
			</div>
			<filterpane:filterPane
				domain="se.skltp.tak.core.entity.Filtercategorization"
				excludeProperties="id,version"/>
		</div>
	</body>
</html>