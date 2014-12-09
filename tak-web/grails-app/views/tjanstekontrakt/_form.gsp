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
<%@ page import="se.skltp.tak.core.entity.Tjanstekontrakt" %>



<div class="fieldcontain ${hasErrors(bean: tjanstekontraktInstance, field: 'namnrymd', 'error')} required">
	<label for="namnrymd">
		<g:message code="tjanstekontrakt.namnrymd.label" default="Namnrymd" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="namnrymd" cols="40" rows="5" maxlength="255" required="" value="${tjanstekontraktInstance?.namnrymd}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tjanstekontraktInstance, field: 'majorVersion', 'error')} required">
	<label for="majorVersion">
		<g:message code="tjanstekontrakt.majorVersion.label" default="Major version" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="majorVersion" name="majorVersion" value="${tjanstekontraktInstance.majorVersion}" from="${0..9}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: tjanstekontraktInstance, field: 'minorVersion', 'error')} required">
	<label for="minorVersion">
		<g:message code="tjanstekontrakt.minorVersion.label" default="Minor version" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="minorVersion" name="minorVersion" value="${tjanstekontraktInstance.minorVersion}" from="${0..9}"/>
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
<g:each in="${tjanstekontraktInstance?.vagval?}" var="l">
    <li><g:link controller="vagval" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="vagval" action="create" params="['tjanstekontrakt.id': tjanstekontraktInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'logiskAdress.label', default: 'LogiskAdress')])}</g:link>
</li>
</ul>

</div>

</g:if>

