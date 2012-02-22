import grails.validation.Validateable
import org.gitpipe.Role
import org.gitpipe.User
import org.gitpipe.UserRole

class SignupController {

    def form = {
        render view: 'form'
    }

    def create(SignupCommand command) {
        if (command.hasErrors()) {
            render view: 'form', model: [user: command]
            return
        }

        def user = new User(params)
        user.createDate = new Date()
        user.enabled = true

        if (!user.save()){
            render view: 'form', model: [user: user]
            return
        }

        def role = Role.findByAuthority('ROLE_USER')
        UserRole.create user, role, true

        redirect action: 'created'
    }

    def created() {
        render view: 'created'
    }

}

@Validateable
class SignupCommand {
    String username
    String password
    String email
    String passwordConfirmation
    static constraints = {
        username blank: false, size: 3..20, matches: "[a-zA-Z0-9\\-_]+"
        password blank: false, size: 3..20, matches: "[a-zA-Z0-9\\-_!?]+", validator: { password, user ->
            password == user.passwordConfirmation ?: ['notmatch']
        }
        email blank: false, email: true
    }
}
