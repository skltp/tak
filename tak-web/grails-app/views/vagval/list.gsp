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

<%@ page import="se.skltp.tak.core.entity.Vagval" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'vagval.label', default: 'Vagval')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="filterpane" />
	</head>
	<body>
		<a href="#list-vagval" class="skip" tabindex="-1">
          <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
        </a>
  
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li>
                  <g:link class="create" action="create">
                    <g:message code="default.new.label" args="[entityName]" />
                  </g:link>
                </li>
                <shiro:hasRole name="Admin">
                  <li>
                    <g:link class="create" action="bulkadd">
                      <g:message code="bulkadd.label" />
                    </g:link>
                  </li>
                </shiro:hasRole>
			</ul>
		</div>
  
		<div id="list-vagval" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					    <th class="rightmostColumn" />
						
						<g:sortableColumn property="pubVersion" title="${message(code: 'default.version.label', default: 'x_PV')}" class="rightmostColumn" />
						
						<g:sortableColumn property="rivTaProfil" title="${message(code: 'vagval.rivTaProfil.label', default: 'Riv TA Version')}" params="${filterParams}" />
					
					    <g:sortableColumn property="tjanstekontrakt" title="${message(code: 'vagval.tjanstekontrakt.label', default: 'Tjanstekontrakt')}" params="${filterParams}" />
					    
					    <g:sortableColumn property="logiskAdress" title="${message(code: 'vagval.logiskAdress.label', default: 'Logisk Adress')}" params="${filterParams}" />
					    
					    <g:sortableColumn property="anropsAdress" title="${message(code: 'vagval.anropsAdress.label', default: 'AnropsAdress')}" params="${filterParams}" />
					
						<g:sortableColumn property="fromTidpunkt" title="${message(code: 'vagval.fromTidpunkt.label', default: 'From Tidpunkt')}" />
					
						<g:sortableColumn property="tomTidpunkt" title="${message(code: 'vagval.tomTidpunkt.label', default: 'Tom Tidpunkt')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${vagvalInstanceList}" status="i" var="vagvalInstance">
					<g:if test="${!vagvalInstance.isDeletedInPublishedVersion()}">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						
							<td>
								<g:link action="show" id="${vagvalInstance.id}">Visa 
									<g:if test="${vagvalInstance.isNewlyCreated()}">
										<img src="${resource(dir:'images',file:'created.png')}" alt="Skapad" />
									</g:if>
									<g:elseif test="${vagvalInstance.isUpdatedAfterPublishedVersion()}">
										<img src="${resource(dir:'images',file:'updated.png')}" alt="Uppdaterad" />
									</g:elseif>
									<g:elseif test="${vagvalInstance.getDeleted()}">
										<img src="${resource(dir:'images',file:'trash.png')}" alt="Borttagen" />
									</g:elseif>
								</g:link></td>
							
							<td>${vagvalInstance.pubVersion}</td>
							
							<td>${fieldValue(bean: vagvalInstance, field: "anropsAdress.rivTaProfil.namn")}</td>
						
							<td><g:link action="show" controller="tjanstekontrakt" id="${vagvalInstance.tjanstekontrakt.id}">${fieldValue(bean: vagvalInstance, field: "tjanstekontrakt")}</g:link></td>
						
							<td><g:link action="show" controller="logiskAdress" id="${vagvalInstance.logiskAdress.id}">${fieldValue(bean: vagvalInstance, field: "logiskAdress")}</g:link></td>
						
							<td><g:link action="show" controller="anropsAdress" id="${vagvalInstance.anropsAdress.tjanstekomponent.id}">
								${fieldValue(bean: vagvalInstance, field: "anropsAdress.adress")}
								</g:link>
							</td>
						
							<td><g:formatDate date="${vagvalInstance.fromTidpunkt}" /></td>
						
							<td><g:formatDate date="${vagvalInstance.tomTidpunkt}" /></td>
						
						</tr>
					</g:if><g:else><tr id="${i++}"></tr></g:else>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:if test="${vagvalInstanceTotal > 10}">
					<filterpane:paginate total="${vagvalInstanceTotal}" domainBean="se.skltp.tak.core.entity.Vagval"/>
				</g:if>
				<filterpane:isFiltered>Ett filter är applicerat!</filterpane:isFiltered>
				<filterpane:isNotFiltered>Inget filter finns!</filterpane:isNotFiltered>
				<filterpane:filterButton text="Filtrera lista" appliedText="Ändra filter"/>
			</div>
			<filterpane:filterPane
				domain="se.skltp.tak.core.entity.Vagval"
				associatedProperties="anropsAdress.adress,anropsAdress.tjanstekomponent.hsaId,logiskAdress.hsaId,tjanstekontrakt.namnrymd"
				excludeProperties="id"/>
		</div>
	</body>
</html>
