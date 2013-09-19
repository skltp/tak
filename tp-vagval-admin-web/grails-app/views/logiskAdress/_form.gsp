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
<%@ page import="se.skl.tp.vagval.admin.core.entity.LogiskAdress" %>



<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'rivVersion', 'error')} required">
	<label for="rivVersion">
		<g:message code="logiskAdress.rivVersion.label" default="Riv Version" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="rivVersion" name="rivVersion.id" from="${se.skl.tp.vagval.admin.core.entity.RivVersion.list()}" optionKey="id" required="" value="${logiskAdressInstance?.rivVersion?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'tjanstekontrakt', 'error')} required">
	<label for="tjanstekontrakt">
		<g:message code="logiskAdress.tjanstekontrakt.label" default="Tjanstekontrakt" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tjanstekontrakt" name="tjanstekontrakt.id" from="${se.skl.tp.vagval.admin.core.entity.Tjanstekontrakt.list()}" optionKey="id" required="" value="${logiskAdressInstance?.tjanstekontrakt?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'logiskAdressat', 'error')} required">
	<label for="logiskAdressat">
		<g:message code="logiskAdress.logiskAdressat.label" default="Logisk Adressat" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="logiskAdressat" name="logiskAdressat.id"
		from="${se.skl.tp.vagval.admin.core.entity.LogiskAdressat.list()}"
		optionKey="id" required=""
		value="${logiskAdressInstance?.logiskAdressat?.id}"
		optionValue="${{it?.hsaId + ' - ' + it?.beskrivning}}"
		class="many-to-one" />
</div>

<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'tjansteproducent', 'error')} required">
	<label for="tjansteproducent">
		<g:message code="logiskAdress.tjansteproducent.label" default="Tjansteproducent" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tjansteproducent" name="tjansteproducent.id"
		from="${se.skl.tp.vagval.admin.core.entity.Tjanstekomponent.list()}"
		optionKey="id" required=""
		value="${logiskAdressInstance?.tjansteproducent?.id}"
		optionValue="${{it?.hsaId + ' - ' + it?.beskrivning}}"
		class="many-to-one" />
</div>

<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'fromTidpunkt', 'error')} required">
	<label for="fromTidpunkt">
		<g:message code="logiskAdress.fromTidpunkt.label" default="From Tidpunkt" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="fromTidpunkt" precision="day"  value="${logiskAdressInstance?.fromTidpunkt}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: logiskAdressInstance, field: 'tomTidpunkt', 'error')} required">
	<label for="tomTidpunkt">
		<g:message code="logiskAdress.tomTidpunkt.label" default="Tom Tidpunkt" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="tomTidpunkt" precision="day"  value="${logiskAdressInstance?.tomTidpunkt}"  />
</div>

