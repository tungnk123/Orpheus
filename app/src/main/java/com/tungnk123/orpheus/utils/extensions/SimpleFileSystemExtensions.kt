package com.tungnk123.orpheus.utils.extensions

import com.tungnk123.orpheus.utils.file.SimpleFileSystem
import com.tungnk123.orpheus.utils.file.SimplePath

fun SimpleFileSystem.Folder.navigateToFolder(path: SimplePath): SimpleFileSystem.Folder? {
    var folder: SimpleFileSystem.Folder? = this
    path.parts.forEach { x ->
        folder = folder?.let {
            val child = it.children[x]
            child as? SimpleFileSystem.Folder
        }
    }
    return folder
}
