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
<%@ page import="se.skltp.tak.core.entity.PubVersion" %>

<div class="fieldcontain ${hasErrors(bean: pubVersionInstance, field: 'formatVersion', 'error')} ">
	<label for="formatVersion">
		<g:message code="pubVersion.formatVersion.label" default="Format Version" />
		
	</label>
	<g:field type="number" name="formatVersion" value="${pubVersionInstance.formatVersion}" />
</div>

<div class="fieldcontain ${hasErrors(bean: pubVersionInstance, field: 'kommentar', 'error')} ">
	<label for="kommentar">
		<g:message code="pubVersion.kommentar.label" default="Kommentar" />
		
	</label>
	<g:textField name="kommentar" value="${pubVersionInstance?.kommentar}" />
</div>
<%--
<div class="fieldcontain ${hasErrors(bean: pubVersionInstance, field: 'time', 'error')} ">
	<label for="time">
		<g:message code="pubVersion.time.label" default="Time" />
		
	</label>
	<g:datePicker name="time" precision="day" value="${pubVersionInstance?.time}" />
</div>

<div class="fieldcontain ${hasErrors(bean: pubVersionInstance, field: 'utforare', 'error')} ">
	<label for="utforare">
		<g:message code="pubVersion.utforare.label" default="Utforare" />
		
	</label>
	<g:textField name="utforare" value="${pubVersionInstance?.utforare}" /> --%>
</div>

