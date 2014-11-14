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
<%@ page import="se.skltp.tak.core.entity.Vagval"
		"import="org.joda.time.DateTime"  %>



<div class="fieldcontain ${hasErrors(bean: vagvalInstance, field: 'tjanstekontrakt', 'error')} required">
	<label for="tjanstekontrakt">
		<g:message code="vagval.tjanstekontrakt.label" default="Tjanstekontrakt" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tjanstekontrakt" name="tjanstekontrakt.id" style="width:50%" from="${se.skltp.tak.core.entity.Tjanstekontrakt.list()}" optionKey="id" required="" value="${vagvalInstance?.tjanstekontrakt?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: vagvalInstance, field: 'logiskAdress', 'error')} required">
	<label for="logiskAdress">
		<g:message code="vagval.logiskAdress.label" default="Logisk Adress" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="logiskAdress" name="logiskAdress.id"
		from="${se.skltp.tak.core.entity.LogiskAdress.list()}"
		optionKey="id" required=""
		value="${vagvalInstance?.logiskAdress?.id}"
		optionValue="${{it?.hsaId + ' - ' + it?.beskrivning}}"
		class="many-to-one" style="width:50%"/>
</div>

<div class="fieldcontain ${hasErrors(bean: vagvalInstance, field: 'tjansteproducent', 'error')} required">
	<label for="tjansteproducent">
		<g:message code="vagval.tjansteproducent.label" default="Tjansteproducent" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tjansteproducent" name="tjansteproducent.id"
		from="${se.skltp.tak.core.entity.Tjanstekomponent.list()}"
		optionKey="id" required=""
		value="${vagvalInstance?.anropsAdress?.tjanstekomponent?.id}"
		optionValue="${{it?.hsaId + ' - ' + it?.beskrivning}}"
		class="many-to-one" style="width:50%"/>
</div>

<div class="fieldcontain ${hasErrors(bean: vagvalInstance, field: 'anropsAdress', 'error')} required">
	<label for="hsaId">
		<g:message code="vagval.anropsAdress.label" default="AnropsAdress" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="anropsAdress" name="anropsAdress.id"
		from="${se.skltp.tak.core.entity.AnropsAdress.list()}"
		optionKey="id" required=""
		value="${vagvalInstance?.anropsAdress?.id}"
		optionValue="${{it?.tjanstekomponent.hsaId + ' - ' + it?.rivTaProfil?.namn + ' - ' + it?.adress}}"
		class="many-to-one" style="width:50%"/>
</div>

<div class="fieldcontain ${hasErrors(bean: vagvalInstance, field: 'fromTidpunkt', 'error')} required">
	<label for="fromTidpunkt">
		<g:message code="vagval.fromTidpunkt.label" default="From Tidpunkt" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="fromTidpunkt" precision="day"  value="${vagvalInstance?.fromTidpunkt}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: vagvalInstance, field: 'tomTidpunkt', 'error')} required">
	<label for="tomTidpunkt">
		<g:message code="vagval.tomTidpunkt.label" default="Tom Tidpunkt" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="tomTidpunkt" format="yyyy-MM-dd" precision="day"  value="${vagvalInstance?.tomTidpunkt}" default="${new Date(DateTime.now().plusYears(100).getMillis())}" />
</div>
