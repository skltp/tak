The following changes have been made to a standard Grails 2.2.0 app:

* Executed 'grails quick-start' to generate Shiro artifacts, then removed domain/Shiro*.groovy
  which we don't use

* src/templates/scaffolding
  - Controller.groovy:
    * Audit logging added to save(), update() and delete()
    * Additional catch of UncategorizedSQLException in delete()
  - list.gsp
    * Changed default columns to display from 6 -> 10
  - renderEditor.template:
    * Commented out (property.oneToMany && !property.bidirectional) to renderManyToMany()
  - _form.groovy
    * Added conditional if tag if the property is a collection and the entity is about to be created
      (i.e. when its 'id' is null)

* views/layouts/main.gsp
  - Customized "logo" div, to include Authentication info

* views/auth/login.gsp
  - Customized page, added titles and default keyboard focus

* conf/BuildConfig.groovy
  - Added pom true + redundant dependency to core

* conf/Config.groovy
  - Configured logging

* conf/UrlMappings.groovy
  - Added "/"(controller:'home', action:'index') , needed for requiring login for index.gsp

* controllers/AuthController.groovy
  - Changed unauthorized() to render default view (unauthorized.gsp)

* realms/JsecDbRealm.groovy
  - Customized to use own domain class, and removed unused methods
  
* web-app/css/main.css
  - Changed color for box-shadows to #999999
  - Changed color for links (a:link, a:visited, a:hover) to #000000;
  - Changed color for h1 to #c40260;
  - Changed background table hover color for .logo (.logo tr:hover {background: #ffffff;})
  - Changed color for required-indicator to #c40260
  - Changed color for table for hovers (th:hover, tr:hover) to #999999;
  
* added conf/ShiroSecurityFilters.groovy
* added conf/hibernate/hibernate.cfg.xml
* added views/auth/unauthorized.gsp
* added controllers/HomeController.groovy
* moved views/index.gsp to views/home/index.gsp 