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
<%@ page import="se.skl.tp.vagval.admin.core.entity.Anropsbehorighet" %>



<div class="fieldcontain ${hasErrors(bean: anropsbehorighetInstance, field: 'integrationsavtal', 'error')} required">
	<label for="integrationsavtal">
		<g:message code="anropsbehorighet.integrationsavtal.label" default="Integrationsavtal" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="integrationsavtal" cols="40" rows="5" maxlength="255" required="" value="${anropsbehorighetInstance?.integrationsavtal}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anropsbehorighetInstance, field: 'tjanstekonsument', 'error')} required">
	<label for="tjanstekonsument">
		<g:message code="anropsbehorighet.tjanstekonsument.label" default="Tjanstekonsument" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tjanstekonsument" name="tjanstekonsument.id"
		from="${se.skl.tp.vagval.admin.core.entity.Tjanstekomponent.list()}"
		optionKey="id" required=""
		value="${anropsbehorighetInstance?.tjanstekonsument?.id}"
		optionValue="${{it?.hsaId + ' - ' + it?.beskrivning}}"
		class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anropsbehorighetInstance, field: 'tjanstekontrakt', 'error')} required">
	<label for="tjanstekontrakt">
		<g:message code="anropsbehorighet.tjanstekontrakt.label" default="Tjanstekontrakt" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="tjanstekontrakt" name="tjanstekontrakt.id"
		from="${se.skl.tp.vagval.admin.core.entity.Tjanstekontrakt.list()}"
		optionKey="id"
		required=""
		value="${anropsbehorighetInstance?.tjanstekontrakt?.id}"
		class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anropsbehorighetInstance, field: 'logiskAdressat', 'error')} required">
	<label for="logiskAdressat">
		<g:message code="anropsbehorighet.logiskAdressat.label" default="Logisk Adressat" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="logiskAdressat"
		name="logiskAdressat.id"
		from="${se.skl.tp.vagval.admin.core.entity.LogiskAdressat.list()}"
		optionKey="id"
		required=""
		value="${anropsbehorighetInstance?.logiskAdressat?.id}"
		optionValue="${{it?.hsaId + ' - ' + it?.beskrivning}}"
		class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anropsbehorighetInstance, field: 'fromTidpunkt', 'error')} required">
	<label for="fromTidpunkt">
		<g:message code="anropsbehorighet.fromTidpunkt.label" default="From Tidpunkt" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="fromTidpunkt" format="yyyy-MM-dd" precision="day"  value="${anropsbehorighetInstance?.fromTidpunkt}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: anropsbehorighetInstance, field: 'tomTidpunkt', 'error')} required">
	<label for="tomTidpunkt">
		<g:message code="anropsbehorighet.tomTidpunkt.label" default="Tom Tidpunkt" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="tomTidpunkt" format="yyyy-MM-dd" precision="day"  value="${anropsbehorighetInstance?.tomTidpunkt}"  />
</div>

<g:if test="${anropsbehorighetInstance.id}">

	<div
		class="fieldcontain ${hasErrors(bean: anropsbehorighetInstance, field: 'filter', 'error')} required">
		<label for="filter"> <g:message
				code="anropsbehorighet.filter.label" default="Filter" /> <span
			class="required-indicator">*</span>
		</label>

		<ul class="one-to-many">
			<g:each in="${anropsbehorighetInstance?.filter?}" var="f">
				<li><g:link controller="filter" action="show" id="${f.id}">
						${f?.encodeAsHTML()}
					</g:link></li>
			</g:each>
			<li class="add"><g:link controller="filter" action="create"
					params="['anropsbehorighet.id': anropsbehorighetInstance?.id]">
					${message(code: 'default.add.label', args: [message(code: 'filter.label', default: 'Filter')])}
				</g:link></li>
		</ul>

	</div>

</g:if>
