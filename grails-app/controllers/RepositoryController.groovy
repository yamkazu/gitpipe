import grails.web.RequestParameter
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.gitpipe.User
import org.gitpipe.git.GpBlame
import org.gitpipe.git.GpDiff
import org.gitpipe.git.GpRaw
import org.gitpipe.git.GpRepository
import org.springframework.http.HttpStatus

class RepositoryController extends AbstractController {

    def grailsApplication
    GpRepository repository

    def beforeInterceptor = {
        bindUser()
        bindProject()
        repository = project.repository

        if (!repository.hasCommits() && (actionName != this.&tree.method || params.path)) {
            redirect(mapping: 'project', params: [username: user.username, project: project.name])
            return false
        }
    }

    def afterInterceptor = {
    }

    def commits(String ref, String path, int page) {
        withFormat {
            html {
                [user: user, project: project, ref: ref, path: path]
            }
            json {
                render(contentType: "text/json") {
                    def cs = repository.log(ref, path, page * maxCommitsFetchSize(), maxCommitsFetchSize())
                    commits = array {
                        for (commit in cs) {
                            c {
                                id = commit.id
                                date = commit.date.format("yyyy-MM-dd HH:mm:sss")
                                url = createCommitLink(commit.id)
                                shortMessage = commit.message
                                author = {
                                    name = commit.author.name
                                    def u = User.findByEmail(commit.author.email)
                                    if (u) {
                                        username = user.username
                                        url = createUserLink(user)
                                    }
                                }
                            }
                        }
                    }
                    if (!(cs.size() < maxCommitsFetchSize())) {
                        next = createCommitsLink(ref, path, ++page)
                    }
                }
            }
        }
    }

    def commit(@RequestParameter('id') String commitId) {
        withFormat {
            html {
                def commit = repository.logLimit1(commitId)
                def map = [user: user, project: project, id: commitId, commit: commit]

                def commitUser = User.findByEmail(commit.author.email)
                if (commitUser) {
                    map.commitUser = commitUser
                }
                map
            }
            json {
                render(contentType: "text/json") {
                    GpDiff d = repository.diff(commitId)
                    entries = array {
                        for (entry in d.entries) {
                            e = {
                                type = entry.type
                                if (entry.newId && ObjectId.zeroId().name != entry.newId) {
                                    newFile = {
                                        id = entry.newId
                                        path = entry.newPath
                                        mode = entry.newMode
                                        blobUrl = createBlobLink(d.newCommit.id, entry.newPath)
                                    }

                                }
                                if (entry.oldId && ObjectId.zeroId().name != entry.oldId) {
                                    oldFile = {
                                        id = entry.oldId
                                        path = entry.oldPath
                                        mode = entry.oldMode
                                        blobUrl = createBlobLink(d.oldCommit.id, entry.oldPath)
                                    }
                                }
                                addLine = entry.addLine
                                removeLine = entry.removeLine
                                diff = entry.diff
                            }
                        }
                    }
                }
            }
        }
    }

