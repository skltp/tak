
<html>
    <head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anvandare.label', default: 'Användare')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
		<g:javascript library="prototype" />
    </head>
    <body>
		<a href="#create-anvandare" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" default="Skapa Användare" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <g:hasErrors bean="${anvandareInstance}">
            <div class="errors">
                <g:renderErrors bean="${anvandareInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="username"><g:message code="anvandare.username" default="username" />:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: anvandareInstance, field: 'username', 'errors')}">
                                    <input type="text" id="username" name="username" value="${fieldValue(bean:anvandareInstance,field:'username')}"/>

                                </td>
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
					            <td>Roll:</td>
		  						<td><g:select id="roles" name="roles.id" from="${AnvandareRoll.list()}" 
		  							noSelection="${['null':'Välj en...']}" 
		  							optionKey="id"
		  							required="*" value="${anvandareRollInstance?.id}"
		  							class="many-to-one"/>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'create', 'default': 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
