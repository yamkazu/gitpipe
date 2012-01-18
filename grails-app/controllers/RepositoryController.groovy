import grails.plugins.springsecurity.Secured
import org.gitpipe.RepositoryInfo
import org.gitpipe.User

/**
 * Created by IntelliJ IDEA.
 * User: yamkazu
 * Date: 12/01/17
 * Time: 2:47
 * To change this template use File | Settings | File Templates.
 */
class RepositoryController {

    def springSecurityService

    @Secured(['ROLE_USER'])
    def form = {
    }

    @Secured(['ROLE_USER'])
    def create = {
        def user = User.findByUsername springSecurityService.principal.username

        if (!user) {
            response.sendError(404)
            return
        }

        def repositoryInfo = new RepositoryInfo(params)
        repositoryInfo.user = user
        if (!repositoryInfo.save()) {
            render view: 'form', model: [repositoryInfo: repositoryInfo]
            return
        }

        // TODO
        // change before insert process
        repositoryInfo.createRepository()

        redirect(uri: "/${user.username}/${repositoryInfo.projectName}")
    }

    def show = {
        def user = User.findByUsername params.username
        if (!user) {
            response.sendError(404)
            return
        }

        def repositoryInfo = user.repositories.find { RepositoryInfo repositoryInfo ->
            params.project == repositoryInfo.projectName
        }
        if (!repositoryInfo) {
            response.sendError(404)
            return
        }

        render view: 'show', model: [user: user, repository: repositoryInfo]
    }

    def tree = {
        def user = User.findByUsername params.username
        if (!user) {
            response.sendError(404)
            return
        }

        def repositoryInfo = user.repositories.find { RepositoryInfo repositoryInfo ->
            params.project == repositoryInfo.projectName
        }
        if (!repositoryInfo) {
            response.sendError(404)
            return
        }

        def ref = params.ref
        def path = params.path

        render(contentType: "text/json") {
//            files = array {
//                for (Map values in files) {
//                    f name: file.name, objectId: file.objectId, type: file.objectType.name(), author: file.commit.authorIdent.name, message: file.commit.shortMessage, date: new Date(file.commit.commitTime * 1000L)
//                }
//            }
            files = repositoryInfo.findFilesInPath(ref, path)
        }
    }

}
