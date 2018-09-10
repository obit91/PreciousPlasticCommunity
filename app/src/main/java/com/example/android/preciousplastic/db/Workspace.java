package com.example.android.preciousplastic.db;

public class Workspace {

    private boolean shredderMachine;
    private boolean injectionMachine;
    private boolean extrusionMachine;
    private boolean compressionMachine;

    /**
     * Default empty constructor for firebase.
     */
    public Workspace() {

    }

    /**
     * Constructor for a new machine.
     *
     * @param shredderMachine    indicator if the workspace contains a shredder machine.
     * @param injectionMachine   indicator if the workspace contains an injection machine.
     * @param extrusionMachine   indicator if the workspace contains an extrusion machine.
     * @param compressionMachine indicator if the workspace contains a compression machine.
     */
    public Workspace(boolean shredderMachine, boolean injectionMachine, boolean extrusionMachine, boolean compressionMachine) {
        this.shredderMachine = shredderMachine;
        this.injectionMachine = injectionMachine;
        this.extrusionMachine = extrusionMachine;
        this.compressionMachine = compressionMachine;
    }

    public boolean isShredderMachine() {
        return shredderMachine;
    }

    public void setShredderMachine(boolean shredderMachine) {
        this.shredderMachine = shredderMachine;
    }

    public boolean isInjectionMachine() {
        return injectionMachine;
    }

    public void setInjectionMachine(boolean injectionMachine) {
        this.injectionMachine = injectionMachine;
    }

    public boolean isExtrusionMachine() {
        return extrusionMachine;
    }

    public void setExtrusionMachine(boolean extrusionMachine) {
        this.extrusionMachine = extrusionMachine;
    }

    public boolean isCompressionMachine() {
        return compressionMachine;
    }

    public void setCompressionMachine(boolean compressionMachine) {
        this.compressionMachine = compressionMachine;
    }
}
