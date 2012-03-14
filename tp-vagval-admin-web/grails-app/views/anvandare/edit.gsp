
<html>
    <head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anvandare.label', default: 'Användare')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
 		<a href="#edit-anvandare" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.create.label" args="[entityName]" /></g:link></li>
			</ul>
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
                                    <label for="username"><g:message code="anvandare.username" default="Anvandarnamn" />:</label>
                                </td>
								<td>${fieldValue(bean: anvandareInstance, field: "username")}</td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password"><g:message code="anvandare.password" default="password" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: anvandareInstance, field: 'password', 'errors')}">
                                    <input type="text" id="password" name="password" value="${fieldValue(bean:anvandareInstance,field:'password')}"/>

                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="roll"><g:message code="anvandare.role" default="Roll" />:</label>
                                </td>
                                <td>
                                <g:link action="editRole" id="${anvandareInstance.roles.id}">${anvandareInstance.roles.name}</g:link>
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
