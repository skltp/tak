package se.skltp.tak.core.entity


import grails.plugin.spock.IntegrationSpec
import groovy.mock.interceptor.MockFor
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext
import org.junit.Before
import se.skltp.tak.web.entity.TAKSettings
import tak.web.alerter.MailAlerterService


class MailAlerterServiceIntegrationSpec extends IntegrationSpec  {
	def greenMail
	def mailAlerter

	void setupUser() {
		def subject = [ getPrincipal   : { "admin" },
						isAuthenticated: { true }
		] as Subject

		ThreadContext.put(ThreadContext.SECURITY_MANAGER_KEY,
				[getSubject: { subject }] as SecurityManager)

		SecurityUtils.metaClass.static.getSubject = { subject }
	}

	@Before
	void before() {
//		setupUser()
		}


	def cleanup() {
	}

	void "mail alert"() {
		given:
			def pVersion = new PubVersion()
			pVersion.id = 13
			pVersion.kommentar = 'my komment'
			pVersion.utforare = 'test'

		when:
			mailAlerter.alertOnPublicering(pVersion)

		then: 'have send 1 message'
			mailAlerter.toAddress.length == greenMail.getReceivedMessages().length

		then: 'content are correct'
			greenMail.getReceivedMessages()[0].content.contains(pVersion.kommentar)
			greenMail.getReceivedMessages()[0].content.contains(pVersion.id.toString())
			greenMail.getReceivedMessages()[0].content.contains(pVersion.utforare)
			greenMail.getReceivedMessages()[0].content.contains(pVersion.time.toString())
	}


}