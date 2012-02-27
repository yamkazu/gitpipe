import org.gitpipe.Project
import org.gitpipe.User

abstract class AbstractController {

    protected User user
    protected Project project

    protected def bindUser(String username) {
        username = username ?: params.username
        user = User.findByUsername(username)
        if (!user) {
            throw new NoSuchUserException()
        }
    }

    protected def bindProject() {
        if (!user) {
            bindUser()
        }
        project = Project.findByUserAndName(user, params.project)
        if (!project) {
            throw new NoSuchProjectException()
        }
    }

}

class NoSuchUserException extends Exception {}
class NoSuchProjectException extends Exception {}