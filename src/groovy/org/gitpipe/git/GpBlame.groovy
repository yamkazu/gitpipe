package org.gitpipe.git

class GpBlame {

    GpRaw raw

    List<GpBlameEntry> entries = []

    GpBlame(GpRaw raw) {
        this.raw = raw
    }

    void leftShift(GpBlameEntry entry) {
        entries << entry
    }

    void sort() {
        entries.sort {[it.start]}
    }

}
