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
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'settings.label', default: 'TAK Settings')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-setting" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
</a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-setting" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
</div>

<ol class="property-list settings">

    <li class="fieldcontain">
        <span id="settings-name-label" class="property-label">
            <g:message code="settings.name.label" default="Name"/>
        </span>
        <span class="property-value" aria-labelledby="settings-name-label">
            <g:fieldValue bean="${settingInstance}" field="settingName"/>
        </span>
    </li>


    <li class="fieldcontain">
        <span id="settings-value-label" class="property-label">
            <g:message code="settings.value.label" default="Value"/>
        </span>
        <span class="property-value" aria-labelledby="settings-value-label">
            <g:fieldValue bean="${settingInstance}" field="settingValue"/>
        </span>
    </li>

</ol>

<g:form>
    <fieldset class="buttons">
        <g:hiddenField name="id" value="${settingInstance?.id}"/>
        <g:link class="edit" action="edit" id="${settingInstance?.id}">
            <g:message code="default.button.edit.label" default="Edit"/>
        </g:link>

    </fieldset>
</g:form>

</body>
</html>