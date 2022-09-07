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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<t:main>
	<%--<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'rivTaProfil.label', default: 'RivTaProfil')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>--%>

		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="/">Hem</a></li>
				<li><a class="list" href="/rivTaProfil">RivTaProfil - Lista</a></li>
				<li><a class="create" href="/rivTaProfil/create">Skapa RivTaProfil</a></li>
			</ul>
		</div>
		<div id="edit-rivTaProfil" class="content scaffold-edit" role="main">
			<h1>Redigera RivTaProfil</h1>
			<c:if test="${message != null}">
			    <div class="message" role="status">${message}</div>
			</c:if>
			<%--<g:hasErrors bean="${rivTaProfilInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${rivTaProfilInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>--%>
			<form:form method="POST" action="/rivTaProfil/update" modelAttribute="rivTaProfilInstance">
				<form:hidden path="id" />
				<form:hidden path="version" />
				<fieldset class="form">
					<%@include file="_form.jsp" %>
				</fieldset>
				<fieldset class="buttons">
					<input type="submit" class="save" value="Uppdatera" />
				</fieldset>
			</form:form>
		</div>
</t:main>
