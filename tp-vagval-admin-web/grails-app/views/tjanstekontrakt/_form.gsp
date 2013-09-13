<%@ page import="se.skl.tp.vagval.admin.core.entity.Tjanstekontrakt" %>



<div class="fieldcontain ${hasErrors(bean: tjanstekontraktInstance, field: 'namnrymd', 'error')} required">
	<label for="namnrymd">
		<g:message code="tjanstekontrakt.namnrymd.label" default="Namnrymd" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="namnrymd" cols="40" rows="5" maxlength="255" required="" value="${tjanstekontraktInstance?.namnrymd}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tjanstekontraktInstance, field: 'beskrivning', 'error')} ">
	<label for="beskrivning">
		<g:message code="tjanstekontrakt.beskrivning.label" default="Beskrivning" />
		
	</label>
	<g:textArea name="beskrivning" cols="40" rows="5" maxlength="255" value="${tjanstekontraktInstance?.beskrivning}"/>
</div>

<g:if test="${tjanstekontraktInstance.id}">

<div class="fieldcontain ${hasErrors(bean: tjanstekontraktInstance, field: 'anropsbehorigheter', 'error')} required">
	<label for="anropsbehorigheter">
		<g:message code="tjanstekontrakt.anropsbehorigheter.label" default="Anropsbehorigheter" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${tjanstekontraktInstance?.anropsbehorigheter?}" var="a">
    <li><g:link controller="anropsbehorighet" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="anropsbehorighet" action="create" params="['tjanstekontrakt.id': tjanstekontraktInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet')])}</g:link>
</li>
</ul>

</div>

</g:if>

<g:if test="${tjanstekontraktInstance.id}">

<div class="fieldcontain ${hasErrors(bean: tjanstekontraktInstance, field: 'logiskaAdresser', 'error')} required">
	<label for="logiskaAdresser">
		<g:message code="tjanstekontrakt.logiskaAdresser.label" default="Logiska Adresser" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${tjanstekontraktInstance?.logiskaAdresser?}" var="l">
    <li><g:link controller="logiskAdress" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="logiskAdress" action="create" params="['tjanstekontrakt.id': tjanstekontraktInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'logiskAdress.label', default: 'LogiskAdress')])}</g:link>
</li>
</ul>

</div>

</g:if>

