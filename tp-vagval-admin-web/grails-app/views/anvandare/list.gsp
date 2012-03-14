
<html>
    <head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anvandare.label', default: 'Användare')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
		<a href="#create-anvandare" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.create.label" args="[entityName]" /></g:link></li>
			</ul>
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
                        
                   	    <g:sortableColumn property="username" title="username" titleKey="anvandare.username" />
                        
                   	    <g:sortableColumn property="administrator" title="Administrator" titleKey="anvandare.administrator" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${anvandareInstanceList}" status="i" var="anvandareInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="edit" id="${anvandareInstance.id}">${fieldValue(bean: anvandareInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: anvandareInstance, field: "username")}</td>
                        
                        	<g:each in="${anvandareInstance.roles}" var="roll">
                            	<td>${roll.name}</td>
                        	</g:each>
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
