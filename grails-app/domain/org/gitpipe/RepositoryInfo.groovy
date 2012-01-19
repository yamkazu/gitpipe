package org.gitpipe

import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryCache
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
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

    def Repository repository;

    static transients = ['repository']

    static final ObjectTypes = [(Constants.OBJ_TREE): Constants.TYPE_TREE, (Constants.OBJ_BLOB): Constants.TYPE_BLOB]

    def repository() {
        if (!repository) {
            repository = RepositoryCache.open(RepositoryCache.FileKey.lenient(repositoryDir(), FS.DETECTED), true)
        }
        repository
    }


    private def resolve(String ref) {
        repository().resolve(ref)
    }

    private def toRevCommit(ObjectId objectId) {
        RevWalk walk = new RevWalk(repository())
        try {
            return walk.parseCommit(objectId)
        } finally {
            walk.dispose()
        }
    }

    def findFilesInPath(String ref, String path) {
        findFilesInPath(toRevCommit(resolve(ref)).tree, path)
    }

    def findFilesInPath(RevTree tree, String path) {
        TreeWalk treeWalk = new TreeWalk(repository())
        treeWalk.addTree(tree)

        if (path) {
            PathFilter pathFilter = PathFilter.create(path)
            treeWalk.setFilter(pathFilter)
        }

        try {
            def files = []
            def foundFolder = false
            if (!path) {
                while (treeWalk.next()) {
//                    files << GitObject.of(treeWalk.getObjectId(0).name, treeWalk.getFileMode(0).objectType, treeWalk.nameString, getLastCommit(repository, treeWalk.pathString, walk.parseCommit(objectId)))
                    files << [id: treeWalk.getObjectId(0).name, type: ObjectTypes.get(treeWalk.getFileMode(0).objectType) , name: treeWalk.nameString]
                }
            } else {
                while (treeWalk.next()) {
                    if (!foundFolder && treeWalk.isSubtree()) {
                        treeWalk.enterSubtree();
                    }
                    if (treeWalk.getPathString().equals(path)) {
                        foundFolder = true;
                        continue;
                    }
                    if (foundFolder) {
//                        files << GitObject.of(treeWalk.getObjectId(0).name, treeWalk.getFileMode(0).objectType, treeWalk.nameString, getLastCommit(repository, treeWalk.pathString, walk.parseCommit(objectId)))
                        files << [id: treeWalk.getObjectId(0).name, type: ObjectTypes.get(treeWalk.getFileMode(0).objectType), name: treeWalk.nameString]
                    }
                }
            }
            println files
            return files
        } finally {
            treeWalk.release()
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
