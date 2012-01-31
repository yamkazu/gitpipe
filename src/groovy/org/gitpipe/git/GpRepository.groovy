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
import org.gitpipe.util.TimeUtils
import org.eclipse.jgit.lib.*

class GpRepository {

    private Repository repository;

    static final OBJECT_TYPE_MAP = [(Constants.OBJ_TREE): Constants.TYPE_TREE, (Constants.OBJ_BLOB): Constants.TYPE_BLOB]

    GpRepository(File directory) {
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
            release(treeWalk)
            release(revWalk)
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
                    files << toMap(treeWalk)
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
                        files << toMap(treeWalk)
                    }
                }
            }
        } finally {
            release(treeWalk)
            release(revWalk)
        }
        files
    }

    Map<String, Object> getLastCommit(String ref, String path) {
        if (!path) {
            return getLastCommit(ref)
        }

        List result = getRevCommit(ref, path, 0, 1)
        if (!result) {
            return [:]
        }

        return result.get(0)
    }

    String getDefaultBranch() {
        return repository.branch
    }

    Map<String, Object> getLastCommit(String ref = repository.branch) {
        RevWalk revWalk = null;
        try {
            ObjectId objectId = repository.resolve(ref)
            revWalk = new RevWalk(repository)
            return toMap(revWalk.parseCommit(objectId))
        } finally {
            release(revWalk)
        }
    }

    List<Map<String, Object>> getRevCommit(String ref, String path, int offset, int maxCount) {
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
                for (RevCommit rev: revWalk) {
                    count++
                    if (count > offset) {
                        commits << toMap(rev)
                        if (maxCount > 0 && commits.size() == maxCount) {
                            break
                        }
                    }
                }
            } else {
                for (RevCommit rev: revWalk) {
                    commits << toMap(rev)
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

    private void release(BlameGenerator generator) {
        if (generator) {
            generator.release()
        }
    }

    Map<String, Object> diff(String commit) {
        def diff = [:]
        RevWalk revWalk = null
        try {
            revWalk = new RevWalk(repository);
            ObjectId objectId = repository.resolve(commit)
            RevCommit newCommit = revWalk.parseCommit(objectId)

            diff.newId = newCommit.id.name()
            def reader = repository.newObjectReader()
            CanonicalTreeParser newTreeParser = new CanonicalTreeParser()
            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser()
            newTreeParser.reset(reader, newCommit.tree);
            if (newCommit.getParentCount() > 0) {
                diff.oldId = newCommit.getParent(0).getId().name()
                RevCommit parentCommit = revWalk.parseCommit(newCommit.getParent(0).getId())
                oldTreeParser.reset(reader, parentCommit.tree);
            } else {
                diff.oldId = ObjectId.zeroId().name()
            }

            GpDiffFormatter diffFormatter = new GpDiffFormatter(new ByteArrayOutputStream())
            diffFormatter.setRepository(repository)
            diffFormatter.setDiffComparator(RawTextComparator.DEFAULT)
            diffFormatter.setDetectRenames(true)

            List<DiffEntry> diffEntries = diffFormatter.scan(oldTreeParser, newTreeParser)

            def diffs = []
            for (DiffEntry entry: diffEntries) {
                diffFormatter.format(entry)
                diffs << toMap(diffFormatter, entry)
                diffFormatter.reset()
            }
            diff.diffs = diffs
            diffFormatter.flush()
        } finally {
            release(revWalk)
        }
        return diff;
    }

    private boolean hasCommits() {
        if (repository != null && repository.getDirectory().exists()) {
            return (new File(repository.getDirectory(), "objects").list().length > 2) || (new File(repository.getDirectory(), "objects/pack").list().length > 0)
        }
        return false
    }

    private Map<String, Object> toMap(RevCommit revCommit) {
        def map = [:]
        if (!revCommit) {
            return map
        }

        map.id = revCommit.id.name
        map.author = revCommit.authorIdent.name
        map.email = revCommit.authorIdent.emailAddress
        map.date = TimeUtils.timeAgo(new Date(revCommit.commitTime * 1000L))
        map.message = revCommit.shortMessage

        return map
    }

    private Map<String, Object> toMap(TreeWalk treeWalk) {
        [id: treeWalk.getObjectId(0).name, type: OBJECT_TYPE_MAP.get(treeWalk.getFileMode(0).objectType), path: treeWalk.pathString, name: treeWalk.nameString]
    }

    private Map<String, Object> toMap(GpDiffFormatter diffFormatter, DiffEntry diffEntry) {
        def entry = [:]
        entry.newId = diffEntry.newId?.name()
        entry.oldId = diffEntry.oldId?.name()
        entry.type = diffEntry.changeType.name()
        entry.newPath = diffEntry.newPath
        entry.oldPath = diffEntry.oldPath
        entry.newMode = String.format("%o", diffEntry.getNewMode().bits)
        entry.oldMode = String.format("%o", diffEntry.getOldMode().bits)
        entry.diff = diffFormatter.diff()
        entry.add = diffFormatter.add
        entry.remove = diffFormatter.remove
        entry
    }

    private release(TreeWalk treeWalk) {
        if (treeWalk) {
            treeWalk.release()
        }
    }

    private release(RevWalk revWalk) {
        if (revWalk) {
            revWalk.release()
        }
    }

}

