package gitpipe

import org.gitpipe.Project
import org.gitpipe.User

class GitpipeTagLib {

    def springSecurityService

    def forkEnable = { attr, body ->
        assert attr.project

        def project = attr.project
        def user = User.findByUsername(springSecurityService.authentication.name)

        if (!user) return
        if (project.user.id == user.id) return
        if (Project.findByUserAndName(user, project.name)) return

        out << body()
    }

}
