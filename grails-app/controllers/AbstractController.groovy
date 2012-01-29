import org.gitpipe.Project
import org.gitpipe.User
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.context.request.RequestContextHolder

abstract class AbstractController {

    protected User user
    protected Project project

    protected def bindUser(String username) {
        username = username ?: params.username
        user = User.findByUsername(username)
        if (!user) {
            throw new NoSuchRequestHandlingMethodException('user not found')
        }
    }

    protected def bindProject() {
        if (!user) {
            bindUser()
        }
        project = Project.findByUserAndName(user, params.project)
        if (!project) {
            throw new NoSuchRequestHandlingMethodException('project not found')
        }
    }

}
