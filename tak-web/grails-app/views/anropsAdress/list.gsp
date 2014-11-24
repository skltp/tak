
<%@ page import="se.skltp.tak.core.entity.AnropsAdress" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anropsAdress.label', default: 'AnropsAdress')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="filterpane" />
	</head>
	<body>
		<a href="#list-anropsAdress" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-anropsAdress" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="adress" title="${message(code: 'anropsAdress.adress.label', default: 'Adress')}" />
					
						<th><g:message code="anropsAdress.tjanstekomponent.label" default="Tjanstekomponent" /></th>
					
						<th><g:message code="anropsAdress.rivTaProfil.label" default="Riv Ta Profil" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${anropsAdressInstanceList}" status="i" var="anropsAdressInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${anropsAdressInstance.id}">${fieldValue(bean: anropsAdressInstance, field: "adress")}</g:link></td>
					
						<td>${fieldValue(bean: anropsAdressInstance, field: "tjanstekomponent")}</td>
					
						<td>${fieldValue(bean: anropsAdressInstance, field: "rivTaProfil")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<filterpane:paginate total="${anropsAdressInstanceList}" domainBean="se.skltp.tak.core.entity.AnropsAdress"/>
				<filterpane:isFiltered>Ett filter är applicerat!</filterpane:isFiltered>
				<filterpane:isNotFiltered>Inget filter finns!</filterpane:isNotFiltered>
				<filterpane:filterButton text="Filtrera lista" appliedText="Ändra filter"/>
			</div>
			<filterpane:filterPane
				domain="se.skltp.tak.core.entity.AnropsAdress"		
				excludeProperties="id,version"/>
		</div>
	</body>
</html>
