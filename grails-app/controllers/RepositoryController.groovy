import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.lib.Constants
import org.gitpipe.User
import org.gitpipe.util.RepositoryUtil

class RepositoryController extends AbstractController {

    def grailsApplication
    RepositoryUtil repository

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

    private String createCommitsLink(String ref, String path, int page) {
        createLink(mapping: 'repository_commits', params: [username: user.username, project: project.name, ref: ref, path: path, page: page])
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
                    diffs = repository.diff(id).collect { diff ->
                        diff.newBlobUrl = createBlobLink(id, diff.newPath)
                        diff.oldBlobUrl = createBlobLink(id, diff.oldPath)
                        diff
                    }
                }
            }
        }
    }

    private String createBlobLink(String ref, String path) {
        createLink(mapping: 'repository_blob', params: [username: user.username, project: project.name, ref: ref, path: path])
    }

    def tree(String ref, String path) {
        withFormat {
            html {
                def commit = repository.getLastCommit(ref)
                [user: user, project: project, ref: ref, path: path, commit: commit + findUserByEmail(commit.email)]
            }
            json {
                render(contentType: "text/json") {
                    current = path;
                    if (path) {
                        parent = new File(path).parent ?: ""
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

    private Map<String, String> findUserByEmail(String email) {
        def author = User.findByEmail(email)
        if (!author) {
            return [:]
        }
        [username: author.username, userurl: createLink(mapping: 'user', params: [username: author.username])]
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

                if (RawText.isBinary(content.data)) {
                    render(contentType: "text/json") {
                        blobPath = path
                        mode = content.mode
                        size = content.size
                        file_type = 'binary'
                    }
                    return
                }

                render(contentType: "text/json") {
                    blobPath = path
                    mode = content.mode
                    size = content.size
                    file_type = getViewerType(toFileName(path))
                    data = new String(content.data, Constants.CHARSET)
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
