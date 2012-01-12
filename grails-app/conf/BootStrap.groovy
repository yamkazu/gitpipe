import org.gitpipe.Role
import org.gitpipe.User
import org.gitpipe.UserRole

class BootStrap {

    def init = { servletContext ->
        initDefaultUser()
    }

    def destroy = {
    }

    def initDefaultUser() {
        def userRole = new Role(authority: 'ROLE_USER').save(flush: true)
        def testUser = new User(username: 'test', enabled: true, password: 'test')
        testUser.save(flush: true)
        UserRole.create testUser, userRole, true

        assert User.count() == 1
        assert Role.count() == 1
        assert UserRole.count() == 1
    }

}
