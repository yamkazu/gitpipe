package org.gitpipe.git

import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.patch.FileHeader

class GpDiffFormatter extends DiffFormatter {

    int addLine = 0
    int removeLine = 0
    ByteArrayOutputStream out

    GpDiffFormatter(ByteArrayOutputStream out) {
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
        addLine++
    }

    @Override
    protected void writeRemovedLine(RawText text, int line) {
        super.writeRemovedLine(text, line)
        removeLine++
    }

    void reset() {
        addLine = 0
        removeLine = 0
        out.reset()
    }

    String diff() {
        out.toString()
    }

}