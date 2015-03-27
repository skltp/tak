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
<%@ page import="se.skltp.tak.core.entity.Vagval"%>
<!DOCTYPE html>
<html>

  <head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'vagval.label')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
    <r:require module="select2" />
  </head>
 
  <body>
    <a href="#create-vagval" class="skip" tabindex="-1">
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
     
    <div id="bulkconfirm-vagval" class="content scaffold-create" role="main">
     
      <h1>
       <g:message code="default.create.label" args="[entityName]" />
      </h1>
    
      <g:if test="${flash.message}">
        <div class="message" role="status">
          ${flash.message}
        </div>
      </g:if>
      
      <ol class="property-list vagval">
      
        <li class="fieldcontain">
          <span id="fromTidpunkt-label" class="property-label">
            <g:message code="vagval.fromTidpunkt.label" />
          </span>
          <span class="property-value" aria-labelledby="fromTidpunkt-label">
            <g:formatDate date="${vagvalBulk?.fromTidpunkt}" />
          </span>
        </li>
                    
        <li class="fieldcontain">
          <span id="tomTidpunkt-label" class="property-label">
            <g:message code="vagval.tomTidpunkt.label" />
          </span>
          <span class="property-value" aria-labelledby="tomTidpunkt-label">
            <g:formatDate date="${vagvalBulk?.tomTidpunkt}" />
          </span>
        </li>
    
        <li class="fieldcontain">
          <span id="tjanstekontrakt-label" class="property-label">
            <g:message code="vagval.tjanstekontrakt.label" />
          </span>
          <span class="property-value" aria-labelledby="filter-label">
            ${vagvalBulk.tjanstekontrakt.namnrymd?.encodeAsHTML()}
          </span>
        </li>
        
        <li class="fieldcontain">
          <span id="anropsAdress-label" class="property-label">
            <g:message code="vagval.anropsAdress.label" />
          </span>
          <span class="property-value" aria-labelledby="anropsAdress-label">
            ${vagvalBulk.anropsAdress.tjanstekomponent?.hsaId} - 
            ${vagvalBulk.anropsAdress.tjanstekomponent?.beskrivning?.encodeAsHTML()} - 
            ${vagvalBulk.anropsAdress?.adress?.encodeAsHTML()} - 
            ${vagvalBulk.anropsAdress.rivTaProfil?.namn?.encodeAsHTML()}
          </span>
        </li>
    
        <!-- logisk adress -->
    
        <li class="fieldcontain">
          <span id="logiskAdress-label" class="property-label">
            <g:message code="vagval.logiskAdress.label" default="Logisk Adress" />
          </span>
          <g:each in="${vagvalBulk.logiskaAdresser}" var="logiskAdress">
            <span class="property-value" aria-labelledby="filter-label">
                ${logiskAdress.hsaId} - ${logiskAdress.beskrivning?.encodeAsHTML()}
            </span>
          </g:each>        
        </li>
    
        <li class="fieldcontain">
          <span id="logiskAdress-label" class="property-label">
            <g:message code="vagval.rejectedLogiskAdress.label" />
          </span>
          <g:each in="${vagvalBulk.rejectedLogiskAdress}" var="string">
            <span class="property-value" aria-labelledby="filter-label">
              ${string}
            </span>
          </g:each>        
        </li>
  
      </ol>

      <!--  -->
   
      <g:form action="bulksave">
        <fieldset class="buttons">
          <g:submitButton 
            name="import" 
            class="save"
            value="${message(code: 'button.importdata.label')}" 
          />
        </fieldset>
      </g:form>
    </div>
  </body>
</html>
