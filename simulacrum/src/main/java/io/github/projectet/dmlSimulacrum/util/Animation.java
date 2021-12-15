package io.github.projectet.dmlSimulacrum.util;

public class Animation {
    private boolean hasStarted = false;
    private int renderPos = 0;
    private int renderStop = 0;
    private int currentTick = 0;
    private long lastAlterTick = 0;
    private String currentString = "";

    public String animate(String string, int totalTickTime, long currentWorldTick, boolean loop) {
        if(lastAlterTick != currentWorldTick) {
            alterString(string, totalTickTime, loop);
            lastAlterTick = currentWorldTick;
        }

        return currentString;
    }

    public void alterString(String string, int totalTickTime, boolean loop) {
        if(!hasStarted) {
            this.hasStarted = true;
            this.renderPos = 0;
            this.renderStop = string.length();
        } else {
            currentTick++;
            if(renderPos < renderStop) {
                if(renderPos >= 0) {
                    this.currentString = string.substring(0, renderPos);
                }
                this.renderPos = currentTick % totalTickTime == 0 ? renderPos + 1 : renderPos;
            } else {
                if(loop) {
                    this.hasStarted = currentTick % totalTickTime != 0;
                }
                this.currentString = string;
            }
        }
    }

    public boolean hasFinished() {
        return renderPos == renderStop && renderStop != 0;
    }

    public void clear() {
        this.currentString = "";
        this.hasStarted = false;
        this.renderPos = 0;
        this.renderStop = 0;
        this.currentTick = 0;
    }
}
