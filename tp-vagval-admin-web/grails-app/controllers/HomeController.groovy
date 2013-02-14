import org.apache.shiro.SecurityUtils

class HomeController {

    def index = {
		if (!SecurityUtils.subject.authenticated) {
			redirect(controller: "auth", action: "login")
		}
	}

}
