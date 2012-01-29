import grails.plugins.springsecurity.Secured
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.gitpipe.Project

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

        redirect(uri: createLink(mapping: 'project', params: [username: user.username, project: project.name]))
    }
    
    def show() {
        bindUser()
        bindProject()

        def repository = project.repository

        render view: '/repository/tree', model: [user: user, project: project, 'ref': repository.defaultBranch, path: '', commit: repository.getLastCommit()]
    }

}
