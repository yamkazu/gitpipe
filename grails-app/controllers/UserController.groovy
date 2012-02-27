class UserController extends AbstractController {

    def beforeInterceptor = {
        this.bindUser()
    }

    def show() {
        [user: user, repositories: user.repositories]
    }

}
