
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
 
 <div id="create-anropsbehorighet" class="content scaffold-create" role="main">
  <h1>
   <g:message code="bulkadd.domain.label" args="[entityName]" />
  </h1>
  <g:if test="${flash.message}">
   <div class="message" role="status">
    ${flash.message}
   </div>
  </g:if>
  
  <g:hasErrors bean="${anropsbehorighetBulk}">
   <ul class="errors" role="alert">
    <g:eachError bean="${anropsbehorighetBulk}" var="error">
     <li
      <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
      <g:message error="${error}" />
     </li>
    </g:eachError>
   </ul>
  </g:hasErrors>
  
  <g:form action="bulkvalidate">
   <fieldset class="form">
    <div class="fieldcontain ${hasErrors(bean: anropsbehorighetBulk, field: 'integrationsavtal', 'error')} required">
     <label for="integrationsavtal"> 
      <g:message code="anropsbehorighet.integrationsavtal.label" default="Integrationsavtal" /> 
      <span class="required-indicator">*</span>
     </label>
     <g:textArea name="integrationsavtal" cols="40" rows="5"
                 maxlength="255" required=""
                 value="${anropsbehorighetBulk?.integrationsavtal}" />
    </div>

    <div
     class="fieldcontain ${hasErrors(bean: anropsbehorighetBulk, field: 'fromTidpunkt', 'error')} required">
     <label for="fromTidpunkt"> 
      <g:message code="anropsbehorighet.fromTidpunkt.label" default="From tidpunkt" /> 
       <span class="required-indicator">*</span>
     </label>
     <g:datePicker name="fromTidpunkt" format="yyyy-MM-dd" precision="day" value="${anropsbehorighetBulk?.fromTidpunkt}" />
    </div>

    <div
     class="fieldcontain ${hasErrors(bean: anropsbehorighetBulk, field: 'tomTidpunkt', 'error')} required">
     <label for="tomTidpunkt"> 
      <g:message code="anropsbehorighet.tomTidpunkt.label" default="Tom tidpunkt" />
      <span class="required-indicator">*</span>
     </label>
     <g:datePicker name="tomTidpunkt" format="yyyy-MM-dd" precision="day" value="${anropsbehorighetBulk?.tomTidpunkt}" default="${new Date(DateTime.now().plusYears(100).getMillis())}" />
    </div>

    <div
     class="fieldcontain ${hasErrors(bean: anropsbehorighetBulk, field: 'tjanstekonsument', 'error')} required">
     <label for="tjanstekonsument"> 
      <g:message code="anropsbehorighet.tjanstekonsument.label" default="Tjänstekonsument" /> 
      <span class="required-indicator">*</span>
     </label>
     <g:select 
      id="tjanstekonsument" 
      name="tjanstekonsument.id"
      from="${se.skltp.tak.core.entity.Tjanstekomponent.list()}"
      optionKey="id" 
      required=""
      value="${anropsbehorighetInstance?.tjanstekonsument?.id}"
      optionValue="${{it?.hsaId + ' - ' + it?.beskrivning}}"
      class="many-to-one" 
      style="width:50%" />
    </div>
 
    <!-- tjänstekontrakt - multiple selection -->
 
    <div class="fieldcontain ${hasErrors(bean: anropsbehorighetBulk, field: 'tjanstekontrakt', 'error')} required">
     <label for="tjanstekontrakt"> 
      <g:message code="anropsbehorighet.tjanstekontrakt.label" default="Tjänstekontrakt" /> 
      <span class="required-indicator">*</span>
     </label>
     
 <%--
     <g:select 
      optionKey="id" 
      optionValue="title"  
      multiple="multiple" 
      name="author.books" 
      id ="author_books" 
      from="${allBooks}" 
      value="${authorInstance?.books*.id}"/>
 --%>
 <%--
     <g:select name="cars"
          from="${Car.list()}"
          value="${person?.cars*.id}"
          optionKey="id"
          multiple="true" />
 --%>

     <%-- FIXME                                                                          --%>
     <%-- This binds to a list of Strings containing ids                                 --%>
     <%-- Which is not what we want                                                      --%>
     <%-- Instead we want it to bind to a list of Tjanstekontrakt                        --%> 
     <%-- As in the examples above (books, cars)                                         --%>
     <%-- There is workaround code in the controller to instantiate objects from the ids --%>
     <g:select 
      id="tjanstekontrakts"
      name="tjanstekontrakts"
      from="${se.skltp.tak.core.entity.Tjanstekontrakt.list()}"
      optionKey="id" 
      required=""
      value="${anropsbehorighetBulk?.tjanstekontrakts*.id}"
      multiple="true"
      class="many-to-one" 
      style="width:50%" />
    </div>
 
    <!-- logisk adress  -->

    <div class="fieldcontain ${hasErrors(bean: anropsbehorighetBulk, field: 'logiskAdressBulk', 'error')} required">
     <label for="logiskAdressBulk"> 
      <g:message code="anropsbehorighet.logiskadress.label" /> 
      <span class="required-indicator">*</span>
     </label>
     <g:textArea style="width:50%"
                 name="logiskAdressBulk" 
                 cols="100" 
                 rows="5"
                 maxlength="15000" 
                 required=""
                 value="${anropsbehorighetBulk?.logiskAdressBulk}" />
    </div>
   </fieldset>

   <!--  -->
   
   <fieldset class="buttons">
    <g:submitButton 
     name="create" 
     class="save"
     value="${message(code: 'default.button.importdata.label', default: 'Validera')}" />
   </fieldset>
  </g:form>
 </div>
</body>
</html>
