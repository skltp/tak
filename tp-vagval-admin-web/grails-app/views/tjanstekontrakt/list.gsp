
<%@ page import="se.skl.tp.vagval.admin.core.entity.Tjanstekontrakt" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="filterpane" />
	</head>
	<body>
		<a href="#list-tjanstekontrakt" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-tjanstekontrakt" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="namnrymd" title="${message(code: 'tjanstekontrakt.namnrymd.label', default: 'Namnrymd')}" />
					
						<g:sortableColumn property="beskrivning" title="${message(code: 'tjanstekontrakt.beskrivning.label', default: 'Beskrivning')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${tjanstekontraktInstanceList}" status="i" var="tjanstekontraktInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${tjanstekontraktInstance.id}">${fieldValue(bean: tjanstekontraktInstance, field: "namnrymd")}</g:link></td>
					
						<td>${fieldValue(bean: tjanstekontraktInstance, field: "beskrivning")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${tjanstekontraktInstanceTotal}" />
				<filterpane:filterButton text="Filtrera lista" appliedText="Ändra filter"/>
			</div>
			<filterpane:filterPane
				domain="se.skl.tp.vagval.admin.core.entity.Tjanstekontrakt"
				excludeProperties="id,version"/>
		</div>
	</body>
</html>
