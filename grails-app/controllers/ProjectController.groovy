import grails.plugins.springsecurity.Secured
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.gitpipe.Project
import org.gitpipe.User

class ProjectController extends AbstractController {

    private static final Log LOG = LogFactory.getLog(ProjectController.class)

    def springSecurityService
    def grailsApplication

    @Secured(['ROLE_USER'])
    def form() {
    }

    @Secured(['ROLE_USER'])
    def create() {
        bindUser(springSecurityService.principal.username)
        def project = new Project(params)
        project.user = user
        if (!project.save()) {
            render view: 'form', model: [project: project]
            return
        }
        // TODO
        // change before insert process
        project.repository.create()

        redirect(mapping: 'project', params: [username: user.username, project: project.name])
    }

    def fork() {
        bindUser()
        bindProject()

        def forkMe = User.findByUsername(springSecurityService.principal.username)
        project.fork(forkMe)

        redirect(mapping: 'project', params: [username: forkMe.username, project: project.name])
    }

    def show() {
        bindUser()
        bindProject()

        def repository = project.repository

        forward controller: 'repository', action: 'tree', params: [username: user.username, project: project.name, ref: repository.defaultBranch]
    }

}
