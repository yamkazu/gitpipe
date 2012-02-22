package org.gitpipe.git

import java.nio.charset.Charset
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectLoader
import org.eclipse.jgit.treewalk.TreeWalk

class GpRaw {

    BigDecimal size
    String mode
    byte[] file // TODO Big File 対応

    GpRaw(TreeWalk walk, ObjectLoader loader) {
        mode = String.format("%o", walk.getFileMode(0).bits)
        size = loader.size / 1024
        file = loader.bytes
    }

    Boolean isBinary() {
        RawText.isBinary(file)
    }

    String getFileAsString() {
        getFileAsString(Constants.CHARSET)
    }

    String getFileAsString(Charset charset) {
        new String(file, charset)
    }


}
