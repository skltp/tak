/**
 *
 */
package se.skltp.tak.core.entity

/**
 * @author muqkha
 *
 */
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException
import spock.lang.Specification

import java.sql.SQLException

abstract class AbstractCRUDControllerUnitTest extends Specification {

    abstract def getEntityName()
    abstract def getEntityClass()
    abstract def createValidEntity()
    abstract def createEntityWithNotSetDeletedDependencies()
    abstract def populateValidParams(paramsMap)
    abstract def populateInvalidParams(paramsMap)

    void setupUser() {

        def subject = [ getPrincipal   : { "iamauser" },
                       isAuthenticated: { true }
        ] as Subject

        ThreadContext.put(ThreadContext.SECURITY_MANAGER_KEY,
                [getSubject: { subject }] as SecurityManager)

        SecurityUtils.metaClass.static.getSubject = { subject }
    }


    void testSaveWithInvalidParams() {
        populateInvalidParams(params)

        controller.save()

        assert model != null
        assert view == '/' + getEntityName() + '/create'
    }

    void testSaveWithValidParams() {
        populateValidParams(params)

        controller.save()

        assert controller.flash.isCreated == true;
        assert controller.flash.message == 'default.created.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }


    void testUpdateWithInvalidParams() {
        Long id = createAndSaveEntity()

        populateInvalidParams(params)
        params.id = id

        controller.update()

        assert model != null
        assert view == '/' + getEntityName() + '/edit'
    }

    void testUpdateWithValidParams() {
        Long id = createAndSaveEntity()

        populateValidParams(params)
        params.id = id

        controller.update()

        assert flash.message == 'default.updated.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testUpdateWithOutdatedVersion() {
        Long id = createAndSaveEntity()

        populateValidParams(params)
        params.id = id
        params.version = -1

        controller.update()

        assert model[controller.getModelName()].errors.getFieldError('version')
        assert model != null
        assert view == '/' + getEntityName() + '/edit'
    }

    void testUpdateNonExistentEntity() {
        controller.update()

        assert flash.message == 'default.not.found.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/list'
    }


    void testDeleteWithPubVersion() {
        def entity = createValidEntity()
        entity.pubVersion = 1

        assert entity.save() != null
        assert entity.count() == 1

        params.id = entity.id

        controller.delete()

        assert entity.count() == 1
        assert entity.get(entity.id) != null
        assert flash.message == 'default.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/list'
    }

    void testDeleteWithNoPubVersion() {
        def entity = createValidEntity()

        assert entity.save() != null
        assert entity.count() == 1

        params.id = entity.id

        controller.delete()

        assert entity.count() == 0
        assert entity.get(entity.id) == null
        assert flash.message == 'default.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/list'
    }

    void testDeleteNonExistentEntity() {
        controller.delete()

        assert flash.message == 'default.not.found.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/list'
    }

    void testDeleteWithNotSetDeletedDependencies() {
        getEntityClass().metaClass.static.get = {createEntityWithNotSetDeletedDependencies()}

        controller.delete()

        assert flash.message == 'default.not.deleted.constraint.violation.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testDeleteWithDataIntegrityViolationException() {
        def entity = createValidEntity()
        getEntityClass().metaClass.static.get = {entity}
        entity.metaClass.delete = {Map map -> throw new DataIntegrityViolationException("intest")}

        controller.delete()

        assert flash.message == 'default.not.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testDeleteWithUncategorizedSQLException() {
        def entity = createValidEntity()
        getEntityClass().metaClass.static.get = {entity}
        entity.metaClass.delete = {Map map -> throw new UncategorizedSQLException("intest", "intest", new SQLException("intest"))}

        controller.delete()

        assert flash.message == 'default.not.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testDeleteWithPubVersionAndDataIntegrityViolationException() {
        def entity = createValidEntity()
        entity.setPubVersion("test PubVersion")
        getEntityClass().metaClass.static.get = {entity}
        entity.metaClass.save = {Map map -> throw new DataIntegrityViolationException("intest")}

        controller.delete()

        assert flash.message == 'default.not.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testDeleteWithPubVersionAndUncategorizedSQLException() {
        def entity = createValidEntity()
        entity.setPubVersion("test PubVersion")
        getEntityClass().metaClass.static.get = {entity}
        entity.metaClass.save = {Map map -> throw new UncategorizedSQLException("intest", "intest", new SQLException("intest"))}

        controller.delete()

        assert flash.message == 'default.not.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }


    private Long createAndSaveEntity() {
        def entity = createValidEntity();

        assert entity.save() != null
        entity.clearErrors()

        return entity.id;
    }
}
