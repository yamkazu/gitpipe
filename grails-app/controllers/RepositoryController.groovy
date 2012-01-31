import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.lib.Constants
import org.gitpipe.User
import org.gitpipe.git.GpBlame
import org.gitpipe.git.GpRepository
import org.gitpipe.util.TimeUtils

class RepositoryController extends AbstractController {

    def grailsApplication
    GpRepository repository

    def beforeInterceptor = {
        bindUser()
        bindProject()
        repository = project.repository
    }

    def commits(String ref, String path, int page) {
        withFormat {
            html {
                [user: user, project: project, 'ref': ref, path: path]
            }
            json {
                render(contentType: "text/json") {
                    def cs = repository.getRevCommit(ref, path, page * maxCommitsFetchSize(), maxCommitsFetchSize())
                    commits = cs.collect { commit ->
                        commit = commit + findUserByEmail(commit.email)
                        commit.remove("email") // does not include email address in json response
                        commit.url = createCommitLink(commit.id)
                        commit
                    }
                    if (!(cs.size() < maxCommitsFetchSize())) {
                        next = createCommitsLink(ref, path, ++page)
                    }
                }
            }
        }
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

    def commit(String id) {
        withFormat {
            html {
                def commit = repository.getLastCommit(id)
                [user: user, project: project, id: id, commit: commit + findUserByEmail(commit.email)]
            }
            json {
                render(contentType: "text/json") {
                    def diff = repository.diff(id)
                    diffs = diff.diffs.collect { d ->
                        d.newBlobUrl = createBlobLink(diff.newId, d.newPath)
                        d.oldBlobUrl = createBlobLink(diff.oldId, d.oldPath)
                        d
                    }
                }
            }
        }
    }

    private String createBlobLink(String ref, String path) {
        createLink(mapping: 'repository_blob', params: [username: user.username, project: project.name, ref: ref, path: path])
    }

    private String createRawLink(String ref, String path) {
        createLink(mapping: 'repository_raw', params: [username: user.username, project: project.name, ref: ref, path: path])
    }

    def tree(String ref, String path) {
        withFormat {
            html {
                def commit = repository.getLastCommit(ref)
                [user: user, project: project, ref: ref, path: path, commit: commit + findUserByEmail(commit.email)]
            }
            json {
                render(contentType: "text/json") {
                    current = createCurrentTreeLink(ref, path)
                    historyUrl = createCommitsLink(ref, path)
                    if (path) {
                        parents = createParentsTreeLink(ref, path)
                    }
                    files = repository.findFilesInPath(ref, path).collect { file ->
                        file.commit = repository.getLastCommit(params.ref, file.path)
                        file.commit = file.commit + findUserByEmail(file.commit.email)
                        file.commit.remove("email") // does not include email address in json response
                        file.commit.url = createCommitLink(file.commit.id)
                        file.url = createTreeLink(file.type, ref, file.path)
                        file
                    }
                }
            }
        }
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

    private User getUserByEmail(String email) {

    }

    private Map<String, String> findUserByEmail(String email) {
        def author = User.findByEmail(email)
        if (!author) {
            return [:]
        }
        [username: author.username, userurl: createLink(mapping: 'user', params: [username: author.username])]
    }

    private String createUserLink(User user) {
        createLink(mapping: 'user', params: [username: user.username]).toString()
    }

    def blame(String ref, String path) {
        withFormat {
            html {
                [user: user, project: project, ref: ref, path: path, commit: repository.getLastCommit(ref)]
            }
            json {
                render(contentType: "text/json") {
                    GpBlame blame = repository.blame(ref, path)

                    current = createCurrentBlobLink(ref, path)
                    parents = createParentsTreeLink(ref, path)
                    historyUrl = createCommitsLink(ref, path)
                    rawUrl = createRawLink(ref, path)

                    raw {
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
                            e {
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

    def blob(String ref, String path) {
        withFormat {
            html {
                [user: user, project: project, ref: ref, path: path, commit: repository.getLastCommit(ref)]
            }
            json {
                // FIXME BIG DATA 対応
                // 取得最大値を設ける
                def content = repository.getContent(ref, path)

                render(contentType: "text/json") {
                    current = createCurrentBlobLink(ref, path)
                    parents = createParentsTreeLink(ref, path)
                    historyUrl = createCommitsLink(ref, path)
                    rawUrl = createRawLink(ref, path)
                    mode = content.mode
                    size = content.size

                    if (RawText.isBinary(content.data)) {
                        file_type = 'binary'
                    } else {
                        file_type = getViewerType(toFileName(path))
                        data = new String(content.data, Constants.CHARSET)
                    }
                }
            }
        }
    }

    def raw(String ref, String path) {
        def content = repository.getContent(ref, path)

        if (!content.data) {
            // handle not found
        }

        response.setContentType(getMimeType(content.data as byte[]))
        response.setHeader("Content-disposition", "filename=${toFileName(path)}")
        response.outputStream << content.data
    }

    private String getMimeType(byte[] data) {
        // TODO 真面目にやるなら拡張子で色々判断しないとな
        RawText.isBinary(data) ? "application/octet-stream" : "text/plain"
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
