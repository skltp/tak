<%=packageName ? "package ${packageName}\n\n" : ''%>import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException
import org.apache.shiro.SecurityUtils
import grails.converters.*
import org.apache.commons.logging.LogFactory

class ${className}Controller {

	private static final log = LogFactory.getLog(this)

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [${propertyName}List: ${className}.list(params), ${propertyName}Total: ${className}.count()]
    }

    def create() {
        [${propertyName}: new ${className}(params)]
    }

    def save() {
        def ${propertyName} = new ${className}(params)
        if (!${propertyName}.save(flush: true)) {
            render(view: "create", model: [${propertyName}: ${propertyName}])
            return
        }

        def principal = SecurityUtils.getSubject()?.getPrincipal();
        log.info "${domainClass.propertyName} \${${propertyName}.toString()} created by \${principal}:"
        log.info "\${${propertyName} as JSON}"
        flash.message = message(code: 'default.created.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])
        redirect(action: "show", id: ${propertyName}.id)
    }

    def show(Long id) {
        def ${propertyName} = ${className}.get(id)
        if (!${propertyName}) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), id])
            redirect(action: "list")
            return
        }

        [${propertyName}: ${propertyName}]
    }

    def edit(Long id) {
        def ${propertyName} = ${className}.get(id)
        if (!${propertyName}) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), id])
            redirect(action: "list")
            return
        }

        [${propertyName}: ${propertyName}]
    }

    def update(Long id, Long version) {
        def ${propertyName} = ${className}.get(id)
        if (!${propertyName}) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (${propertyName}.version > version) {<% def lowerCaseName = grails.util.GrailsNameUtils.getPropertyName(className) %>
                ${propertyName}.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: '${domainClass.propertyName}.label', default: '${className}')] as Object[],
                          "Another user has updated this ${className} while you were editing")
                render(view: "edit", model: [${propertyName}: ${propertyName}])
                return
            }
        }

        ${propertyName}.properties = params

        if (!${propertyName}.save(flush: true)) {
            render(view: "edit", model: [${propertyName}: ${propertyName}])
            return
        }

        def principal = SecurityUtils.getSubject()?.getPrincipal();
        log.info "${domainClass.propertyName} \${${propertyName}.toString()} updated by \${principal}:"
        log.info "\${${propertyName} as JSON}"
        flash.message = message(code: 'default.updated.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])
        redirect(action: "show", id: ${propertyName}.id)
    }

    def delete(Long id) {
        def ${propertyName} = ${className}.get(id)
        if (!${propertyName}) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), id])
            redirect(action: "list")
            return
        }

        try {
            ${propertyName}.delete(flush: true)
	        def principal = SecurityUtils.getSubject()?.getPrincipal();
	        log.info "${domainClass.propertyName} \${${propertyName}.toString()} deleted by \${principal}:"
	        log.info "\${${propertyName} as JSON}"
            flash.message = message(code: 'default.deleted.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException | UncategorizedSQLException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), id])
            redirect(action: "show", id: id)
        }
    }
}
