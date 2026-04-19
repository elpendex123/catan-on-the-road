package com.enrique.catanontheroad.game.phase;

public class ActionPhase {

    private boolean tradeUsed;
    private boolean substituteUsed;

    public ActionPhase() {
        this.tradeUsed = false;
        this.substituteUsed = false;
    }

    public boolean isTradeAvailable() {
        return !tradeUsed;
    }

    public boolean isSubstituteAvailable() {
        return !substituteUsed;
    }

    public void markTradeUsed() {
        this.tradeUsed = true;
    }

    public void markSubstituteUsed() {
        this.substituteUsed = true;
    }

    public void reset() {
        this.tradeUsed = false;
        this.substituteUsed = false;
    }
}
