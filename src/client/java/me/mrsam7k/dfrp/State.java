package me.mrsam7k.dfrp;

public enum State {
    SPAWN("Spawn"),
    PLAY("Playing"),
    DEV("Coding"),
    BUILD("Building"),
    CODE_SPECTATE("Existing"),
    NONE("None");

    private final String action;

    State(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}
