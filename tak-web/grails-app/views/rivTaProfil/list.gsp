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

<%@ page import="se.skltp.tak.core.entity.RivTaProfil" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'rivTaProfil.label', default: 'RivTaProfil')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-rivTaProfil" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-rivTaProfil" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<th class="rightmostColumn" />
						
						<g:sortableColumn property="id" title="${message(code: 'default.version.label', default: 'x_PV')}" class="rightmostColumn" />
										
						<g:sortableColumn property="namn" title="${message(code: 'rivTaProfil.namn.label', default: 'Namn')}" />
					
						<g:sortableColumn property="beskrivning" title="${message(code: 'rivTaProfil.beskrivning.label', default: 'Beskrivning')}" />
						
					</tr>
				</thead>
				<tbody>
				<g:each in="${rivTaProfilInstanceList}" status="i" var="rivTaProfilInstance">
					<g:if test="${!rivTaProfilInstance.isDeletedInPublishedVersion()}">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							
							<td>
								<g:link action="show" id="${rivTaProfilInstance.id}">Visa 
									<g:if test="${rivTaProfilInstance.isNewlyCreated()}">
										<img src="${resource(dir:'images',file:'created.png')}" alt="Skapad" />
									</g:if>
									<g:elseif test="${rivTaProfilInstance.isUpdatedAfterPublishedVersion()}">
										<img src="${resource(dir:'images',file:'updated.png')}" alt="Uppdaterad" />
									</g:elseif>
									<g:elseif test="${rivTaProfilInstance.getDeleted()}">
										<img src="${resource(dir:'images',file:'trash.png')}" alt="Borttagen" />
									</g:elseif>
									
								</g:link>
							</td>
							
							<td>${fieldValue(bean: rivTaProfilInstance, field: "pubVersion")}</td>
						
							<td><g:link action="show" id="${rivTaProfilInstance.id}">${fieldValue(bean: rivTaProfilInstance, field: "namn")}</g:link></td>
						
							<td>${fieldValue(bean: rivTaProfilInstance, field: "beskrivning")}</td>
							
						</tr>
					</g:if><g:else><tr id="${i++}"></tr></g:else>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${rivTaProfilInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
