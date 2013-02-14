<html>
    <head>
        <title><g:message code="application.title" default="Welcome to TP ${grailsApplication.metadata.'app.version'}" /></title>
		<meta name="layout" content="main" />
    </head>
    <body>
        <h1 style="margin-left:20px;"><g:message code="application.title" default="Welcome to TP ${grailsApplication.metadata.'app.version'}" /></h1>
        <p style="margin-left:20px;width:80%"><g:message code="application.description"
        default="This is the Administration Application for the Service Catalogue of TP ${grailsApplication.metadata.'app.version'}" /></p>
        <br/>
        <hr/>
        <br/>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul>
              <li>
                <g:link controller="rivVersion">
                  <g:message code="topmenu.rivVersion" default="Administrate Riv Versions" />
                </g:link>
              </li>
              <li>
                <g:link controller="tjanstekontrakt">
                  <g:message code="topmenu.tjanstekontrakt" default="Administrate Service Contracts" />
                </g:link>
              </li>
              <li>
                <g:link controller="tjanstekomponent">
                  <g:message code="topmenu.tjanstekomponent" default="Administrate Service Components" />
                </g:link>
              </li>
              <li>
                <g:link controller="logiskAdressat">
                  <g:message code="topmenu.logiskAdressat" default="Administrate Logical Addressees" />
                </g:link>
              </li>
              <li>
                <g:link controller="logiskAdress">
                  <g:message code="topmenu.logiskAdress" default="Administrate Logical Addresses" />
                </g:link>
              </li>
              <li>
                <g:link controller="anropsbehorighet">
                  <g:message code="topmenu.anropsbehorighet" default="Administrate Call Authorizations" />
                </g:link>
              </li>
            </ul>
        </div>
        <shiro:hasRole name="Admin">
          <br/>
	      <hr/>
      	  <br/>
          <div class="dialog" style="margin-left:20px;width:60%;">
            <ul>
              <li>
                <g:link controller="anvandare">
                  <g:message code="topmenu.anvandare" default="Administrate Users" />
                </g:link>
              </li>
            </ul>
          </div>
        </shiro:hasRole>
        <br/>
        <hr/>
        <br/>
    </body>
</html>