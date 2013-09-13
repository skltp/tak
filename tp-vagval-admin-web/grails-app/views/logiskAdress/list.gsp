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

<%@ page import="se.skl.tp.vagval.admin.core.entity.LogiskAdress" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'logiskAdress.label', default: 'LogiskAdress')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="filterpane" />
	</head>
	<body>
		<a href="#list-logiskAdress" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-logiskAdress" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<th><g:message code="logiskAdress.rivVersion.label" default="Riv Version" /></th>
					
					    <g:sortableColumn property="tjanstekontrakt.namnrymd" title="Tjanstekontrakt" params="${filterParams}"/>
					    
						<th><g:message code="logiskAdress.logiskAdressat.label" default="Logisk Adressat" /></th>
					
						<th><g:message code="logiskAdress.tjansteproducent.label" default="Tjansteproducent" /></th>
					
						<g:sortableColumn property="fromTidpunkt" title="${message(code: 'logiskAdress.fromTidpunkt.label', default: 'From Tidpunkt')}" />
					
						<g:sortableColumn property="tomTidpunkt" title="${message(code: 'logiskAdress.tomTidpunkt.label', default: 'Tom Tidpunkt')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${logiskAdressInstanceList}" status="i" var="logiskAdressInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${logiskAdressInstance.id}">${fieldValue(bean: logiskAdressInstance, field: "rivVersion")}</g:link></td>
					
						<td>${fieldValue(bean: logiskAdressInstance, field: "tjanstekontrakt")}</td>
					
						<td>${fieldValue(bean: logiskAdressInstance, field: "logiskAdressat")}</td>
					
						<td>${fieldValue(bean: logiskAdressInstance, field: "tjansteproducent")}</td>
					
						<td><g:formatDate date="${logiskAdressInstance.fromTidpunkt}" /></td>
					
						<td><g:formatDate date="${logiskAdressInstance.tomTidpunkt}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${logiskAdressInstanceTotal}" />
				<filterpane:filterButton text="Filtrera lista" appliedText="Ändra filter"/>
			</div>
			<filterpane:filterPane
				domain="se.skl.tp.vagval.admin.core.entity.LogiskAdress"
				associatedProperties="rivVersion.namn,tjanstekontrakt.namnrymd,logiskAdressat.hsaId,tjansteproducent.hsaId"
				excludeProperties="id"/>
		</div>
	</body>
</html>
