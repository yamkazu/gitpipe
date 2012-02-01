package org.gitpipe.git

import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.lib.Constants

class GpObject {

    static final OBJECT_TYPE_MAP = [(Constants.OBJ_TREE): Constants.TYPE_TREE, (Constants.OBJ_BLOB): Constants.TYPE_BLOB]

    String id
    String type
    String path
    String name

    GpObject(TreeWalk walk) {
        this.id = walk.getObjectId(0).name
        this.type =  OBJECT_TYPE_MAP.get(walk.getFileMode(0).objectType)
        this.path = walk.pathString
        this.name =  walk.nameString
    }

}
