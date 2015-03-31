
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
<%@ page import="se.skltp.tak.core.entity.Anropsbehorighet"%>
<%@	page import="org.joda.time.DateTime"%>
<!DOCTYPE html>
<html>

 <head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet')}" />
  <title><g:message code="default.create.label" args="[entityName]" /></title>
  <r:require module="select2" />
 </head>
 
 <body>
  <a href="#create-anropsbehorighet" class="skip" tabindex="-1">
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
  </ul>
 </div>
 
 <div id="bulkconfirm-anropsbehorighet" class="content scaffold-create" role="main">
  <h1>
   <g:message code="default.create.label" args="[entityName]" />
  </h1>
  <g:if test="${flash.message}">
   <div class="message" role="status">
    ${flash.message}
   </div>
  </g:if>
  
  
  
  <ol class="property-list tjanstekontrakt">
  
    <li class="fieldcontain">
      <span id="integrationsavtal-label" class="property-label">
        <g:message code="tjanstekontrakt.integrationsavtal.label" default="Integrationsavtal" />
      </span>
      <span class="property-value" aria-labelledby="integrationsavtal-label">
        <g:fieldValue bean="${anropsbehorighetBulk}" field="integrationsavtal"/>
      </span>
    </li>
  
    <li class="fieldcontain">
      <span id="tjanstekonsument-label" class="property-label">
        <g:message code="anropsbehorighetBulk.tjanstekonsument.label" default="Tjanstekonsument" />
      </span>
      <span class="property-value" aria-labelledby="tjanstekonsument-label">
        ${anropsbehorighetBulk?.tjanstekonsument?.encodeAsHTML()} - ${anropsbehorighetBulk?.tjanstekonsument?.beskrivning?.encodeAsHTML()}
      </span>
    </li>

    <li class="fieldcontain">
      <span id="fromTidpunkt-label" class="property-label">
        <g:message code="anropsbehorighet.fromTidpunkt.label" default="From Tidpunkt" />
      </span>
      <span class="property-value" aria-labelledby="fromTidpunkt-label">
        <g:formatDate date="${anropsbehorighetBulk?.fromTidpunkt}" />
      </span>
    </li>
                
    <li class="fieldcontain">
      <span id="tomTidpunkt-label" class="property-label">
        <g:message code="anropsbehorighet.tomTidpunkt.label" default="Tom Tidpunkt" />
      </span>
      <span class="property-value" aria-labelledby="tomTidpunkt-label">
        <g:formatDate date="${anropsbehorighetBulk?.tomTidpunkt}" />
      </span>
    </li>

    <li class="fieldcontain">
      <span id="tjanstekontrakt-label" class="property-label">
        <g:message code="anropsbehorighet.tjanstekontrakt.label" default="Tjanstekontrakt" />
      </span>
      <g:each in="${anropsbehorighetBulk.tjanstekontrakts}" var="tjanstekontrakt">
        <span class="property-value" aria-labelledby="filter-label">
            ${tjanstekontrakt?.namnrymd}
        </span>
      </g:each>        
    </li>

    <li class="fieldcontain">
      <span id="logiskAdress-label" class="property-label">
        <g:message code="anropsbehorighet.logiskAdress.label" default="Logisk Adress" />
      </span>
      <g:each in="${anropsbehorighetBulk.logiskaAdresser}" var="logiskAdress">
        <span class="property-value" aria-labelledby="filter-label">
            ${logiskAdress.hsaId} - ${logiskAdress.beskrivning?.encodeAsHTML()}
        </span>
      </g:each>        
    </li>

    <li class="fieldcontain">
      <span id="logiskAdress-label" class="property-label">
        <g:message code="anropsbehorighet.rejectedlogiskAdress.label" default="HSAId hittades inte" />
      </span>
      <g:each in="${anropsbehorighetBulk.rejectedLogiskAdress}" var="string">
        <span class="property-value" aria-labelledby="filter-label">
            ${string?.encodeAsHTML()}
        </span>
      </g:each>        
    </li>
  
  </ol>

  <!--  -->
   
  <g:form action="bulksave">
   <fieldset class="buttons">
    <g:submitButton 
     name="create" 
     class="save"
     value="${message(code: 'default.button.importdata.label', default: 'Skapa')}" />
   </fieldset>
  </g:form>
 </div>
</body>
</html>
