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
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'beställning.label', default: 'JSON Beställning')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
    <r:require module="select2"/>
</head>
<body>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="create-jsonBestallning" class="content scaffold-create" role="main">
    <h1 style="margin-left:1em;">
        <g:message code="bestallning.create.label" />
    </h1>
    <g:if test="${isUrlConfigured}">
            <g:if test="${flash.message}">
                <div class="message" role="status"> ${flash.message} </div>
            </g:if>
            <g:if test="${flash.loadError}">
                <div class="errors" role="status"> ${flash.loadError} </div>
            </g:if>
        <g:form action="load">
            <fieldset class="form">
                <div class="fieldcontain">
                    <label for="create-jsonBestallning">
                        <g:message code="beställning_num.label" />
                        <span class="required-indicator"> </span>
                    </label>
                    <g:textField style="width:50%"
                            name="jsonBestallningNum"
                            cols="20"
                            rows="1"
                            maxSize="750"
                            value="${jsonBestallningNum}" />
                </div>
            </fieldset>
            <fieldset class="buttons">
                <g:submitButton name="create" class="save" value="${message(code:'button.get.label')}" />
            </fieldset>
        </g:form>
    </g:if>
    <g:form action="validate">
        <g:if test="${flash.error}">
            <div class="errors" role="status"> ${flash.error} </div>
        </g:if>
        <fieldset class="form">
            <div class="fieldcontain">
                <label for="create-jsonBestallning">
                    <g:message code="beställning.label" />
                    <span class="required-indicator">*</span>
                </label>
                <g:textArea style="width:50%"
                            name="jsonBestallningText"
                            cols="100"
                            rows="5"
                            maxSize="15000"
                            required=""
                            value="${jsonBestallningText}" />
            </div>
        </fieldset>

        <fieldset class="buttons">
            <g:submitButton name="validate" class="save" value="${message(code:'button.validate.label')}" />
        </fieldset>
    </g:form>
</div>
</body>
</html>
