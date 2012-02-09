import grails.plugins.springsecurity.Secured
import grails.validation.Validateable
import org.gitpipe.User
import org.gitpipe.PublicKey
import org.springframework.http.HttpStatus
import grails.converters.JSON
import org.springframework.validation.FieldError

@Secured(['ROLE_USER'])
class AccountController extends AbstractController {

    def beforeInterceptor = {
        bindUser(springSecurityService.principal.username)
    }

    def springSecurityService

    def showAccount() {
        [user: user]
    }

    def updateAccount(AccountUpdateCommand command) {
        if (command.hasErrors()) {
            render view: 'showAccount', model: [user: command]
            return
        }

        bindData(user, command)
        user.save()

        flash.message = message(code: "account.update.successful")
        redirect controller: 'account'
    }

    def showAdmin() {
    }

    def updatePassword(PasswordUpdateCommand command) {
        if (command.hasErrors()) {
            render view: 'showAdmin', model: [user: command]
            return
        }

        bindData(user, command)
        user.save()

        flash.passwordMessage = message(code: "password.update.successful")
        redirect controller: 'account', action: 'admin'
    }

    def showPublicKeys() {
        withFormat {
            html {
                [publicKeys: user.publicKeys]
            }
            json {
                render user.publicKeys as JSON
            }
        }
    }
    
    def addPublicKey() {
        def key = new PublicKey(params)
        key.user = user
        
        if (!key.save()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value())
            render(contentType: "text/json") {
                args = array {
                    for (FieldError f in key.errors.fieldErrors) {
                        e = {
                            name = f.field
                            errorMessage = fieldError(bean: key, field: f.field)
                        }
                    }

                }
            }
            return
        }

        render key as JSON
    }

}

@Validateable
class AccountUpdateCommand {
    String name
    String email
    String location
    static constraints = {
        name maxSize: 20
        email blank: false, email: true
        location maxSize: 20
    }
}

@Validateable
class PasswordUpdateCommand {
    String password
    String passwordConfirmation
    static constraints = {
        password blank: false, size: 3..20, matches: "[a-zA-Z0-9\\-_!?]+", validator: { password, user ->
            password == user.passwordConfirmation ?: ['notmutch']
        }
    }
}

