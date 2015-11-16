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
  
  void testSaveEntity(AbstractController controller, String viewUrl, String redirectUrl) {
	  controller.save()

	  assert model.entity != null
	  assert view == viewUrl

	  response.reset()

	  populateValidParams(params)
	  controller.save()
	  
	  assert response.redirectedUrl == redirectUrl
	  assert controller.flash.message != null
	  assert model.entity.count() == 1
  }
  
  void testUpdateEntity(AbstractController controller, String entityName) {
	  testUrlOnUpdate(controller, '/' + entityName + '/list')
	  	  
	  Long id = testSaveBeforeUpdate()
	  	  
	  testUpdateWithInvalidParams(controller, id, '/' + entityName + '/edit')	  
	  testUpdateWithValidParams(controller, id, '/' + entityName + '/show/0')	  
	  testUpdateWithOutdatedVersion(controller, id, '/' + entityName + '/edit')
  }
  
  void testDeleteEntity(AbstractController controller, String redirectUrl) {
	  controller.delete()
	  assert flash.message != null
	  assert response.redirectedUrl == redirectUrl

	  response.reset()

	  def entity = getEntity()
	  assert entity.save() != null
	  assert entity.count() == 1

	  params.id = entity.id

	  controller.delete()

	  assert entity.count() == 1
	  assert entity.get(entity.id) != null
	  assert response.redirectedUrl == redirectUrl
  }
  
  void testUrlOnUpdate(AbstractController, String redirectUrl) {
	  controller.update()	  
	  assert flash.message != null
	  assert response.redirectedUrl == redirectUrl
	  response.reset()	  
  }
  
  Long testSaveBeforeUpdate() { 
	  
	  def entity = getEntity();
	  
	  assert entity.save() != null
	  entity.clearErrors()
	  
	  return entity.id;
  }
  
  void testUpdateWithInvalidParams(AbstractController controller, Long id, String viewUrl) {	
	 params.id = id
	 populateInvalidParams(params)

	  controller.update()

	  assert view == viewUrl
	  assert model.entity != null
  }
  
  void testUpdateWithValidParams(AbstractController controller, Long id, String redirectUrl) {
	  
	  populateValidParams(params)
	  params.id = id
	  controller.update()

	  assert response.redirectedUrl == redirectUrl
	  assert flash.message != null
	  
	  response.reset()
  }
  
  void testUpdateWithOutdatedVersion(AbstractController controller, Long id, String viewUrl) {	  	  
	  populateValidParams(params)
	  params.id = id
	  params.version = -1
	  controller.update()

	  assert view == viewUrl
	  assert model.entity != null
	  assert model.entity.errors.getFieldError('version')
	  assert flash.message != null
	  
	  response.reset()	  
  }
  
  
    
}