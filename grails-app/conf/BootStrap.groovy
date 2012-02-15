import org.gitpipe.Role
import org.gitpipe.User
import org.gitpipe.UserRole
import org.gitpipe.Project
import javax.servlet.ServletContext

class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        initDefaultUser()
        initConfigDir()
        initCommand(servletContext)
        initAuthorizedKeys()
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
    
    def initCommand(ServletContext servletContext) {
        def command = new File(grailsApplication.config.gitpipe.command)
        if (!command.exists()) {
            command << new File(servletContext.getRealPath("/WEB-INF/gitpipe.groovy")).bytes
        }
    }

    def initAuthorizedKeys() {
        def created = new File(grailsApplication.config.gitpipe.authorized_keys.createdfile)
        if (!created.exists()) {
            println System.getProperty("user.home")
            def original = new File(System.getProperty("user.home") + "/.ssh/authorized_keys")
            println original.exists()
            if (original.exists()) {
                def backup = new File(grailsApplication.config.gitpipe.config.dir + "/authorized_keys.org")
                backup << original.bytes
            }
            created.createNewFile()
        }
    }

    def destroy = {
    }

    // FIXME this is debug code
    def initDefaultUser() {
        def userRole = new Role(authority: 'ROLE_USER').save(flush: true)
        def testUser = new User(username: 'test', enabled: true, password: 'test', email: 'test@example.com', createDate: new Date())
        
        testUser.name = 'Test User'
        testUser.company = 'Test Company'
        testUser.location = 'Tokyo, Japan'
        testUser.save(flush: true)
        UserRole.create testUser, userRole, true

        assert User.count() == 1
        assert Role.count() == 1
        assert UserRole.count() == 1

        new Project(name: "test", description: "test description", user: testUser).save(flush: true)
    }


}
