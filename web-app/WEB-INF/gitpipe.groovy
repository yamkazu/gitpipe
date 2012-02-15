//
// gitpipe.groovy
//

@GrabResolver(name = 'jgit', root = 'http://download.eclipse.org/jgit/maven')
@Grab(group = 'org.eclipse.jgit', module = 'org.eclipse.jgit.pgm', version = '1.2.0.201112221803-r')
class Main extends org.eclipse.jgit.pgm.Main {}

def parseCmd(String c) {
    c.startsWith('git-') ? c.substring('git-'.length()) : c
}

def parseRepo(String r) {
    def repo = r.replaceAll('\'', '')
    if (repo.startsWith('/')) repo = repo.substring('/'.length())
    if (repo.endsWith(".git")) repo = repo.substring(0, repo.size() - ".git".size())
    repo.split('/')
}

def (cmd, repo) = System.env['SSH_ORIGINAL_COMMAND'].split(' ')
cmd = parseCmd(cmd)
def (owner, reponame) = parseRepo(repo)
username = args[0]

new Main().run([cmd, System.properties['user.home'] + "/.gitpipe/repositories/" + owner + "/" + reponame + ".git"] as String[])

