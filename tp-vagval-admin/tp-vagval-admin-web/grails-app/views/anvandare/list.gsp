
<%@ page import="se.skl.tp.vagval.admin.web.entity.Anvandare" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="anvandare.list" default="Anvandare List" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}"><g:message code="home" default="Home" /></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="anvandare.new" default="New Anvandare" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="anvandare.list" default="Anvandare List" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	    <g:sortableColumn property="id" title="Id" titleKey="anvandare.id" />
                        
                   	    <g:sortableColumn property="anvandarnamn" title="Anvandarnamn" titleKey="anvandare.anvandarnamn" />
                        
                   	    <g:sortableColumn property="administrator" title="Administrator" titleKey="anvandare.administrator" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${anvandareInstanceList}" status="i" var="anvandareInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${anvandareInstance.id}">${fieldValue(bean: anvandareInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: anvandareInstance, field: "anvandarnamn")}</td>
                        
                            <td><g:formatBoolean boolean="${anvandareInstance.administrator}" /></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${anvandareInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
