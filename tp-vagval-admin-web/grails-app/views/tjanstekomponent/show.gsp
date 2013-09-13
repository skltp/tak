
<%@ page import="se.skl.tp.vagval.admin.core.entity.Tjanstekomponent" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'tjanstekomponent.label', default: 'Tjanstekomponent')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-tjanstekomponent" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-tjanstekomponent" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list tjanstekomponent">
			
				<g:if test="${tjanstekomponentInstance?.hsaId}">
				<li class="fieldcontain">
					<span id="hsaId-label" class="property-label"><g:message code="tjanstekomponent.hsaId.label" default="Hsa Id" /></span>
					
						<span class="property-value" aria-labelledby="hsaId-label"><g:fieldValue bean="${tjanstekomponentInstance}" field="hsaId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${tjanstekomponentInstance?.adress}">
				<li class="fieldcontain">
					<span id="adress-label" class="property-label"><g:message code="tjanstekomponent.adress.label" default="Adress" /></span>
					
						<span class="property-value" aria-labelledby="adress-label"><g:fieldValue bean="${tjanstekomponentInstance}" field="adress"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${tjanstekomponentInstance?.beskrivning}">
				<li class="fieldcontain">
					<span id="beskrivning-label" class="property-label"><g:message code="tjanstekomponent.beskrivning.label" default="Beskrivning" /></span>
					
						<span class="property-value" aria-labelledby="beskrivning-label"><g:fieldValue bean="${tjanstekomponentInstance}" field="beskrivning"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${tjanstekomponentInstance?.anropsbehorigheter}">
				<li class="fieldcontain">
					<span id="anropsbehorigheter-label" class="property-label"><g:message code="tjanstekomponent.anropsbehorigheter.label" default="Anropsbehorigheter" /></span>
					
						<g:each in="${tjanstekomponentInstance.anropsbehorigheter}" var="a">
						<span class="property-value" aria-labelledby="anropsbehorigheter-label"><g:link controller="anropsbehorighet" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${tjanstekomponentInstance?.logiskAdresser}">
				<li class="fieldcontain">
					<span id="logiskAdresser-label" class="property-label"><g:message code="tjanstekomponent.logiskAdresser.label" default="Logisk Adresser" /></span>
					
						<g:each in="${tjanstekomponentInstance.logiskAdresser}" var="l">
						<span class="property-value" aria-labelledby="logiskAdresser-label"><g:link controller="logiskAdress" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${tjanstekomponentInstance?.id}" />
					<g:link class="edit" action="edit" id="${tjanstekomponentInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
