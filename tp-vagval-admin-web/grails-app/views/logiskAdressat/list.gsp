
<%@ page import="se.skl.tp.vagval.admin.core.entity.LogiskAdressat" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'logiskAdressat.label', default: 'LogiskAdressat')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<r:require module="filterpane" />
	</head>
	<body>
		<a href="#list-logiskAdressat" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-logiskAdressat" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="hsaId" title="${message(code: 'logiskAdressat.hsaId.label', default: 'Hsa Id')}" />
					
						<g:sortableColumn property="beskrivning" title="${message(code: 'logiskAdressat.beskrivning.label', default: 'Beskrivning')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${logiskAdressatInstanceList}" status="i" var="logiskAdressatInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${logiskAdressatInstance.id}">${fieldValue(bean: logiskAdressatInstance, field: "hsaId")}</g:link></td>
					
						<td>${fieldValue(bean: logiskAdressatInstance, field: "beskrivning")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<filterpane:paginate total="${logiskAdressatInstanceTotal}" domainBean="se.skl.tp.vagval.admin.core.entity.LogiskAdressat"/>
				<filterpane:isFiltered>Ett filter är applicerat!</filterpane:isFiltered>
				<filterpane:isNotFiltered>Inget filter finns!</filterpane:isNotFiltered>
				<filterpane:filterButton text="Filtrera lista" appliedText="Ändra filter"/>
			</div>
			<filterpane:filterPane
				domain="se.skl.tp.vagval.admin.core.entity.LogiskAdressat"		
				excludeProperties="id,version"/>
		</div>
	</body>
</html>
