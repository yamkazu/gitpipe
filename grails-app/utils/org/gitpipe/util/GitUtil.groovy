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
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.patch.FileHeader
import org.eclipse.jgit.diff.RawText

class GitUtil {

    private Repository repository;

    static final OBJECT_TYPE_MAP = [(Constants.OBJ_TREE): Constants.TYPE_TREE, (Constants.OBJ_BLOB): Constants.TYPE_BLOB]

    GitUtil(File directory) {
        repository = RepositoryCache.open(RepositoryCache.FileKey.exact(directory, FS.DETECTED), false)
    }

    void create(boolean bare = true) {
        if (!(repository?.directory.exists())) {
            repository.create(bare)
        }
    }

    Map<String, String> getContent(String ref, String path) {
//        InputStream stream = null
        RevWalk revWalk = null
        TreeWalk treeWalk = null

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
                content['mode'] = String.format("%o", treeWalk.getFileMode(0).bits)
                content['size'] = objectLoader.size / 1000
                content['data'] = objectLoader.bytes
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
    
    Map<String, Ref> getBranches() {
        repository.refDatabase.getRefs(Constants.R_HEADS)
    }

    Map<String, Ref> getTags() {
        repository.refDatabase.getRefs(Constants.R_TAGS)
    }

    List<Map<String, String>> findFilesInPath(String ref, String path) {
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
                        type: OBJECT_TYPE_MAP.get(treeWalk.getFileMode(0).objectType),
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
                            type: OBJECT_TYPE_MAP.get(treeWalk.getFileMode(0).objectType),
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
        if (!path) {
            return getLastCommit(ref)
        }

        List result = getRevCommit(ref, path, 0, 1)
        if (!result || result.size() == 0) return null;
        return result.get(0)
    }

    String getDefaultBranch() {
        return repository.branch
    }

    RevCommit getLastCommit(String ref = repository.branch) {
        RevWalk revWalk = null;
        try {
            ObjectId objectId = repository.resolve(ref)
            revWalk = new RevWalk(repository)
            return revWalk.parseCommit(objectId)
        } finally {
            if (revWalk != null) {
                revWalk.release()
            }
        }
    }

    boolean hasCommits() {
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

    List diff(String commit) {
        def list = []
        RevWalk revWalk = new RevWalk(repository);
        try {

            ObjectId objectId = repository.resolve(commit)
            RevCommit revCommit = revWalk.parseCommit(objectId)

            if (revCommit.getParentCount() == 0) {
                TreeWalk treeWalk = new TreeWalk(repository)
                treeWalk.reset()
                treeWalk.setRecursive(true)
                treeWalk.addTree(revCommit.getTree())
                while (treeWalk.next()) {
//                    list.add(new PathChangeModel(tw.getPathString(), tw.getPathString(), 0, tw
//                            .getRawMode(0), commit.getId().getName(), ChangeType.ADD));
                }
                treeWalk.release()
            } else {
                RevCommit parent = revWalk.parseCommit(revCommit.getParent(0).getId())

                DiffFormatter diffFormatter = new GitpipeDiffFormatter(new ByteArrayOutputStream())
                diffFormatter.setRepository(repository)
                diffFormatter.setDiffComparator(RawTextComparator.DEFAULT)
                diffFormatter.setDetectRenames(true)

                List<DiffEntry> diffEntries = diffFormatter.scan(parent.tree, revCommit.tree)

                for (DiffEntry diff : diffEntries) {
                    diffFormatter.format(diff)
                    diff.changeType.name()
                    def entry = [:]
                    entry.newId = diff.newId?.name()
                    entry.oldId = diff.oldId?.name()
                    entry.type = diff.changeType.name()
                    entry.newPath = diff.newPath
                    entry.oldPath = diff.oldPath
                    entry.newMode = String.format("%o", diff.getNewMode().bits)
                    entry.oldMode = String.format("%o", diff.getOldMode().bits)
                    entry.diff = diffFormatter.diff()
                    entry.add = diffFormatter.add
                    entry.remove = diffFormatter.remove
                    list << entry
                    diffFormatter.reset()
                }

                diffFormatter.flush()
            }
        } finally {
            revWalk.release()
        }
        return list;
    }

    static class GitpipeDiffFormatter extends DiffFormatter {

        int add = 0
        int remove = 0
        ByteArrayOutputStream out

        GitpipeDiffFormatter(ByteArrayOutputStream out) {
            super(out)
            this.out = out
        }

        @Override
        void format(FileHeader head, RawText a, RawText b) {
            if (head.getPatchType() == FileHeader.PatchType.UNIFIED)
                format(head.toEditList(), a, b);
        }

        @Override
        protected void writeAddedLine(RawText text, int line) {
            super.writeAddedLine(text, line)
            add++
        }

        @Override
        protected void writeRemovedLine(RawText text, int line) {
            super.writeRemovedLine(text, line)
            remove++
        }

        void reset() {
            add = 0
            remove = 0
            out.reset()
        }

        String diff() {
            out.toString()
        }

    }
}

