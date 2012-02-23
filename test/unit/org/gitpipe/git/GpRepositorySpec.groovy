package org.gitpipe.git

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.dircache.DirCacheEntry
import org.eclipse.jgit.junit.TestRepository
import org.eclipse.jgit.revwalk.RevBlob
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevObject
import org.eclipse.jgit.revwalk.RevTree
import spock.lang.Specification

class GpRepositorySpec extends Specification {

    GpRepository repository
    TestRepository db

    def setup() {
        repository = new GpRepository(createTmpDirectory("git", null))
        repository.create(false)
        db = new TestRepository(repository.repository)
    }

    def cleanup() {
        FileUtils.cleanDirectory(repository.directory)
    }

    def "get branches size 0"() {
        expect:
        repository.branches.size() == 0
    }

    def "get branches size 1"() {
        setup:
        db.branch("1").commit().create()

        expect:
        repository.branches.size() == 1
        repository.branches.get("1")
    }

    def "get branches size 2"() {
        setup:
        db.branch("1").commit().create()
        db.branch("2").commit().create()

        expect:
        repository.branches.size() == 2
        repository.branches.get("1")
        repository.branches.get("2")
    }

    def "clone repository"() {
        setup:
        def cloneTo = createTmpDirectory("git", null)
        assert !cloneTo.exists()

        when:
        repository.cloneRepository(cloneTo)

        then:
        cloneTo.exists()

        cleanup:
        FileUtils.cleanDirectory(cloneTo)
    }

    def "get branch from empty repository"() {
        expect:
        repository.defaultBranch == 'master'
    }

    def "get branch from has any branches repository"() {
        setup:
        db.branch("master").commit().create()
        db.branch("1").commit().create()
        db.branch("2").commit().create()

        expect:
        repository.defaultBranch == 'master'
    }

    def "get branch from has any branches and not contain master branch repository"() {
        setup:
        db.branch("1").commit().create()
        db.branch("2").commit().create()

        expect:
        repository.defaultBranch == '2'
    }
    
    def "has remote"() {
        setup:
        assert !repository.hasRemote("origin")
        repository.addRemote("origin", createTmpDirectory("remote", null))

        expect:
        repository.hasRemote("origin")
    }

    private File createTmpFile(String prefix, String suffix) {
        File.createTempFile(prefix, suffix)
    }

    private File createTmpDirectory(String prefix, String suffix, boolean exists = false) {
        def file = createTmpFile(prefix, suffix)
        file.delete()
        if (exists) file.mkdir()
        file
    }

    protected RevBlob blob(String content) throws Exception {
        db.blob(content)
    }

    protected DirCacheEntry file(String path, RevBlob blob) throws Exception {
        db.file(path, blob)
    }

    protected RevTree tree(DirCacheEntry... entries) throws Exception {
        db.tree(entries)
    }

    protected RevObject get(RevTree tree, String path) throws Exception {
        db.get(tree, path)
    }

    protected RevCommit commit(RevCommit... parents) throws Exception {
        db.commit(parents)
    }

    protected RevCommit commit(RevTree tree, RevCommit... parents) throws Exception {
        db.commit(tree, parents)
    }

    protected RevCommit commit(int secDelta, RevCommit... parents) throws Exception {
        db.commit(secDelta, parents)
    }

    protected RevCommit commit(int secDelta, RevTree tree, RevCommit... parents) throws Exception {
        db.commit(secDelta, tree, parents)
    }

}