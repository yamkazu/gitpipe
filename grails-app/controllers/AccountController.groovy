import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.validation.Validateable
import org.gitpipe.PublicKey
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError

@Secured(['ROLE_USER'])
class AccountController extends AbstractController {

    static defaultAction = "showAccount"

    def beforeInterceptor = {
        bindUser(springSecurityService.principal.username)
    }

    def springSecurityService
    def publicKeyService

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
        redirect(mapping: 'account')
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
                render(contentType: "text/json") {
                    array {
                        for (PublicKey pk in user.publicKeys) {
                            k = {
                                id = pk.id
                                title = pk.title
                                key = pk.key
                                url = createLink(mapping: 'account_ssh', params: [id: pk.id])
                            }
                        }
                    }
                }
//                render user.publicKeys as JSON
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

        publicKeyService.writeAuthorizedKeys()
        flash.sshEventMessage = 'ssh add successful'
        render ""
    }

    def updatePublicKey(Long id) {
        // call update
        def targetKey = PublicKey.findById(id)
        // TODO handle not found
        
        if (user != targetKey.user) {
            // security handle
            throw new RuntimeException('illegal access')
        }
        
        bindData(targetKey, params)
        
        if (!targetKey.save()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value())
            render(contentType: "text/json") {
                array {
                    for (FieldError f in targetKey.errors.fieldErrors) {
                        e = {
                            name = f.field
                            errorMessage = fieldError(bean: targetKey, field: f.field)
                        }
                    }

                }
            }
            return
        }

        publicKeyService.writeAuthorizedKeys()
        flash.sshEventMessage = 'ssh update successful'
        render ""
    }

    def deletePublicKey(Long id) {
        // call update
        def targetKey = PublicKey.findById(id)
        // TODO handle not found

        if (user != targetKey.user) {
            // security handle
            throw new RuntimeException('illegal access')
        }

        targetKey.delete()

        publicKeyService.writeAuthorizedKeys()
        flash.sshEventMessage = 'ssh delete successful'
        render ""
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
            password == user.passwordConfirmation ?: ['notmatch']
        }
    }
}

