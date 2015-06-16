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
<%@ page import="se.skltp.tak.core.entity.LogiskAdress"%>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'logiskAdress.label', default: 'LogiskAdress')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
    <r:require module="select2" />
  </head>
  
  <body>
    <a href="#create-logiskAdress" class="skip" tabindex="-1">
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
   
    <div id="create-logiskAdress" class="content scaffold-create" role="main">
      <h1>
        <g:message code="logiskAdress.bulkcreate.label" />
      </h1>
      <g:if test="${flash.message}">
        <div class="message" role="status"> ${flash.message} </div>
      </g:if>
      <g:hasErrors bean="${logiskAdressInstance}">
        <ul class="errors" role="alert">
          <g:eachError bean="${logiskAdressInstance}" var="error">
            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
              <g:message error="${error}" />
            </li>
          </g:eachError>
        </ul>
      </g:hasErrors>
     
      <g:form action="bulkvalidate">
        <fieldset class="form">
        
         <!-- logisk adress bulk -->
     
          <div class="fieldcontain ${hasErrors(field: 'logiskAdressBulk', 'error')} required">
            <label for="logiskAdressBulk"> 
              <g:message code="logiskaAdresserBulk.label" /> 
              <span class="required-indicator">*</span>
            </label>
            <g:textArea style="width:50%"
                        name="logiskaAdresserBulk" 
                        cols="100" 
                        rows="5"
                        maxlength="15000" 
                        required=""
                        value="${logiskaAdresserBulk}" />
          </div>
        </fieldset>
        
        <fieldset class="buttons">
          <g:submitButton name="validate" class="save" value="${message(code:'button.validate.label')}" />
        </fieldset>
      </g:form>
    </div>
  </body>
</html>
