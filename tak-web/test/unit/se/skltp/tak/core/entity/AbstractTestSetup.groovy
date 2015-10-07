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

import spock.lang.Specification

class AbstractTestSetup extends Specification {

  void setupUser() {
	  
	  def subject = [ getPrincipal: { "iamauser" },
		  isAuthenticated: { true }
		] as Subject
	
	  ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,
		  [ getSubject: { subject } ] as SecurityManager )

	  SecurityUtils.metaClass.static.getSubject = { subject }
  }
  
  
}
