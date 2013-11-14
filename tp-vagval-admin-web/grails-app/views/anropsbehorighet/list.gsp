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

<%@ page import="se.skl.tp.vagval.admin.core.entity.Anropsbehorighet" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="filterpane" />
	</head>
	<body>
		<a href="#list-anropsbehorighet" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-anropsbehorighet" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<th/>
						
						<g:sortableColumn property="integrationsavtal" title="${message(code: 'anropsbehorighet.integrationsavtal.label', default: 'Integrationsavtal')}" params="${filterParams}" />
						
						<g:sortableColumn property="tjanstekonsument" title="${message(code: 'anropsbehorighet.tjanstekonsument.label', default: 'Tjanstekonsument')}" params="${filterParams}" />
					
						<g:sortableColumn property="tjanstekontrakt" title="${message(code: 'anropsbehorighet.tjanstekontrakt.label', default: 'Tjanstekontrakt')}" params="${filterParams}" />

						<g:sortableColumn property="logiskAdressat" title="${message(code: 'anropsbehorighet.logiskAdressat.label', default: 'Logisk Adressat')}" params="${filterParams}" />					
					
						<g:sortableColumn property="fromTidpunkt" title="${message(code: 'anropsbehorighet.fromTidpunkt.label', default: 'From Tidpunkt')}" params="${filterParams}"/>
					
						<g:sortableColumn property="tomTidpunkt" title="${message(code: 'anropsbehorighet.tomTidpunkt.label', default: 'Tom Tidpunkt')}" params="${filterParams}"/>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${anropsbehorighetInstanceList}" status="i" var="anropsbehorighetInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${anropsbehorighetInstance.id}">Visa</g:link></td>
						
						<td>${fieldValue(bean: anropsbehorighetInstance, field: "integrationsavtal")}</td>
					
						<td><g:link action="show" controller="tjanstekomponent" id="${anropsbehorighetInstance.tjanstekonsument.id}">${fieldValue(bean: anropsbehorighetInstance, field: "tjanstekonsument")}</g:link></td>
					
						<td><g:link action="show" controller="tjanstekontrakt" id="${anropsbehorighetInstance.tjanstekontrakt.id}">${fieldValue(bean: anropsbehorighetInstance, field: "tjanstekontrakt")}</g:link></td>
					
						<td><g:link action="show" controller="logiskAdressat" id="${anropsbehorighetInstance.logiskAdressat.id}">${fieldValue(bean: anropsbehorighetInstance, field: "logiskAdressat")}</g:link></td>
					
						<td><g:formatDate date="${anropsbehorighetInstance.fromTidpunkt}" /></td>
					
						<td><g:formatDate date="${anropsbehorighetInstance.tomTidpunkt}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<filterpane:paginate total="${anropsbehorighetInstanceTotal}" domainBean="se.skl.tp.vagval.admin.core.entity.Anropsbehorighet"/>
				<filterpane:isFiltered>Ett filter är applicerat!</filterpane:isFiltered>
				<filterpane:isNotFiltered>Inget filter finns!</filterpane:isNotFiltered>
				<filterpane:filterButton text="Filtrera lista" appliedText="Ändra filter"/>
			</div>
			<filterpane:filterPane
				domain="se.skl.tp.vagval.admin.core.entity.Anropsbehorighet"
				associatedProperties="tjanstekonsument.hsaId,tjanstekontrakt.namnrymd,logiskAdressat.hsaId"
				excludeProperties="id,version"/>
		</div>
	</body>
</html>
