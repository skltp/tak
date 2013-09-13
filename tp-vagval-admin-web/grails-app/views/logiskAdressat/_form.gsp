<%@ page import="se.skl.tp.vagval.admin.core.entity.LogiskAdressat" %>



<div class="fieldcontain ${hasErrors(bean: logiskAdressatInstance, field: 'hsaId', 'error')} required">
	<label for="hsaId">
		<g:message code="logiskAdressat.hsaId.label" default="Hsa Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="hsaId" cols="40" rows="5" maxlength="255" required="" value="${logiskAdressatInstance?.hsaId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: logiskAdressatInstance, field: 'beskrivning', 'error')} ">
	<label for="beskrivning">
		<g:message code="logiskAdressat.beskrivning.label" default="Beskrivning" />
		
	</label>
	<g:textArea name="beskrivning" cols="40" rows="5" maxlength="255" value="${logiskAdressatInstance?.beskrivning}"/>
</div>

<g:if test="${logiskAdressatInstance.id}">

<div class="fieldcontain ${hasErrors(bean: logiskAdressatInstance, field: 'anropsbehorigheter', 'error')} required">
	<label for="anropsbehorigheter">
		<g:message code="logiskAdressat.anropsbehorigheter.label" default="Anropsbehorigheter" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${logiskAdressatInstance?.anropsbehorigheter?}" var="a">
    <li><g:link controller="anropsbehorighet" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="anropsbehorighet" action="create" params="['logiskAdressat.id': logiskAdressatInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet')])}</g:link>
</li>
</ul>

</div>

</g:if>

<g:if test="${logiskAdressatInstance.id}">

<div class="fieldcontain ${hasErrors(bean: logiskAdressatInstance, field: 'logiskaAdresser', 'error')} required">
	<label for="logiskaAdresser">
		<g:message code="logiskAdressat.logiskaAdresser.label" default="Logiska Adresser" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${logiskAdressatInstance?.logiskaAdresser?}" var="l">
    <li><g:link controller="logiskAdress" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="logiskAdress" action="create" params="['logiskAdressat.id': logiskAdressatInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'logiskAdress.label', default: 'LogiskAdress')])}</g:link>
</li>
</ul>

</div>

</g:if>

