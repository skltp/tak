
<html>
    <head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anvandare.label', default: 'Användare')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
		<a href="#create-anvandare" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			    <li><g:link class="create" action="create"><g:message code="default.create.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
        <div class="body">
            <h1><g:message code="anvandare.show" default="Show Anvandare" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <g:form>
                <g:hiddenField name="id" value="${anvandareInstance?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name"><g:message code="anvandare.id" default="Id" />:</td>
                                
                                <td valign="top" class="value">${fieldValue(bean: anvandareInstance, field: "id")}</td>
                                
                            </tr>
                            
                            <tr class="prop">
                                <td valign="top" class="name"><g:message code="anvandare.username" default="Användarnamn" />:</td>
                                
                                <td valign="top" class="value">${fieldValue(bean: anvandareInstance, field: "username")}</td> 
                            </tr>
                            
                            <tr class="prop">
                                <td valign="top" class="name"><g:message code="anvandare.roll" default="Roll" />:</td>
                                <td>${anvandareInstance.roles}</td>
                            </tr>
                            
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'edit', 'default': 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'delete', 'default': 'Delete')}" onclick="return confirm('${message(code: 'delete.confirm', 'default': 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
