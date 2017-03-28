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
<%@ page import="se.skltp.tak.core.entity.Tjanstekomponent" %>



<div class="fieldcontain ${hasErrors(bean: tjanstekomponentInstance, field: 'hsaId', 'error')} required">
	<label for="hsaId">
		<g:message code="tjanstekomponent.hsaId.label" default="Hsa Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="hsaId" cols="40" maxlength="255" required="true" pattern="[0-9A-Z_\\-]*"  value="${tjanstekomponentInstance?.hsaId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tjanstekomponentInstance, field: 'beskrivning', 'error')} ">
	<label for="beskrivning">
		<g:message code="tjanstekomponent.beskrivning.label" default="Beskrivning" />
		
	</label>
	<g:textArea name="beskrivning" cols="40" rows="5" maxlength="255" value="${tjanstekomponentInstance?.beskrivning}"/>
</div>

<g:if test="${tjanstekomponentInstance.id}">

<div class="fieldcontain ${hasErrors(bean: tjanstekomponentInstance, field: 'anropsbehorigheter', 'error')} required">
	<label for="anropsbehorigheter">
		<g:message code="tjanstekomponent.anropsbehorigheter.label" default="Anropsbehorigheter" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${tjanstekomponentInstance?.anropsbehorigheter?}" var="a">
    <li><g:link controller="anropsbehorighet" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="anropsbehorighet" action="create" params="['tjanstekomponent.id': tjanstekomponentInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet')])}</g:link>
</li>
</ul>

</div>

</g:if>

<g:if test="${tjanstekomponentInstance.id}">

<div class="fieldcontain ${hasErrors(bean: tjanstekomponentInstance, field: 'anropsAdresser', 'error')} required">
	<label for="anropsAdresser">
		<g:message code="tjanstekomponent.anropsAdress.label" default="Anropsadresser" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${tjanstekomponentInstance?.anropsAdresser?}" var="l">
    <li><g:link controller="anropsAdress" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="anropsAdress" action="create" params="['tjanstekomponent.id': tjanstekomponentInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'anropsAdress.label', default: 'Anropsadress')])}</g:link>
</li>
</ul>

</div>

</g:if>

