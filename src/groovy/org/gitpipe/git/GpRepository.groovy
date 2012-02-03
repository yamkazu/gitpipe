package org.gitpipe.git

import org.apache.commons.lang.StringUtils
import org.eclipse.jgit.blame.BlameGenerator
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.AndTreeFilter
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.treewalk.filter.PathFilterGroup
import org.eclipse.jgit.treewalk.filter.TreeFilter
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.lib.*

class GpRepository {

    private Repository repository;

    GpRepository(File directory) {
        repository = RepositoryCache.open(RepositoryCache.FileKey.exact(directory, FS.DETECTED), false)
    }

    void create(boolean bare = true) {
        if (!(repository?.directory.exists())) {
            repository.create(bare)
        }
    }

    Map<String, Ref> getBranches() {
        repository.refDatabase.getRefs(Constants.R_HEADS)
    }

    String getDefaultBranch() {
        return repository.branch
    }

    Map<String, Ref> getTags() {
        repository.refDatabase.getRefs(Constants.R_TAGS)
    }

    List<GpObject> findFilesInPath(String ref, String path) {
        ObjectId objectId = repository.resolve(ref)
        TreeWalk treeWalk = null
        RevWalk revWalk = null
        def files = []
        try {
            treeWalk = new TreeWalk(repository)
            revWalk = new RevWalk(repository)
            RevCommit revCommit = revWalk.parseCommit(objectId)
            treeWalk.addTree(revCommit.tree)

            if (path) {
                PathFilter pathFilter = PathFilter.create(path)
                treeWalk.setFilter(pathFilter)
            }

            def foundFolder = false
            if (!path) {
                while (treeWalk.next()) {
                    files << new GpObject(treeWalk)
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
                        files << new GpObject(treeWalk)
                    }
                }
            }
        } finally {
            release(treeWalk)
            release(revWalk)
        }
        files
    }

    List<GpCommit> log(String ref, String path, int offset, int maxCount) {
        def commits = []
        if (maxCount == 0 || !hasCommits()) {
            return commits
        }

        ObjectId objectId = repository.resolve(ref)
        RevWalk revWalk = null
        try {
            revWalk = new RevWalk(repository)
            revWalk.markStart(revWalk.parseCommit(objectId))

            if (!StringUtils.isEmpty(path)) {
                TreeFilter filter = AndTreeFilter.create(
                        PathFilterGroup.createFromStrings(Collections.singleton(path)),
                        TreeFilter.ANY_DIFF)
                revWalk.setTreeFilter(filter)
            }

            if (offset > 0) {
                int count = 0
                for (RevCommit commit: revWalk) {
                    count++
                    if (count > offset) {
                        commits << new GpCommit(commit, false)
                        if (maxCount > 0 && commits.size() == maxCount) {
                            break
                        }
                    }
                }
            } else {
                for (RevCommit commit: revWalk) {
                    commits << new GpCommit(commit, false)
                    if (maxCount > 0 && commits.size() == maxCount) {
                        break
                    }
                }
            }
        } finally {
            release(revWalk)
        }
        commits
    }

    GpCommit logLimit1(String ref = repository.branch) {
        if (!hasCommits()) return

        RevWalk revWalk = null;
        try {
            ObjectId objectId = repository.resolve(ref)
            revWalk = new RevWalk(repository)
            return new GpCommit(revWalk.parseCommit(objectId), false)
        } finally {
            release(revWalk)
        }
    }

    GpCommit logLimit1(String ref, String path) {
        if (!path) {
            return logLimit1(ref)
        }

        List result = log(ref, path, 0, 1)
        if (!result) {
            return null
        }

        return result.get(0)
    }

    GpRaw raw(String ref, String path) {
        RevWalk revWalk = null
        TreeWalk treeWalk = null
        GpRaw raw = null

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
                ObjectLoader objectLoader = repository.open(treeWalk.getObjectId(0), Constants.OBJ_BLOB)
                raw = new GpRaw(treeWalk, objectLoader)
            }
        } finally {
            release(treeWalk)
            release(revWalk)
        }
        raw
    }

    GpDiff diff(String commit) {
        GpDiff diff = new GpDiff()
        RevWalk revWalk = null
        try {
            revWalk = new RevWalk(repository);
            ObjectId objectId = repository.resolve(commit)
            RevCommit newCommit = revWalk.parseCommit(objectId)

            diff.newCommit = new GpCommit(newCommit, false)

            def reader = repository.newObjectReader()

            CanonicalTreeParser newTreeParser = new CanonicalTreeParser()
            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser()
            newTreeParser.reset(reader, newCommit.tree);

            if (newCommit.getParentCount() > 0) {
                RevCommit parentCommit = revWalk.parseCommit(newCommit.getParent(0).getId())
                oldTreeParser.reset(reader, parentCommit.tree);
                diff.oldCommit = new GpCommit(parentCommit, false)

            }

            GpDiffFormatter formatter = new GpDiffFormatter(new ByteArrayOutputStream())
            formatter.setRepository(repository)
            formatter.setDiffComparator(RawTextComparator.DEFAULT)
            formatter.setDetectRenames(true)

            List<DiffEntry> diffEntries = formatter.scan(oldTreeParser, newTreeParser)

            for (DiffEntry entry: diffEntries) {
                formatter.format(entry)
                diff << new GpDiffEntry(formatter, entry)
                formatter.reset()
            }
            formatter.flush()
        } finally {
            release(revWalk)
        }
        return diff;
    }

    GpBlame blame(String ref, String path) {
        BlameGenerator generator = null
        GpBlame blame = null
        try {
            blame = new GpBlame(raw(ref, path))
            generator = new BlameGenerator(repository, path)
            generator.push("", repository.resolve(ref))
            blame.size = generator.resultContents.size()
            while (generator.next()) {
                blame << new GpBlameEntry(generator)
            }
            blame.sort()
        } finally {
            release(generator)
        }
        blame
    }

    boolean hasCommits() {
        if (repository != null && repository.getDirectory().exists()) {
            return (new File(repository.getDirectory(), "objects").list().length > 2) || (new File(repository.getDirectory(), "objects/pack").list().length > 0)
        }
        return false
    }

    private void release(BlameGenerator generator) {
        if (generator) { generator.release() }
    }

    private void release(TreeWalk treeWalk) {
        if (treeWalk) { treeWalk.release() }
    }

    private void release(RevWalk revWalk) {
        if (revWalk) { revWalk.release() }
    }

}

