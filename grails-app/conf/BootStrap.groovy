import org.gitpipe.Role
import org.gitpipe.User
import org.gitpipe.UserRole

class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        initDefaultUser()
        initConfigDir()
    }

    def initConfigDir() {
        def configDir = new File(grailsApplication.config.gitpipe.config.dir)
        if (!configDir.exists()) {
            configDir.mkdir()
        }

        def repositoriesDir = new File(grailsApplication.config.gitpipe.repositories.dir)
        if (!repositoriesDir.exists()) {
            repositoriesDir.mkdir()
        }
    }

    def destroy = {
    }

    // FIXME this is debug code
    def initDefaultUser() {
        def userRole = new Role(authority: 'ROLE_USER').save(flush: true)
        def testUser = new User(username: 'test', enabled: true, password: 'test', email: 'test@example.com')
        testUser.save(flush: true)
        UserRole.create testUser, userRole, true

        assert User.count() == 1
        assert Role.count() == 1
        assert UserRole.count() == 1
    }


}
