modules = {
    application {
        resource url:'js/application.js'
    }
	
	select2 {
		dependsOn 'jquery'
		defaultBundle false
			
		resource url: '/js/select2/select2.js'
		resource url: '/js/select2/select2_locale_sv.js'
		resource url: '/js/select2/vagval_select.js'
		resource url: '/css/select2/select2.css'
	}
}