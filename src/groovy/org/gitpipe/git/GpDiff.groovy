package org.gitpipe.git

import org.eclipse.jgit.diff.DiffEntry

class GpDiff {

    GpCommit oldCommit
    GpCommit newCommit

    List<GpDiffEntry> entries = []

    void leftShift(GpDiffEntry diff) {
        entries << diff
    }

}
