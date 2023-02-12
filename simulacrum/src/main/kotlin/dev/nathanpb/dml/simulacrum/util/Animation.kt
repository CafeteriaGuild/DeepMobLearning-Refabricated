package dev.nathanpb.dml.simulacrum.util

class Animation {

    private var hasStarted = false
    private var renderPos = 0
    private var renderStop = 0
    private var currentTick = 0
    private var lastAlterTick: Long = 0
    private var currentString = ""


    fun animate(key: String, totalTickTime: Int, currentWorldTick: Long, loop: Boolean): String {
        return animate(key, totalTickTime, currentWorldTick, loop, -1)
    }

    fun animate(key: String, totalTickTime: Int, currentWorldTick: Long, loop: Boolean, intArg: Int): String {
        if (lastAlterTick != currentWorldTick) {
            alterString(key, totalTickTime, loop)
            lastAlterTick = currentWorldTick
        }
        return currentString
    }

    fun alterString(string: String, totalTickTime: Int, loop: Boolean) {
        if (!hasStarted) {
            hasStarted = true
            renderPos = 0
            renderStop = string.length
        } else {
            currentTick++
            if (renderPos < renderStop) {
                if (renderPos >= 0) {
                    currentString = string.substring(0, renderPos)
                }
                renderPos = if (currentTick % totalTickTime == 0) renderPos + 1 else renderPos
            } else {
                if (loop) {
                    hasStarted = currentTick % totalTickTime != 0
                }
                currentString = string
            }
        }
    }

    fun hasFinished(): Boolean {
        return renderPos == renderStop && renderStop != 0
    }

    fun clear() {
        currentString = ""
        hasStarted = false
        renderPos = 0
        renderStop = 0
        currentTick = 0
    }
}