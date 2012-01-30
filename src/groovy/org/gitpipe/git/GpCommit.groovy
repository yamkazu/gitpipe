package org.gitpipe.git

import org.eclipse.jgit.revwalk.RevCommit

class GpCommit {
    
    String id
    String message
    GpAuthor author
    List<GpCommit> parents = []
    Date date

    GpCommit(RevCommit revCommit, Boolean recursive = true) {
        this.id = revCommit.id.name
        this.message = revCommit.shortMessage
        this.author = new GpAuthor(revCommit.authorIdent)
        this.date = new Date(revCommit.commitTime * 1000L)

        if (recursive) {
            revCommit.parents.each { RevCommit rc ->
                parents << new GpCommit(rc, false)
            }
        }
    }

    Boolean hasParents() {
        parents > 1
    }

}
