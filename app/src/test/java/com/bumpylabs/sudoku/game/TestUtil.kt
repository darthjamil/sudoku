package com.bumpylabs.sudoku.game

internal fun Array<IntArray>.pretty(): String {
    var str = ""

    this.forEachIndexed { i, row ->
        str += "[${row.joinToString(", ")}]"
        str += "\n"
        if ((i + 1) % 3 == 0) { str += "\n" }
    }

    return str
}