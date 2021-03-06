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

<%@ page import="se.skltp.tak.core.entity.Tjanstekomponent" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'tjanstekomponent.label', default: 'Tjanstekomponent')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="filterpane" />
	</head>
	<body>
		<a href="#list-tjanstekomponent" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-tjanstekomponent" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<th class="rightmostColumn" />
						
						<g:sortableColumn property="id" title="${message(code: 'default.version.label', default: 'x_PV')}" class="rightmostColumn" />
						
						<g:sortableColumn property="hsaId" title="${message(code: 'tjanstekomponent.hsaId.label', default: 'Hsa Id')}" params="${filterParams}" />
					
						<g:sortableColumn property="beskrivning" title="${message(code: 'tjanstekomponent.beskrivning.label', default: 'Beskrivning')}" params="${filterParams}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${tjanstekomponentInstanceList}" status="i" var="tjanstekomponentInstance">
					<g:if test="${!tjanstekomponentInstance.isDeletedInPublishedVersion()}">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
							<td>
								<g:link action="show" id="${tjanstekomponentInstance.id}">Visa  
									<g:if test="${tjanstekomponentInstance.isNewlyCreated()}">
										<img src="${resource(dir:'images',file:'created.png')}" alt="Skapad" />
									</g:if>
									<g:elseif test="${tjanstekomponentInstance.isUpdatedAfterPublishedVersion()}">
										<img src="${resource(dir:'images',file:'updated.png')}" alt="Uppdaterad" />
									</g:elseif>
									<g:elseif test="${tjanstekomponentInstance.getDeleted()}">
										<img src="${resource(dir:'images',file:'trash.png')}" alt="Borttagen" />
									</g:elseif>
								</g:link></td>
							
							<td>${tjanstekomponentInstance.pubVersion}</td>
							
							<td><g:link action="show" id="${tjanstekomponentInstance.id}">${fieldValue(bean: tjanstekomponentInstance, field: "hsaId")}</g:link></td>
						
							<td>${fieldValue(bean: tjanstekomponentInstance, field: "beskrivning")}</td>					
						</tr>
					</g:if><g:else><tr id="${i++}"></tr></g:else>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:if test="${tjanstekomponentInstanceTotal > 10}">
					<filterpane:paginate total="${tjanstekomponentInstanceTotal}" domainBean="se.skltp.tak.core.entity.Tjanstekomponent"/>
				</g:if>
				<filterpane:isFiltered>Ett filter är applicerat!</filterpane:isFiltered>
				<filterpane:isNotFiltered>Inget filter finns!</filterpane:isNotFiltered>
				<filterpane:filterButton text="Filtrera lista" appliedText="Ändra filter"/>
			</div>
			<filterpane:filterPane
				domain="se.skltp.tak.core.entity.Tjanstekomponent"
				excludeProperties="id,version"/>
		</div>
	</body>
</html>
