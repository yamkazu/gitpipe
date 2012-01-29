import org.gitpipe.User
import grails.web.RequestParameter

class UserController extends AbstractController {

    def beforeInterceptor = {
        this.bindUser()
    }

    def show() {
        model: [user: user, repositories: user.repositories]
    }

}
