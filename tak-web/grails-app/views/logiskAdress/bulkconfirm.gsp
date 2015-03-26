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
<!DOCTYPE html>
<html>

 <head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'logiskaAdresser.label', default: 'logiskaAdresser')}" />
  <title><g:message code="default.create.label" args="[entityName]" /></title>
  <r:require module="select2" />
 </head>
 
 <body>
  <a href="#create-logiskaAdresser" class="skip" tabindex="-1">
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

 
 <div id="bulkconfirm-logiskaAdresser" class="content scaffold-create" role="main">
 
  <h1>
   <g:message code="default.create.label" args="[entityName]" />
  </h1>

  <g:if test="${flash.message}">
   <div class="message" role="status">
    ${flash.message}
   </div>
  </g:if>
  
  <ol class="property-list logiskAdress">
  
    <li class="fieldcontain">
      <span id="logiskAdress-label" class="property-label">
        <g:message code="logiskAdress.label" />
      </span>
      <g:each in="${logiskaAdresserBulk.acceptedLines}" var="logiskAdress">
        <span class="property-value" aria-labelledby="filter-label">
            ${logiskAdress.key} - ${logiskAdress.value?.encodeAsHTML()}
        </span>
      </g:each>        
    </li>

    <li class="fieldcontain">
      <span id="logiskAdress-label" class="property-label">
        <g:message code="logiskAdress.rejectedlogiskAdress.label" />
      </span>
      <g:each in="${logiskaAdresserBulk.rejectedLines}" var="string">
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
     value="${message(code: 'button.importdata.label')}" />
   </fieldset>
  </g:form>
 </div>
</body>
</html>
