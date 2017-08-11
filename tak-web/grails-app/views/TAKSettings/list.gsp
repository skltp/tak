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
<%@ page import="se.skltp.tak.web.entity.TAKSettings" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:set var="entityName" value="${message(code: 'settings.label', default: 'Settings')}"/>
    <title><g:message code="application.title"
                      default="Welcome to TP ${grailsApplication.metadata.'app.version'}"/> (${grailsApplication.config.tak.environment})</title>
    <meta name="layout" content="main"/>
</head>

<body>
<a href="#list-settings" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                               default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<div id="list-settings" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>

    <table>
        <thead>
        <tr>

            <g:sortableColumn property="id" title="${message(code: 'settings.id.label', default: 'id')}"
                              class="rightmostColumn"/>

            <g:sortableColumn property="settingName" title="${message(code: 'settings.name.label', default: 'Name')}"
                              params="${filterParams}"/>

            <g:sortableColumn property="settingValue" title="${message(code: 'settings.value.label', default: 'Value')}"
                              params="${filterParams}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${settingInstanceList}" status="i" var="setting">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td>${setting.id}</td>
                <td><g:link action="show" id="${setting.id}">${setting.settingName}</g:link></td>
                <td>${setting.settingValue}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
</body>
</html>