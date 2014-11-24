<%@ page import="se.skltp.tak.core.entity.AnropsAdress" %>



<div class="fieldcontain ${hasErrors(bean: anropsAdressInstance, field: 'adress', 'error')} required">
	<label for="adress">
		<g:message code="anropsAdress.adress.label" default="Adress" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="adress" cols="40" rows="5" maxlength="255" required="" value="${anropsAdressInstance?.adress}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anropsAdressInstance, field: 'tjanstekomponent', 'error')} required">
	<label for="tjanstekomponent">
		<g:message code="anropsAdress.tjanstekomponent.label" default="Tjanstekomponent" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tjanstekomponent" name="tjanstekomponent.id" from="${se.skltp.tak.core.entity.Tjanstekomponent.list()}" optionKey="id" required="" value="${anropsAdressInstance?.tjanstekomponent?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anropsAdressInstance, field: 'rivTaProfil', 'error')} required">
	<label for="rivTaProfil">
		<g:message code="anropsAdress.rivTaProfil.label" default="Riv Ta Profil" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="rivTaProfil" name="rivTaProfil.id" from="${se.skltp.tak.core.entity.RivTaProfil.list()}" optionKey="id" required="" value="${anropsAdressInstance?.rivTaProfil?.id}" class="many-to-one"/>
</div>

<g:if test="${anropsAdressInstance.id}">

<div class="fieldcontain ${hasErrors(bean: anropsAdressInstance, field: 'vagVal', 'error')} required">
	<label for="vagVal">
		<g:message code="anropsAdress.vagVal.label" default="Vag Val" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${anropsAdressInstance?.vagVal?}" var="v">
    <li><g:link controller="vagval" action="show" id="${v.id}">${v?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="vagval" action="create" params="['anropsAdress.id': anropsAdressInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'vagval.label', default: 'Vagval')])}</g:link>
</li>
</ul>

</div>

</g:if>

