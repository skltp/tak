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
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<t:main>
	<%--<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'rivTaProfil.label', default: 'RivTaProfil')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
		<r:require module="select2"/>
	</head>--%>

		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="/">Hem</a></li>
				<li><a class="list" href="/rivTaProfil">RivTaProfil - Lista</a></li>
			</ul>
		</div>
		<div id="create-rivTaProfil" class="content scaffold-create" role="main">
			<h1>Skapa RivTaProfil</h1>
			<%--<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${instance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${instance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>--%>
			<form:form method="POST" action="/rivTaProfil/create" modelAttribute="instance">
				<fieldset class="form">
					<%@include file="_form.jsp" %>
				</fieldset>
				<fieldset class="buttons">
					<input type="submit" name="create" class="save" value="Skapa" />
				</fieldset>
			</form:form>
		</div>

</t:main>
