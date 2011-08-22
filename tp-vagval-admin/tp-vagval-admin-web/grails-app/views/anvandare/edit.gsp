
<%@ page import="se.skl.tp.vagval.admin.web.entity.Anvandare" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="anvandare.edit" default="Edit Anvandare" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}"><g:message code="home" default="Home" /></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="anvandare.list" default="Anvandare List" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="anvandare.new" default="New Anvandare" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="anvandare.edit" default="Edit Anvandare" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <g:hasErrors bean="${anvandareInstance}">
            <div class="errors">
                <g:renderErrors bean="${anvandareInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${anvandareInstance?.id}" />
                <g:hiddenField name="version" value="${anvandareInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="anvandarnamn"><g:message code="anvandare.anvandarnamn" default="Anvandarnamn" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: anvandareInstance, field: 'anvandarnamn', 'errors')}">
                                    <input type="text" id="anvandarnamn" name="anvandarnamn" value="${fieldValue(bean:anvandareInstance,field:'anvandarnamn')}"/>

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="losenord"><g:message code="anvandare.losenord" default="Losenord" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: anvandareInstance, field: 'losenord', 'errors')}">
                                    <input type="text" id="losenord" name="losenord" value="${fieldValue(bean:anvandareInstance,field:'losenord')}"/>

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="administrator"><g:message code="anvandare.administrator" default="Administrator" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: anvandareInstance, field: 'administrator', 'errors')}">
                                    <g:checkBox name="administrator" value="${anvandareInstance?.administrator}" />

                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'update', 'default': 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'delete', 'default': 'Delete')}" onclick="return confirm('${message(code: 'delete.confirm', 'default': 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
