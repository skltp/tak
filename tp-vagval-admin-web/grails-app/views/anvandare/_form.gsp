<%@ page import="se.skl.tp.vagval.admin.web.entity.Anvandare" %>



<div class="fieldcontain ${hasErrors(bean: anvandareInstance, field: 'anvandarnamn', 'error')} required">
	<label for="anvandarnamn">
		<g:message code="anvandare.anvandarnamn.label" default="Anvandarnamn" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="anvandarnamn" required="" value="${anvandareInstance?.anvandarnamn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anvandareInstance, field: 'administrator', 'error')} ">
	<label for="administrator">
		<g:message code="anvandare.administrator.label" default="Administrator" />
		
	</label>
	<g:checkBox name="administrator" value="${anvandareInstance?.administrator}" />
</div>

<div class="fieldcontain ${hasErrors(bean: anvandareInstance, field: 'losenord', 'error')} ">
	<label for="losenord">
		<g:message code="anvandare.losenord.label" default="Losenord" />
		
	</label>
	<g:passwordField name="losenord" value="${anvandareInstance?.losenord}"/>
</div>

