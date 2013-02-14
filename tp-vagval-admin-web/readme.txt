The following changes have been made to a standard Grails app:

* Installed plugins i18n-templates:1.1.0.1 and jsecurity:0.4.1

* Executed 'grails quick-start' to generate JSecurity artifacts, then removed domain/Jsec*.groovy
  which we don't use

* src/templates/scaffolding
  - Controller.groovy:
    * Audit logging added to save(), update() and delete()
    * Additional catch of UncategorizedSQLException in delete()
  - list.gsp
    * Changed default columns to display from 6 -> 10
  - renderEditor.template:
    * Commented out (property.oneToMany && !property.bidirectional) to renderManyToMany()
    * Changed renderStringEditor implementation to not use standard gsp tags due to grails
      defect related to JPA domain entities.

* views/layouts/main.gsp
  - Customized "logo" div, to include Authentication info

* views/auth/login.gsp
    - Customized page, added titles and default keyboard focus

* conf/Config.groovy
  - Configured logging

* conf/DataSource.groovy
  - configClass = "org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration",
    to enable JPA annotated Entities

* conf/UrlMappings.groovy
  - Added "/index.gsp"(view:"/index"), needed for JSecurity-redirects

* controllers/AuthController.groovy
  - Changed unauthorized() to render default view (unauthorized.gsp)

* realms/JsecDbRealm.groovy
  - Customized to use own domain class, and removed unused methods
  
* web-app/css/main.css
  - Changed color for h1 to rgb(196, 2, 96);
  
* added conf/SecurityFilters.groovy
* added conf/hibernate/hibernate.cfg.xml
* added views/auth/unauthorized.gsp
