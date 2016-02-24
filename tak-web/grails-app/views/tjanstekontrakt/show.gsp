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

<%@ page import="se.skltp.tak.core.entity.Tjanstekontrakt"%>
<!DOCTYPE html>
<html>
 <head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'tjanstekontrakt.label', default: 'Tjanstekontrakt')}" />
  <title><g:message code="default.show.label" args="[entityName]" /></title>
 </head>
 <body>
  <a href="#show-tjanstekontrakt" class="skip" tabindex="-1">
   <g:message code="default.link.skip.label" default="Skip to content&hellip;" />
  </a>
  
  <div class="nav" role="navigation">
   <ul>
    <li>
     <a class="home" href="${createLink(uri: '/')}">
      <g:message code="default.home.label" />
     </a>
    </li>
    <li>
     <g:link class="list" action="list">
      <g:message code="default.list.label" args="[entityName]" />
     </g:link>
    </li>
    <li>
     <g:link class="create" action="create">
      <g:message code="default.new.label" args="[entityName]" />
     </g:link>
    </li>
   </ul>
  </div>
  
  <div id="show-tjanstekontrakt" class="content scaffold-show" role="main">
   <h1>
    <g:message code="default.show.label" args="[entityName]" />
   </h1>
   <g:if test="${flash.message}">
    <div class="message" role="status">
     ${flash.message}
    </div>
   </g:if>
   
   <ol class="property-list tjanstekontrakt">
    <g:if test="${tjanstekontraktInstance?.namnrymd}">
     <li class="fieldcontain">
      <span id="namnrymd-label" class="property-label">
       <g:message code="tjanstekontrakt.namnrymd.label" default="Namnrymd" />
      </span> 
      <span class="property-value" aria-labelledby="namnrymd-label">
       <g:fieldValue bean="${tjanstekontraktInstance}" field="namnrymd" />
      </span>
     </li>
    </g:if>

   <li class="fieldcontain">
    <span id="majorVersion-label" class="property-label">
     <g:message code="tjanstekontrakt.majorVersion.label" default="Major version" />
    </span>
    <span class="property-value" aria-labelledby="majorVersion-label">
     <g:fieldValue bean="${tjanstekontraktInstance}" field="majorVersion" />
    </span> 
    <span id="minorVersion-label" class="property-label">
     <g:message code="tjanstekontrakt.minorVersion.label" default="Minor version" />
    </span>
    <span class="property-value" aria-labelledby="minorVersion-label">
     <g:fieldValue bean="${tjanstekontraktInstance}" field="minorVersion" />
    </span>
   </li>

   <g:if test="${tjanstekontraktInstance?.beskrivning}">
    <li class="fieldcontain">
     <span id="beskrivning-label" class="property-label">
      <g:message code="tjanstekontrakt.beskrivning.label" default="Beskrivning" />
     </span>
     <span class="property-value" aria-labelledby="beskrivning-label">
      <g:fieldValue bean="${tjanstekontraktInstance}" field="beskrivning" />
     </span>
    </li>
   </g:if>

   <g:if test="${tjanstekontraktInstance?.anropsbehorigheter}">
    <li class="fieldcontain">
     <span id="anropsbehorigheter-label" class="property-label">
      <g:message code="tjanstekontrakt.anropsbehorigheter.label" default="Anropsbehorigheter" />
     </span> 
     <g:each in="${tjanstekontraktInstance.anropsbehorigheter}" var="a">
      <span class="property-value" aria-labelledby="anropsbehorigheter-label">
      <g:link controller="anropsbehorighet" action="show" id="${a.id}">
        <% 
          def logiskAdressBeskrivning = a?.logiskAdress?.beskrivning?.size() > 30? a?.logiskAdress?.beskrivning.substring(0, 30) : a?.logiskAdress?.beskrivning
          def tjansteKonsumentBeskrivning = a?.tjanstekonsument?.beskrivning.size() > 30? a?.tjanstekonsument?.beskrivning.substring(0, 30) : a?.tjanstekonsument?.beskrivning
        %>
        ${"${a?.tjanstekonsument?.encodeAsHTML()} [${tjansteKonsumentBeskrivning?.encodeAsHTML()}] - ${a?.logiskAdress?.encodeAsHTML()} [${logiskAdressBeskrivning?.encodeAsHTML()}"}]
      </g:link>
     </span>
     </g:each>
    </li>
   </g:if>

   <g:if test="${tjanstekontraktInstance?.vagval}">
    <li class="fieldcontain">
     <span id="vagval-label" class="property-label">
      <g:message code="tjanstekontrakt.vagval.label" />
     </span> 
     <g:each in="${tjanstekontraktInstance.vagval}" var="v">
      <span class="property-value" aria-labelledby="vagval-label"> 
       <g:link controller="vagval" action="show" id="${v.id}">
        <% 
         def logiskAdressBeskrivning = v?.logiskAdress?.beskrivning?.size() > 30? v?.logiskAdress?.beskrivning.substring(0, 30) : v?.logiskAdress?.beskrivning
         def anropsAdressAdress = v?.anropsAdress?.adress.size() > 30? v?.anropsAdress?.adress.substring(0, 30) : v?.anropsAdress?.adress
        %>
        ${"${v?.logiskAdress?.encodeAsHTML()} [${logiskAdressBeskrivning?.encodeAsHTML()}] - ${v?.anropsAdress?.encodeAsHTML()} [${anropsAdressAdress?.encodeAsHTML()}"}]
       </g:link>
      </span>
     </g:each>
    </li>
   </g:if>
   
   <g:if test="${tjanstekontraktInstance?.id}">
		<li class="fieldcontain">
			<span id="uniqueid-label" class="property-label"><g:message code="default.uniqueId.label" /></span>
			<span class="property-value" aria-labelledby="uniqueid-label"><g:fieldValue bean="${tjanstekontraktInstance}" field="id"/></span>					
		</li>
	</g:if>
   
   <g:if test="${tjanstekontraktInstance?.pubVersion}">
		<li class="fieldcontain">
			<span id="pubVersion-label" class="property-label"><g:message code="default.pubVersion.label" /></span>
			<span class="property-value" aria-labelledby="pubVersion-label"><g:fieldValue bean="${tjanstekontraktInstance}" field="pubVersion"/></span>					
		</li>
	</g:if>
	
	<g:if test="${tjanstekontraktInstance?.updatedTime}">
		<li class="fieldcontain">
			<g:if test="${flash.isCreated}">
				<span id="updatedTime-label" class="property-label"><g:message code="default.createdTime.label" /></span>
			</g:if>
			<g:else>
				<span id="updatedTime-label" class="property-label"><g:message code="default.updatedTime.label" /></span>
			</g:else>			
			<span class="property-value" aria-labelledby="updatedTime-label"><g:formatDate date="${tjanstekontraktInstance?.updatedTime}" /></span>					
		</li>
	</g:if>
	
	<g:if test="${tjanstekontraktInstance?.updatedBy}">
		<li class="fieldcontain">
			<g:if test="${flash.isCreated}">
				<span id="updatedBy-label" class="property-label"><g:message code="default.createdBy.label" /></span>
			</g:if>
			<g:else>
				<span id="updatedBy-label" class="property-label"><g:message code="default.updatedBy.label" /></span>
			</g:else>
			<span class="property-value" aria-labelledby="updatedBy-label"><g:fieldValue bean="${tjanstekontraktInstance}" field="updatedBy"/></span>					
		</li>
	</g:if>
	
	<g:if test="${tjanstekontraktInstance?.deleted}">
		<li class="fieldcontain">
			<span id="deleted-label" class="property-label"><g:message code="default.deleted.label" /></span>
			<span class="property-value" aria-labelledby="deleted-label"><g:formatBoolean boolean="${tjanstekontraktInstance?.deleted}" /></span>		
		</li>
	</g:if>
	
  </ol>
  
  <g:form>
   <fieldset class="buttons">
    <g:hiddenField name="id" value="${tjanstekontraktInstance?.id}" />
    <g:link class="edit" action="edit" id="${tjanstekontraktInstance?.id}">
     <g:message code="default.button.edit.label" />
    </g:link>
    <g:if test="${!tjanstekontraktInstance?.deleted}">
    	<g:actionSubmit class="delete" 
                    action="delete" 
                    value="${message(code: 'default.button.delete.label')}"
                    onclick="return confirm('${message(code: 'default.button.delete.confirm.message')}');" 
    	/>
    </g:if>
   </fieldset>
  </g:form>
 </div>
</body>
</html>
