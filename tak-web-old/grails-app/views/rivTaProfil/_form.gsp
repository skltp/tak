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
<%@ page import="se.skltp.tak.core.entity.RivTaProfil" %>



<div class="fieldcontain ${hasErrors(bean: rivTaProfilInstance, field: 'namn', 'error')} required">
	<label for="namn">
		<g:message code="rivTaProfil.namn.label" default="Namn" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="namn" cols="40" rows="5" maxlength="255" required="" value="${rivTaProfilInstance?.namn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: rivTaProfilInstance, field: 'beskrivning', 'error')} ">
	<label for="beskrivning">
		<g:message code="rivTaProfil.beskrivning.label" default="Beskrivning" />
		
	</label>
	<g:textArea name="beskrivning" cols="40" rows="5" maxlength="255" value="${rivTaProfilInstance?.beskrivning}"/>
</div>
<%-- 
<div class="fieldcontain ${hasErrors(bean: rivTaProfilInstance, field: 'pubVersion', 'error')} ">
	<label for="pubVersion">
		<g:message code="rivTaProfil.pubVersion.label" default="Pub Version" />
		
	</label>
	<g:textArea name="pubVersion" cols="40" rows="5" maxlength="255" value="${rivTaProfilInstance?.pubVersion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: rivTaProfilInstance, field: 'updatedTime', 'error')} ">
	<label for="updatedTime">
		<g:message code="rivTaProfil.updatedTime.label" default="Updated Time" />
		
	</label>
	<g:datePicker name="updatedTime" precision="day"  value="${rivTaProfilInstance?.updatedTime}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: rivTaProfilInstance, field: 'updatedBy', 'error')} ">
	<label for="updatedBy">
		<g:message code="rivTaProfil.updatedBy.label" default="Updated By" />
		
	</label>
	<g:textArea name="updatedBy" cols="40" rows="5" maxlength="255" value="${rivTaProfilInstance?.updatedBy}"/>
</div>

<g:if test="${rivTaProfilInstance.id}">

<div class="fieldcontain ${hasErrors(bean: rivTaProfilInstance, field: 'AnropsAdresser', 'error')} required">
	<label for="AnropsAdresser">
		<g:message code="rivTaProfil.AnropsAdresser.label" default="Anrops Adresser" />
		<span class="required-indicator">*</span>
	</label>
	
<ul class="one-to-many">
<g:each in="${rivTaProfilInstance?.AnropsAdresser?}" var="A">
    <li><g:link controller="anropsAdress" action="show" id="${A.id}">${A?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="anropsAdress" action="create" params="['rivTaProfil.id': rivTaProfilInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'anropsAdress.label', default: 'AnropsAdress')])}</g:link>
</li>
</ul>

</div>

</g:if>

<div class="fieldcontain ${hasErrors(bean: rivTaProfilInstance, field: 'deleted', 'error')} ">
	<label for="deleted">
		<g:message code="rivTaProfil.deleted.label" default="Deleted" />
		
	</label>
	<g:checkBox name="deleted" value="${rivTaProfilInstance?.deleted}" />
</div>

--%>