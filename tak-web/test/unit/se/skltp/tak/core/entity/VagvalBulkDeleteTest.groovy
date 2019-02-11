package se.skltp.tak.core.entity

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.junit.Before
import spock.lang.Specification
import tak.web.BestallningConstructor
import tak.web.ObjectsConstructor


@TestMixin(GrailsUnitTestMixin)
@TestFor(VagvalController)
@Mock([Vagval])
class VagvalBulkDeleteTest extends Specification {

    Vagval vagval1
    Vagval vagval2

    void setupUser() {
        def subject = [ getPrincipal   : { "iamauser" },
                        isAuthenticated: { true }
        ] as Subject
        ThreadContext.put(ThreadContext.SECURITY_MANAGER_KEY,
                [getSubject: { subject }] as SecurityManager)
        SecurityUtils.metaClass.static.getSubject = { subject }
    }

    void createVagval() {
        vagval1 = ObjectsConstructor.createVagval(
                BestallningConstructor.LOGISK_ADRESS,
                BestallningConstructor.TJANSTEKOMPONENT,
                BestallningConstructor.TJANSTEKONTRAKT,
                BestallningConstructor.ADRESS,
                BestallningConstructor.RIVTA_PROFIL,
                BestallningConstructor.generateDateGreaterThan(new java.sql.Date(new Date().getTime())))
        vagval1.setId(123L)

        vagval2 = ObjectsConstructor.createVagval(
                BestallningConstructor.LOGISK_ADRESS2,
                BestallningConstructor.TJANSTEKOMPONENT2,
                BestallningConstructor.TJANSTEKONTRAKT2,
                BestallningConstructor.ADRESS2,
                BestallningConstructor.RIVTA_PROFIL2,
                BestallningConstructor.generateDateGreaterThan(new java.sql.Date(new Date().getTime())))
        vagval2.setId(124L)
    }

    @Before
    void before() {
        setupUser()
        createVagval()
    }

    void testBulkDelete() {
        vagval1.save(flush: true)
        vagval2.save(flush: true)
        Integer id11 = vagval1.id
        Integer id22 = vagval2.id
        assert Vagval.count() == 2
        String[] ids = new String[2]
        ids[0] = "" + id11
        ids[1] = "" + id22
        params.toDelete = ids
        response.reset()
        controller.bulkDelete()
        assert response.text == ''
        assert response.redirectUrl == '/vagval/deletelist'
        assert Vagval.count() == 0
    }
}

