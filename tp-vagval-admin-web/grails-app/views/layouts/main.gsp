<html>
    <head>
        <title><g:layoutTitle/></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />				
    </head>
    <body>
        <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
        </div>	
        <div class="logo">
          <table border="0" cellpadding="3" cellspacing="3">
            <tr>
              <td><img src="${resource(dir:'images',file:'logga.png')}" alt="Sjukvårdsrådgivningen" /></td>
              <td><p align="right">Version <g:meta name="app.version"/></p></td>
            </tr>
            <shiro:isLoggedIn>
                <tr>
         			    <td><g:message code="login.signedInAs" default="Signed in as" />: <shiro:principal/> (<g:link controller="auth" action="signOut"><g:message code="login.signOut" default="Sign out" /></g:link>)</td>
                </tr>
        		</shiro:isLoggedIn>
          </table>
        </div>
        <g:layoutBody />
    </body>	
</html>