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
import org.junit.Before
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.UncategorizedSQLException
import spock.lang.Specification
import java.sql.SQLException

abstract class AbstractCRUDControllerUnitTest extends Specification {

    abstract def getEntityName()
    abstract def getEntityClass()
    abstract def createEntity(Map paramsMap)
    abstract def createEntityWithNotSetDeletedDependencies()
    abstract def populateValidParams(Map paramsMap)
    abstract def populateInvalidParams(Map paramsMap)

    void setupUser() {

        def subject = [ getPrincipal   : { "iamauser" },
                       isAuthenticated: { true }
        ] as Subject

        ThreadContext.put(ThreadContext.SECURITY_MANAGER_KEY,
                [getSubject: { subject }] as SecurityManager)

        SecurityUtils.metaClass.static.getSubject = { subject }
    }

    @Before
    void before() {
        setupUser()
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
        def entity = createAndSaveEntity()
        populateInvalidParams(params)
        params.id = entity.id

        controller.update()

        assert model != null
        assert view == '/' + getEntityName() + '/edit'
    }

    void testUpdateWithValidParams() {
        def entity = createAndSaveEntity()
        populateValidParams(params)
        params.id = entity.id

        controller.update()

        assert flash.message == 'default.updated.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testUpdateWithOutdatedVersion() {
        def entity = createAndSaveEntity()
        populateValidParams(params)
        params.id = entity.id
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
        def entity = createAndSaveEntity()
        entity.pubVersion = 1
        params.id = entity.id

        controller.delete()

        assert entity.count() == 1
        assert entity.get(entity.id) != null
        assert flash.message == 'default.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/list'
    }

    void testDeleteWithNoPubVersion() {
        def entity = createAndSaveEntity()
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
        def entity = createAndSaveEntity()
        params.id = entity.id
        entity.metaClass.delete = {Map map -> throw new DataIntegrityViolationException("intest")}

        controller.delete()

        assert flash.message == 'default.not.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testDeleteWithUncategorizedSQLException() {
        def entity = createAndSaveEntity()
        params.id = entity.id
        entity.metaClass.delete = {Map map -> throw new UncategorizedSQLException("intest", "intest", new SQLException("intest"))}

        controller.delete()

        assert flash.message == 'default.not.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testDeleteWithPubVersionAndDataIntegrityViolationException() {
        def entity = createAndSaveEntity()
        entity.pubVersion = 1
        params.id = entity.id
        entity.metaClass.save = {Map map -> throw new DataIntegrityViolationException("intest")}

        controller.delete()

        assert flash.message == 'default.not.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }

    void testDeleteWithPubVersionAndUncategorizedSQLException() {
        def entity = createAndSaveEntity()
        entity.pubVersion = 1
        params.id = entity.id
        entity.metaClass.save = {Map map -> throw new UncategorizedSQLException("intest", "intest", new SQLException("intest"))}

        controller.delete()

        assert flash.message == 'default.not.deleted.message'
        assert response.redirectedUrl == '/' + getEntityName() + '/show/0'
    }


    private AbstractVersionInfo createAndSaveEntity() {
        def paramsMap = [:]
        populateValidParams(paramsMap)
        def entity = createEntity(paramsMap);

        assert entity.save() != null
        assert entity.count() == 1

        entity
    }
}
