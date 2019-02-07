package se.skltp.tak.core.entity

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.junit.Before
import se.skltp.tak.web.command.VagvalBulk
import tak.web.BestallningConstructor
import tak.web.ObjectsConstructor


@TestMixin(GrailsUnitTestMixin)
@TestFor(VagvalController)
@Mock([Vagval])
class VagvalBulkDeleteTest {
    Vagval vagval1 = ObjectsConstructor.createVagval(
            BestallningConstructor.LOGISK_ADRESS,
            BestallningConstructor.TJANSTEKOMPONENT,
            BestallningConstructor.TJANSTEKONTRAKT,
            BestallningConstructor.ADRESS,
            BestallningConstructor.RIVTA_PROFIL,
            BestallningConstructor.generateDateGreaterThan(new java.sql.Date(new Date().getTime())))

    Vagval vagval2 = ObjectsConstructor.createVagval(
            BestallningConstructor.LOGISK_ADRESS2,
            BestallningConstructor.TJANSTEKOMPONENT2,
            BestallningConstructor.TJANSTEKONTRAKT2,
            BestallningConstructor.ADRESS2,
            BestallningConstructor.RIVTA_PROFIL2,
            BestallningConstructor.generateDateGreaterThan(new java.sql.Date(new Date().getTime())))

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

    /*void testBulkDelete() {
        vagval1.metaClass.id = 123
        assert vagval1.id == 123
        vagval2.metaClass.id = 124
        assert vagval2.id == 124
        Object id1 = vagval1.save(flush:true)
        Object id2 = vagval2.save(flush:true)

        Integer id11 = vagval1.id
        Integer id22 = vagval2.id

        String[] ids = new String[2]
        ids[0] = "" + id11
        ids[1] = "" + id22
        System.out.println("IDS is now :::" + ids + ":::")
        params.toDelete = ids
        response.reset()
        controller.bulkDelete()
        String s = response.text
        assert response.text == ''
        String ss = response.redirectUrl
        assert response.redirectUrl == '/vagval/deletelist'
        String sss = flash.message
        assert sss == ""
    }*/
}

