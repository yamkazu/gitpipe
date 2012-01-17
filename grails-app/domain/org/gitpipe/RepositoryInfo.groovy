package org.gitpipe

import org.eclipse.jgit.lib.RepositoryCache
import org.eclipse.jgit.util.FS

class RepositoryInfo {

    def grailsApplication

    String projectName
    String description

    static belongsTo = [user: User]

    static constraints = {
        projectName(blank: false, size: 3..20, matches: "[a-zA-Z0-9\\-_]+", unique: ['user', 'projectName'])
    }

    def createRepository() {
        def repositoryDir = repositoryDir()
        def dir = RepositoryCache.FileKey.resolve(repositoryDir, FS.DETECTED)
        if (dir == null) {
            def location = RepositoryCache.FileKey.exact(repositoryDir, FS.DETECTED)
            RepositoryCache.open(location, false).create(true)
        }
    }

    private def repositoryDir() {
        new File(userRepositoryDir(), repositoryName())
    }

    private def repositoryName() {
        projectName + ".git"
    }

    private def baseRepositoryDir() {
        new File(grailsApplication.config.gitpipe.repositories.dir)
    }

    private def userRepositoryDir() {
        new File(baseRepositoryDir(), user.username)
    }

}
