package org.gitpipe.git

import org.eclipse.jgit.blame.BlameGenerator

class GpBlameEntry {

    int start
    int end
    int length
    GpCommit commit

    GpBlameEntry(BlameGenerator generator) {
        this.start = generator.resultStart
        this.end = generator.resultEnd
        this.length = generator.regionLength
        this.commit = new GpCommit(generator.sourceCommit)
    }

}
