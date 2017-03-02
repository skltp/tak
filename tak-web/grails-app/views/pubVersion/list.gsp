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
        <g:if test="${params.get('rollback')}">
            <div class="message"><g:message code="info.message.rollback" /></div>
            <div class="message"><g:message code="info.message.rollback2" /></div>
        </g:if>
        <g:if test="${params.get('resetCache')}">
            <div class="message"><g:message code="info.message.resetCache" /></div>
        </g:if>
		<div id="list-pubVersion" class="content scaffold-list" role="main">
			<h1><g:message code="pubVersion.list.label" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<th class="rightmostColumn" />
						
						<g:sortableColumn property="id" defaultOrder="desc" title="${message(code: 'default.version.label', default: 'x_PV')}" class="rightmostColumn" />
					
						<g:sortableColumn property="kommentar" title="${message(code: 'pubVersion.kommentar.label', default: 'x_Kommentar')}" />
					
						<g:sortableColumn property="time" title="${message(code: 'pubVersion.time.label', default: 'x_Tid')}" />
					
						<g:sortableColumn property="utforare" title="${message(code: 'pubVersion.utforare.label', default: 'x_Utforare')}" />
						
						<g:sortableColumn property="storlek" title="${message(code: 'pubVersion.storlek.label', default: 'x_Storlek')}" />
						
						<g:sortableColumn property="laddaner" title="${message(code: 'pubVersion.download.label', default: 'x_Laddaner')}" />

						<g:if test="${params.get('rollback')}">
                            <shiro:hasRole name="Admin">
                                <g:sortableColumn property="rollback" title="${message(code: 'pubVersion.rollback.label', default: 'x_Rollback')}" />
                            </shiro:hasRole>
                        </g:if>

                        <g:if test="${params.get('resetCache')}">
                            <g:sortableColumn property="resetCache" title="${message(code: 'pubVersion.rensa.cache.version.header', default: 'x_ResetCache')}" />
                        </g:if>
					</tr>
				</thead>
				<tbody>
				<g:each in="${pubVersionInstanceList}" status="i" var="pubVersionInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${pubVersionInstance.id}">Visa</g:link></td>
						
						<td><g:link action="show" id="${pubVersionInstance.id}">${pubVersionInstance.id}</g:link></td>
						
						<td>${fieldValue(bean: pubVersionInstance, field: "kommentar")}</td>
					
						<td><g:formatDate date="${pubVersionInstance.time}" /></td>
					
						<td>${fieldValue(bean: pubVersionInstance, field: "utforare")}</td>
						<td>${String.format("%.3f", ((pubVersionInstance.storlek)/1024))} KB</td>
						
						<td><g:link action="download" id="${pubVersionInstance.id}"><img src="${resource(dir:'images',file:'download.png')}" alt="Laddaner" /></g:link></td>

                        <g:if test="${params.get('rollback')}">
                            <shiro:hasRole name="Admin">
                                <td>
                                    <g:if test="${i==0}">
                                        <g:link action="rollback" id="${pubVersionInstance.id}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
                                            <img src="${resource(dir:'images',file:'rollback.png')}" alt="Rollback" />
                                        </g:link>
                                    </g:if>
                                </td>
                            </shiro:hasRole>
                        </g:if>

                        <g:if test="${params.get('resetCache')}">
                            <td>
                                <g:link url ="../tak-services/reset/pv?version=${pubVersionInstance.id}" target="_blank">
                                    <g:message code="pubVersion.rensa.cache.version.label" />
                                </g:link>
                            </td>
                        </g:if>
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
