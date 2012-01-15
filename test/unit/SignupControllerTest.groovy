import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.gitpipe.Role
import org.gitpipe.User
import org.gitpipe.UserRole

@TestFor(SignupController)
@Mock([User, Role, UserRole])
class SignupControllerTest {

    def springSecurityService

    void testForm() {
        controller.form()
        assert view == '/signup/form'
    }

    void testCreated() {
        controller.created()
        assert view == '/signup/created'
    }

    void testCreate_command_constraint_error() {
        def command = mockCommandObject(SignupCommand)
        assert !command.validate()

        controller.create(command)

        assert view == '/signup/form'
        assert model == [user: command]
    }
    
    void testCreate_command_constraints_error() {
        // nullable
        SignupCommand command = mockCommandObject(SignupCommand)
        assert !command.validate()
        assert command.errors['username'].code == 'nullable'
        assert command.errors['email'].code == 'nullable'
        assert command.errors['password'].code == 'nullable'

        // minsize
        command = mockCommandObject(SignupCommand)
        command.username = 'aa'
        command.password = 'aa'
        assert !command.validate()
        assert command.errors['username'].code == 'size.toosmall'
        assert command.errors['password'].code == 'size.toosmall'

        // maxsize
        command = mockCommandObject(SignupCommand)
        command.username = 'a' * 21
        command.password = 'a' * 21
        assert !command.validate()
        assert command.errors['username'].code == 'size.toobig'
        assert command.errors['password'].code == 'size.toobig'

        // matches
        command = mockCommandObject(SignupCommand)
        command.username = 'aaaa~'
        command.password = 'aaaa~'
        assert !command.validate()
        assert command.errors['username'].code == 'matches.invalid'
        assert command.errors['password'].code == 'matches.invalid'

        // email
        command = mockCommandObject(SignupCommand)
        command.email = 'aaa'
        assert !command.validate()
        assert command.errors['email'].code == 'email.invalid'

        // password compare
        command = mockCommandObject(SignupCommand)
        command.password = 'aaa'
        command.passwordConfirmation = 'bbb'
        assert !command.validate()
        assert command.errors['password'].code == 'notmutch'
    }

    void testCreate_domain_unique_error() {
        User existsUser = new User(username: 'aaa', email: 'aaa@example.com', password: 'aaa', enabled: true)
        existsUser.save()

        SignupCommand command = mockCommandObject(SignupCommand)
        command.username = 'aaa'
        command.email = 'aaa@example.com'
        command.password = 'aaa'
        command.passwordConfirmation = 'aaa'
        assert command.validate()

        controller.create(command)

        assert view == '/signup/form'
    }

    void testCreate() {
        SignupCommand command = mockCommandObject(SignupCommand)
        command.username = 'a' * 20
        command.email = 'aaa@example.com'
        command.password = 'aaa'
        command.passwordConfirmation = 'aaa'
        assert command.validate()

        controller.create(command)

        assert response.redirectedUrl == '/signup/created'
    }
}

