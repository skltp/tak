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
<%-- 
<table>
		
		<tbody>
			<tr>
				
				<div class="fieldcontain ${hasErrors(bean: pubVersionInstance, field: 'formatVersion', 'error')} ">
					<td><label for="formatVersion">
						<g:message code="pubVersion.formatVersion.label" default="Format Version" />		
						</label>
					</td>
					<td>
						<g:field type="number" name="formatVersion" value="${pubVersionInstance.formatVersion}" />
					</td>
				</div> 
			</tr>
			<tr>--%>
				<div class="fieldcontain ${hasErrors(bean: pubVersionInstance, field: 'kommentar', 'error')} required">
					<label for="kommentar">
						<g:message code="pubVersion.kommentar.label" default="x_Kommentar" />
						<span class="required-indicator">*</span>
					</label>
					<g:textArea name="kommentar" cols="40" rows="5" maxlength="255" required="" value="${pubVersionInstance?.kommentar}" />
				</div>
				<%-- 
			</tr>	
		</tbody>
	</table>	--%>	


<%--
<div class="fieldcontain ${hasErrors(bean: pubVersionInstance, field: 'kommentar', 'error')} ">
	<label for="kommentar">
		<g:message code="pubVersion.kommentar.label" default="Kommentar" />
		
	</label>
	<g:textField name="kommentar" value="${pubVersionInstance?.kommentar}" />
</div>

 --%>

