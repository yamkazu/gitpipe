import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryCache.FileKey
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.transport.ReceivePack
import org.eclipse.jgit.transport.UploadPack

//
// gitpipe.groovy
//
// handle ssh command.
// original command ex:
//   git-upload-pack 'test/test.git'
//   git-receive-pack 'test/test.git'
//

@GrabResolver(name = 'jgit', root = 'http://download.eclipse.org/jgit/maven')
@Grab(group = 'org.eclipse.jgit', module = 'org.eclipse.jgit.pgm', version = '1.2.0.201112221803-r')
class Main {

    static final String REPOSITORY_BASE = System.properties['user.home'] + "/.gitpipe/repositories"

    def command
    def user
    def project

    Repository repository

    void execute(request) {
        parse(request)
        open()
        handle()
    }

    void open() {
        FileKey key = FileKey.lenient(new File("${REPOSITORY_BASE}/${user}/${project}.git"), FS.DETECTED)
        repository = key.open(true)
    }

    void handle() {
        if (isUploadPack()) {
            handleUploadPack()
        } else /* if (isReceivePack()) */ {
            handleReceivePack()
        }
    }

    void handleUploadPack() {
        new UploadPack(repository).upload(System.in, System.out, System.err)
    }

    void handleReceivePack() {
        new ReceivePack(repository).receive(System.in, System.out, System.err)
    }

    void parse(String request) {
        def matcher = (request =~ /^(git-upload-pack|git-receive-pack) '(.+?)\/(.+?)(\.git)?'/)
        if (!matcher.matches()) throw new IllegalArgumentException("illegal request: ${request}")
        (request, command, user, project) = matcher[0]
    }

    boolean isUploadPack() {
        command == 'git-upload-pack'
    }

    boolean isReceivePack() {
        command == 'git-receive-pack'
    }

}

new Main().execute(System.env['SSH_ORIGINAL_COMMAND'])


