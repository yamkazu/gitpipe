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
        findFilesInPath(toRevCommit(resolve(ref)), path)
    }

    def findFilesInPath(RevCommit revCommit, String path) {
        TreeWalk treeWalk = new TreeWalk(repository())
        treeWalk.addTree(revCommit.tree)

        if (path) {
            PathFilter pathFilter = PathFilter.create(path)
            treeWalk.setFilter(pathFilter)
        }

        try {
            def files = []
            def foundFolder = false
            if (!path) {
                while (treeWalk.next()) {
                    RevCommit lastCommit = getLastCommit(revCommit, treeWalk.pathString)

                    files << [id: treeWalk.getObjectId(0).name,
                            type: ObjectTypes.get(treeWalk.getFileMode(0).objectType),
                            name: treeWalk.nameString,
                            committer: lastCommit.authorIdent.name,
                            message: lastCommit.shortMessage,
                            date: new Date(lastCommit.commitTime * 1000L)
                    ]
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
                        RevCommit lastCommit = getLastCommit(revCommit, treeWalk.pathString)
                        files << [id: treeWalk.getObjectId(0).name,
                                type: ObjectTypes.get(treeWalk.getFileMode(0).objectType),
                                name: treeWalk.nameString,
                                committer: lastCommit.committerIdent.name,
                                message: lastCommit.shortMessage,
                                date: new Date(lastCommit.commitTime * 1000L)
                        ]
//                        files << [id: treeWalk.getObjectId(0).name, type: ObjectTypes.get(treeWalk.getFileMode(0).objectType), name: treeWalk.nameString]
//                        files << GitObject.of(treeWalk.getObjectId(0).name, treeWalk.getFileMode(0).objectType, treeWalk.nameString, getLastCommit(repository, treeWalk.pathString, walk.parseCommit(objectId)))
//                        files << [id: treeWalk.getObjectId(0).name, type: ObjectTypes.get(treeWalk.getFileMode(0).objectType), name: treeWalk.nameString]
                    }
                }
            }
            return files
        } finally {
            treeWalk.release()
        }
    }

    public RevCommit getLastCommit(RevCommit start, String path) {
        List result = getRevCommit(start, path, 0, 1)
        if (!result || result.size() == 0) return null;
        return result.get(0)
    }

    public boolean hasCommits() {
        // TODO packがあるとカラではない？
//        println 'repo ' + repository()
//        println repository().getDirectory().exists()
//        if (repository() == null && repository().getDirectory().exists()) {
//            return (new File(repository().getDirectory(), "objects").list().length > 2) || (new File(repository().getDirectory(), "objects/pack").list().length > 0)
//        }
//        return false
        true
    }

    public List<RevCommit> getRevCommit(RevCommit revCommit, String path, int offset, int maxCount) {
        def commits = []
        if (maxCount == 0 || !hasCommits()) {
            return commits
        }

        RevWalk rw = null
        try {
            rw = new RevWalk(repository())
            rw.markStart(revCommit)

            if (!StringUtils.isEmpty(path)) {
                TreeFilter filter = AndTreeFilter.create(
                        PathFilterGroup.createFromStrings(Collections.singleton(path)),
                        TreeFilter.ANY_DIFF)
                rw.setTreeFilter(filter)
            }

            Iterable<RevCommit> revlog = rw;
            if (offset > 0) {
                int count = 0
                for (RevCommit rev: revlog) {
                    count++
                    if (count > offset) {
                        commits.add(rev);
                        if (maxCount > 0 && commits.size() == maxCount) {
                            break
                        }
                    }
                }
            } else {
                for (RevCommit rev: revlog) {
                    commits.add(rev);
                    if (maxCount > 0 && commits.size() == maxCount) {
                        break
                    }
                }
            }
            return commits
        } finally {
            if (rw) {
                rw.dispose()
            }
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