    def tree(String ref, @RequestParameter('path') String treePath) {
        if (!repository.hasCommits()) {
            withFormat {
                html {
                    render view: 'setup', model: [user: user, project: project]
                }
                json {
                    response.setStatus(HttpStatus.NOT_FOUND, 'This is Empty Repository')
                }
            }
            return
        }
        withFormat {
            html {
                def map = [user: user, project: project, ref: ref, path: treePath]

                def commit = repository.logLimit1(ref)
                if (commit) {
                    map.commit = commit
                    def commitUser = User.findByEmail(commit.author.email)
                    if (commitUser) {
                        map.commitUser = commitUser
                    }
                }
                map
            }
            json {
                render(contentType: "text/json") {
                    current = createCurrentTreeLink(ref, treePath)
                    historyUrl = createCommitsLink(ref, treePath)
                    if (treePath) {
                        parents = createParentsTreeLink(ref, treePath)
                    }
                    files = array {
                        for (file in repository.findFilesInPath(ref, treePath)) {
                            f = {
                                id = file.id
                                name = file.name
                                type = file.type
                                path = file.path
                                url = createTreeLink(file.type, ref, file.path)
                                commit = {
                                    def commit = repository.logLimit1(ref, file.path)
                                    id = commit.id
                                    date = commit.date.format("yyyy-MM-dd HH:mm:sss")
                                    url = createCommitLink(commit.id)
                                    shortMessage = commit.message
                                    author = {
                                        name = commit.author.name
                                        def u = User.findByEmail(commit.author.email)
                                        if (u) {
                                            username = user.username
                                            url = createUserLink(user)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    def blob(String ref, String path) {
        withFormat {
            html {
                def commit = repository.logLimit1(ref)
                def map = [user: user, project: project, ref: ref, path: path, commit: commit]
                def commitUser = User.findByEmail(commit.author.email)
                if (commitUser) {
                    map.commitUser = commitUser
                }
                map
            }
            json {
                // FIXME BIG DATA 対応
                // 取得最大値を設ける
                render(contentType: "text/json") {
                    def blob = repository.raw(ref, path)
                    current = createCurrentBlobLink(ref, path)
                    parents = createParentsTreeLink(ref, path)
                    historyUrl = createCommitsLink(ref, path)
                    blameUrl = createBlameLink(ref, path)
                    rawUrl = createRawLink(ref, path)
                    mode = blob.mode
                    size = blob.size
                    if (blob.isBinary()) {
                        file_type = 'binary'
                    } else {
                        file_type = getViewerType(toFileName(path))
                        data = blob.getFileAsString(Constants.CHARSET)
                    }
                }
            }
        }
    }

    def blame(String ref, String path) {
        withFormat {
            html {
                def commit = repository.logLimit1(ref)
                def map = [user: user, project: project, ref: ref, path: path, commit: commit]
                def commitUser = User.findByEmail(commit.author.email)
                if (commitUser) {
                    map.commitUser = commitUser
                }
                map
            }
            json {
                render(contentType: "text/json") {
                    GpBlame blame = repository.blame(ref, path)

                    current = createCurrentBlobLink(ref, path)
                    parents = createParentsTreeLink(ref, path)
                    historyUrl = createCommitsLink(ref, path)
                    rawUrl = createRawLink(ref, path)

                    raw = {
                        mode = blame.raw.mode
                        size = blame.raw.size
                        if (blame.raw.isBinary()) {
                            type = 'binary'
                        } else {
                            type = getViewerType(toFileName(path))
                            file = blame.raw.getFileAsString()
                        }
                    }

                    size = blame.size
                    entries = array {
                        for (entry in blame.entries) {
                            e = {
                                start = entry.start
                                end = entry.end
                                length = entry.length
                                commit {
                                    id = entry.commit.id
                                    date = entry.commit.date.format("yyyy-MM-dd")
                                    url = createCommitLink(entry.commit.id)
                                    author = {
                                        name = entry.commit.author.name
                                        def u = User.findByEmail(entry.commit.author.email)
                                        if (u) {
                                            username = user.username
                                            url = createUserLink(user)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    def raw(String ref, String path) {
        def raw = repository.raw(ref, path)

//        if (!raw.) {
//            // handle not found
//        }

        response.setContentType(getMimeType(raw))
        response.setHeader("Content-disposition", "filename=${toFileName(path)}")
        response.outputStream << raw.file
    }

    private Map<String, String> createCurrentTreeLink(String ref, String path) {
        if (!path) {
            return [name: project.name, path: "", url: createTreeLink(Constants.TYPE_TREE, ref, "")] // this is root link
        }
        [name: new File(path).name, path: path, url: createTreeLink(Constants.TYPE_TREE, ref, path)]
    }

    private Map<String, String> createCurrentBlobLink(String ref, String path) {
        [name: new File(path).name, path: path, url: createBlobLink(ref, path)]
    }

    private List<Map<String, String>> createParentsTreeLink(String ref, String path) {
        def parents = []
        File parent = new File(path).parentFile;

        while (parent != null) {
            parents << [name: parent.name, path: parent.path, url: createTreeLink(Constants.TYPE_TREE, ref, parent.path)]
            parent = parent.parentFile
        }

        parents << [name: project.name, path: "", url: createTreeLink(Constants.TYPE_TREE, ref, "")] // this is root link
        parents.reverse()
    }

    private String createBlobLink(String ref, String path) {
        createLink(mapping: 'repository_blob', params: [username: user.username, project: project.name, ref: ref, path: path]).toString()
    }

    private String createBlameLink(String ref, String path) {
        createLink(mapping: 'repository_blame', params: [username: user.username, project: project.name, ref: ref, path: path]).toString()
    }

    private String createRawLink(String ref, String path) {
        createLink(mapping: 'repository_raw', params: [username: user.username, project: project.name, ref: ref, path: path]).toString()
    }

    private String createCommitLink(String commit) {
        createLink(mapping: 'repository_commit', params: [username: user.username, project: project.name, id: commit]).toString()
    }

    private String createTreeLink(String type, String ref, String path) {
        if (type == Constants.TYPE_BLOB) {
            return createLink(mapping: 'repository_blob', params: [username: user.username, project: project.name, ref: ref, path: path]).toString()
        } else /*if (type == Constants.TYPE_TREE)*/ {
            return createLink(mapping: 'repository_tree', params: [username: user.username, project: project.name, ref: ref, path: path]).toString()
        }
    }

    private String createUserLink(User user) {
        createLink(mapping: 'user', params: [username: user.username]).toString()
    }

    private String createCommitsLink(String ref, String path, Integer page = null) {
        def params = [username: user.username, project: project.name, ref: ref, path: path]
        if (page) {
            params.page = page
        }
        createLink(mapping: 'repository_commits', params: params).toString()
    }

    private int maxCommitsFetchSize() {
        grailsApplication.config.gitpipe.commits.max.fetch.size
    }


    private String getMimeType(GpRaw raw) {
        // TODO 真面目にやるなら拡張子で色々判断しないとな
        raw.isBinary() ? "application/octet-stream" : "text/plain"
    }

    private String toFileName(String path) {
        new File(path).name
    }

    private boolean isViewerSupport(String name) {
        getViewerType(name) != null ? true : false
    }

    private boolean isNotViewerSupport(String name) {
        return !isViewerSupport(name)
    }

    private String getViewerType(String name) {
        def supportTypes = grailsApplication.config.gitpipe.viewer.support
        def found = supportTypes.find { k, v ->
            name.toLowerCase().endsWith(k)
        }
        found ? found.value : "Plain"
    }

}
