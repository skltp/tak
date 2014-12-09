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
<%@ page import="se.skltp.tak.core.entity.LogiskAdress" %>



<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'hsaId', 'error')} required">
	<label for="hsaId">
		<g:message code="logiskAdress.hsaId.label" default="Hsa Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="hsaId" cols="40" rows="5" maxlength="255" required="" value="${logiskAdressInstance?.hsaId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'beskrivning', 'error')} ">
	<label for="beskrivning">
		<g:message code="logiskAdress.beskrivning.label" default="Beskrivning" />
		
	</label>
	<g:textArea name="beskrivning" cols="40" rows="5" maxlength="255" value="${logiskAdressInstance?.beskrivning}"/>
</div>

<g:if test="${logiskAdressInstance.id}">

<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'anropsbehorigheter', 'error')} required">
	<label for="anropsbehorigheter">
		<g:message code="logiskAdress.anropsbehorigheter.label" default="Anropsbehorigheter" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${logiskAdressInstance?.anropsbehorigheter?}" var="a">
    <li><g:link controller="anropsbehorighet" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="anropsbehorighet" action="create" params="['logiskAdress.id': logiskAdressInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet')])}</g:link>
</li>
</ul>

</div>

</g:if>

<g:if test="${logiskAdressInstance.id}">

<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'vagval', 'error')} required">
	<label for="vagval">
		<g:message code="logiskAdress.vagval.label" default="Vägval" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${logiskAdressInstance?.vagval?}" var="l">
    <li><g:link controller="vagval" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="vagval" action="create" params="['logiskAdress.id': logiskAdressInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'vagval.label', default: 'Vägval')])}</g:link>
</li>
</ul>

</div>

</g:if>

