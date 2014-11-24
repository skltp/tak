
<%@ page import="se.skltp.tak.core.entity.AnropsAdress" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anropsAdress.label', default: 'AnropsAdress')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-anropsAdress" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-anropsAdress" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list anropsAdress">
			
				<g:if test="${anropsAdressInstance?.adress}">
				<li class="fieldcontain">
					<span id="adress-label" class="property-label"><g:message code="anropsAdress.adress.label" default="Adress" /></span>
					
						<span class="property-value" aria-labelledby="adress-label"><g:fieldValue bean="${anropsAdressInstance}" field="adress"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.tjanstekomponent}">
				<li class="fieldcontain">
					<span id="tjanstekomponent-label" class="property-label"><g:message code="anropsAdress.tjanstekomponent.label" default="Tjanstekomponent" /></span>
					
						<span class="property-value" aria-labelledby="tjanstekomponent-label"><g:link controller="tjanstekomponent" action="show" id="${anropsAdressInstance?.tjanstekomponent?.id}">${anropsAdressInstance?.tjanstekomponent?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.rivTaProfil}">
				<li class="fieldcontain">
					<span id="rivTaProfil-label" class="property-label"><g:message code="anropsAdress.rivTaProfil.label" default="Riv Ta Profil" /></span>
					
						<span class="property-value" aria-labelledby="rivTaProfil-label"><g:link controller="rivTaProfil" action="show" id="${anropsAdressInstance?.rivTaProfil?.id}">${anropsAdressInstance?.rivTaProfil?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anropsAdressInstance?.vagVal}">
				<li class="fieldcontain">
					<span id="vagVal-label" class="property-label"><g:message code="anropsAdress.vagVal.label" default="Vag Val" /></span>
					
						<g:each in="${anropsAdressInstance.vagVal}" var="v">
						<span class="property-value" aria-labelledby="vagVal-label"><g:link controller="vagval" action="show" id="${v.id}">${v?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${anropsAdressInstance?.id}" />
					<g:link class="edit" action="edit" id="${anropsAdressInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
