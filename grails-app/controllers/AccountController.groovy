import grails.plugins.springsecurity.Secured
import org.gitpipe.User
import grails.validation.Validateable

@Secured(['ROLE_USER'])
class AccountController {

    def springSecurityService

    def showAccount = {
        def user = User.findByUsername springSecurityService.principal.username

        if (!user) {
            response.sendError(404)
            return
        }

        render view: 'showAccount', model: [user: user]
    }

    def updateAccount = { AccountUpdateCommand command ->
        if (command.hasErrors()) {
            render view: 'showAccount', model: [user: command]
            return
        }

        def user = User.findByUsername springSecurityService.principal.username

        if (!user) {
            response.sendError(404)
            return
        }

        user.name = command.name
        user.email = command.email
        user.location = command.location
        user.save()

//        flash.message = "User not found for id ${params.id}"
        flash.message = message(code: "account.update.successful")
        redirect controller: 'account'
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
