package org.gitpipe.git

import org.eclipse.jgit.diff.DiffEntry

class GpDiffEntry {
    
    String newId
    String oldId
    String type
    String newPath
    String oldPath
    String newMode
    String oldMode
    String diff
    int addLine
    int removeLine

    GpDiffEntry(GpDiffFormatter formatter, DiffEntry entry) {
        this.newId = entry.newId?.name()
        this.oldId = entry.oldId?.name()
        this.type = entry.changeType.name()
        this.newPath = entry.newPath
        this.oldPath = entry.oldPath
        this.newMode = String.format("%o", entry.getNewMode().bits)
        this.oldMode = String.format("%o", entry.getOldMode().bits)
        this.diff = formatter.diff()
        this.addLine = formatter.addLine
        this.removeLine = formatter.removeLine
    }
    
}
