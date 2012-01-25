import org.gitpipe.User
import org.springframework.http.HttpStatus

class UserController {

    def show() {
        def user = User.findByUsername params.username

        if (!user) {
            response.sendError(404)
            return
        }

        render view: 'show', model: [user: user, repositories: user.repositories]
    }

}
