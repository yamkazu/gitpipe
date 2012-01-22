package org.gitpipe

import org.apache.commons.lang.StringUtils
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryCache
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.AndTreeFilter
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.treewalk.filter.PathFilterGroup
import org.eclipse.jgit.treewalk.filter.TreeFilter
import org.eclipse.jgit.util.FS
import org.gitpipe.util.GitUtil

class RepositoryInfo {

    def grailsApplication

    String projectName
    String description

    static belongsTo = [user: User]

    static constraints = {
        projectName(blank: false, size: 3..20, matches: "[a-zA-Z0-9\\-_]+", unique: ['user', 'projectName'])
    }

    static final ObjectTypes = [(Constants.OBJ_TREE): Constants.TYPE_TREE, (Constants.OBJ_BLOB): Constants.TYPE_BLOB]

    def repository() {
        new GitUtil(directory())
    }

    private def directory() {
        new File(userRepositoryDir(), repositoryName())
    }

    private def repositoryName() {
        projectName + Constants.DOT_GIT;
    }

    private def baseRepositoryDir() {
        new File(grailsApplication.config.gitpipe.repositories.dir)
    }

    private def userRepositoryDir() {
        new File(baseRepositoryDir(), user.username)
    }

}
