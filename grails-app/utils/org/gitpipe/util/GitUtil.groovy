package org.gitpipe.util

import org.apache.commons.lang.StringUtils
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.AndTreeFilter
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.treewalk.filter.PathFilterGroup
import org.eclipse.jgit.treewalk.filter.TreeFilter
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.lib.*

class GitUtil {

    Repository repository;

    static final ObjectTypes = [(Constants.OBJ_TREE): Constants.TYPE_TREE, (Constants.OBJ_BLOB): Constants.TYPE_BLOB]
    
    static final SUPPORT_CONTENTS = [
            '.as': 'AS3',
            '.sh': 'Bash',
            '.cfm': 'ColdFusion',
            '.cs': 'CSharp',
            '.c': 'Cpp',
            '.cpp': 'Cpp',
            '.css': 'Css',
            '.pas': 'Delphi',
            '.diff': 'Diff',
            '.patch': 'Diff',
            '.erl': 'Erlang',
            '.groovy': 'Groovy',
            '.js': 'JScript',
            '.java': 'Java',
            '.fx': 'JavaFX',
            '.pl': 'Perl',
            '.php': 'Php',
            '.text': 'Plain',
            '.txt': 'Plain',
            '.md': 'Plain',
            'readme': 'Plain',
            '.ps1': 'PowerShell',
            '.py': 'Python',
            '.ruby': 'Ruby',
            '.scala': 'Scala',
            '.sql': 'Sql',
            '.vb': 'Vb',
            '.xml': 'Xml',
            '.xslt': 'Xml',
            '.html': 'Xml',
            '.xhtml': 'Xml'
    ]

    def GitUtil(File directory) {
        repository = RepositoryCache.open(RepositoryCache.FileKey.exact(directory, FS.DETECTED), false)
    }

    def create(boolean bare = true) {
        if (!(repository?.directory.exists())) {
            repository.create(bare)
        }
    }

    static def isSupportContentType(String path) {
        getContentType(path) != null
    }

    static def isNotSupportContentType(String path) {
        !isSupportContentType(path)
    }

    static def getContentType(String path) {
        def name = new File(path).name
        def type = SUPPORT_CONTENTS.find { k, v ->
            name.toLowerCase().endsWith(k)
        }
        type.value
    }

    def getContent(String ref, String path) {
//        InputStream stream = null
        RevWalk revWalk = null
        TreeWalk treeWalk = null

        if (isNotSupportContentType(path)) {
            throw new UnsupportedContentType()
        }
        
        def content = [:]
        try {
            ObjectId objectId = repository.resolve(ref)

            revWalk = new RevWalk(repository)
            RevCommit revCommit = revWalk.parseCommit(objectId)

            treeWalk = new TreeWalk(repository)
            treeWalk.addTree(revCommit.tree)
            treeWalk.setFilter(PathFilterGroup.createFromStrings(Collections.singleton(path)))

            while (treeWalk.next()) {
                if (treeWalk.isSubtree() && !path.equals(treeWalk.getPathString())) {
                    treeWalk.enterSubtree();
                    continue;
                }
                ObjectId blobId = treeWalk.getObjectId(0)
                ObjectLoader objectLoader = repository.open(blobId, Constants.OBJ_BLOB)
                content['mode'] = treeWalk.getFileMode(0).bits
                content['size'] = objectLoader.size
                content['data'] = objectLoader.bytes
                content['file_type'] = getContentType(path)
//                FIXME big file の扱い
//                stream = objectLoader.openStream()
//                IOUtils.toByteArray(stream)
            }
        } finally {
//            IOUtils.closeQuietly(stream)
            if (treeWalk) treeWalk.release()
            if (revWalk) revWalk.release()
        }
        content
    }

    def findFilesInPath(String ref, String path) {
        ObjectId objectId = repository.resolve(ref)

        TreeWalk treeWalk = new TreeWalk(repository)
        RevWalk revWalk = new RevWalk(repository)
        RevCommit revCommit = revWalk.parseCommit(objectId)
        treeWalk.addTree(revCommit.tree)

        if (path) {
            PathFilter pathFilter = PathFilter.create(path)
            treeWalk.setFilter(pathFilter)
        }

        def files = []
        def foundFolder = false
        if (!path) {
            while (treeWalk.next()) {
                files << [id: treeWalk.getObjectId(0).name,
                        type: ObjectTypes.get(treeWalk.getFileMode(0).objectType),
                        path: treeWalk.pathString,
                        name: treeWalk.nameString
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
                    files << [id: treeWalk.getObjectId(0).name,
                            type: ObjectTypes.get(treeWalk.getFileMode(0).objectType),
                            path: treeWalk.pathString,
                            name: treeWalk.nameString
                    ]
                }
            }
        }
        treeWalk.release()
        revWalk.release()
        files
    }



    RevCommit getLastCommit(String ref, String path) {
        List result = getRevCommit(ref, path, 0, 1)
        if (!result || result.size() == 0) return null;
        return result.get(0)
    }

    boolean hasCommits() {
        // TODO packがあるとカラではない？
        if (repository != null && repository.getDirectory().exists()) {
            return (new File(repository.getDirectory(), "objects").list().length > 2) || (new File(repository.getDirectory(), "objects/pack").list().length > 0)
        }
        return false
    }

    List<RevCommit> getRevCommit(String ref, String path, int offset, int maxCount) {
        def commits = []

        if (maxCount == 0 || !hasCommits()) {
            return commits
        }

        ObjectId objectId = repository.resolve(ref)
        RevWalk revWalk = new RevWalk(repository)
        revWalk.markStart(revWalk.parseCommit(objectId))

        if (!StringUtils.isEmpty(path)) {
            TreeFilter filter = AndTreeFilter.create(
                    PathFilterGroup.createFromStrings(Collections.singleton(path)),
                    TreeFilter.ANY_DIFF)
            revWalk.setTreeFilter(filter)
        }

        Iterable<RevCommit> revlog = revWalk;
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
        revWalk.release()
        commits
    }

    static class UnsupportedContentType extends RuntimeException {}

}
