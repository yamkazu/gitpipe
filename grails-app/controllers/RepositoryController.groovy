import grails.plugins.springsecurity.Secured
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevCommit
import org.gitpipe.RepositoryInfo
import org.gitpipe.User
import org.gitpipe.util.TimeUtils

/**
 * Created by IntelliJ IDEA.
 * User: yamkazu
 * Date: 12/01/17
 * Time: 2:47
 * To change this template use File | Settings | File Templates.
 */
class RepositoryController {

    private static final Log LOG = LogFactory.getLog(RepositoryController.class)

    def springSecurityService
    def grailsApplication

    @Secured(['ROLE_USER'])
    def form() {
    }

    @Secured(['ROLE_USER'])
    def create() {
        def user = User.findByUsername springSecurityService.principal.username

        if (!user) {
            response.sendError(404)
            return
        }

        def repositoryInfo = new RepositoryInfo(params)
        repositoryInfo.user = user
        if (!repositoryInfo.save()) {
            render view: 'form', model: [repositoryInfo: repositoryInfo]
            return
        }

        // TODO
        // change before insert process
        repositoryInfo.repository().create()

        redirect(uri: "/${user.username}/${repositoryInfo.projectName}")
    }

    def show() {
        def user = User.findByUsername params.username
        if (!user) {
            response.sendError(404)
            return
        }

        def repositoryInfo = user.repositories.find { RepositoryInfo repositoryInfo ->
            params.project == repositoryInfo.projectName
        }
        if (!repositoryInfo) {
            response.sendError(404)
            return
        }

        def repository = repositoryInfo.repository()

        render view: 'tree', model: [user: user, repository: repositoryInfo, 'ref': repository.defaultBranch, path: '', commit: repository.getLastCommit()]
    }

    def commits() {
        def user = User.findByUsername params.username
        if (!user) {
            response.sendError(404)
            return
        }

        def repositoryInfo = user.repositories.find { RepositoryInfo repositoryInfo ->
            params.project == repositoryInfo.projectName
        }
        if (!repositoryInfo) {
            response.sendError(404)
            return
        }

        def repository = repositoryInfo.repository()

        withFormat {
            html {
                render view: 'commits', model: [user: user, repository: repositoryInfo, 'ref': params.ref, path: params.path]
            }
            json {
                render(contentType: "text/json") {
                    commits = array {
                        for (c in repository.getRevCommit(params.ref, params.path, Integer.decode(params.offset), 20)) {
                            commit id: c.id.name, shortMessage: c.shortMessage, author: c.authorIdent.name, date: new Date(c.commitTime * 1000L)
                        }
                    }
                }
            }
        }
    }

    def tree() {
        def user = User.findByUsername params.username
        if (!user) {
            response.sendError(404)
            return
        }

        def repositoryInfo = user.repositories.find { RepositoryInfo repositoryInfo ->
            params.project == repositoryInfo.projectName
        }
        if (!repositoryInfo) {
            response.sendError(404)
            return
        }

        def repository = repositoryInfo.repository()

        withFormat {
            html {
                render view: 'tree', model: [user: user, repository: repositoryInfo, 'ref': params.ref, path: params.path, commit: repository.getLastCommit(params.ref)]
            }
            json {
                render(contentType: "text/json") {
                    current = params.path;
                    if (params.path) {
                        parent = new File(params.path).parent ?: ""
                    }
                    files = repository.findFilesInPath(params.ref, params.path).collect {
                        RevCommit commit = repository.getLastCommit(params.ref, it.path)

                        def commitInfo = [message: commit.shortMessage, date: TimeUtils.timeAgo(new Date(commit.commitTime * 1000L))]

                        if (!commit.authorIdent.emailAddress) {
                            return it + commitInfo
                        }

                        def author = User.findByEmail(commit.authorIdent.emailAddress)
                        if (!author) {
                            return it + commitInfo
                        }
                        commitInfo['author'] = author.username
                        it + commitInfo
                    }.collect {
                        def type = it.type
                        def url = null
                        if (type == Constants.TYPE_BLOB) {
                            url = createLink(mapping: 'repository_blob', params: [username: params.username, project: params.project, ref: params.ref, path: it.path]).toString()
                        } else /*if (type == Constants.TYPE_TREE)*/ {
                            url = createLink(mapping: 'repository_tree', params: [username: params.username, project: params.project, ref: params.ref, path: it.path]).toString()
                        }
                        it + [url: url]
                    }
                }
            }
        }
    }

    def blob() {
        def user = User.findByUsername(params.username)
        if (!user) {
            LOG.error("cannot found user: ${params.username}")
            response.sendError(404)
            return
        }

        def repositoryInfo = user.repositories.find { RepositoryInfo repositoryInfo ->
            params.project == repositoryInfo.projectName
        }
        if (!repositoryInfo) {
            LOG.error("cannot found projecet: ${params.project}")
            response.sendError(404)
            return
        }

        def repository = repositoryInfo.repository()

        withFormat {
            html {
                render view: 'blob', model: [user: user, repository: repositoryInfo, ref: params.ref, path: params.path, commit: repository.getLastCommit(params.ref)]
            }
            json {

                // FIXME BIG DATA 対応
                // 取得最大値を設ける
                def content = repository.getContent(params.ref, params.path)

                if (RawText.isBinary(content.data)) {
                    render(contentType: "text/json") {
                        path = params.path
                        mode = content.mode
                        size = content.size
                        file_type = 'binary'
                    }
                    return
                }

                render(contentType: "text/json") {
                    path = params.path
                    mode = content.mode
                    size = content.size
                    file_type = getViewerType(toFileName(path))
                    data = new String(content.data, Constants.CHARSET)
                }
            }
        }
    }

    def raw() {
        def user = User.findByUsername(params.username)
        if (!user) {
            LOG.error("cannot found user: ${params.username}")
            response.sendError(404)
            return
        }

        def repositoryInfo = user.repositories.find { RepositoryInfo repositoryInfo ->
            params.project == repositoryInfo.projectName
        }
        if (!repositoryInfo) {
            LOG.error("cannot found projecet: ${params.project}")
            response.sendError(404)
            return
        }

        def content = repositoryInfo.repository().getContent(params.ref, params.path)

        if (!content.data) {
            // handle not found
        }

        response.setContentType(getMimeType(content.data as byte[]))
        response.setHeader("Content-disposition", "filename=${toFileName(params.path)}")
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
