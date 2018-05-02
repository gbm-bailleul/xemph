package net.gbmb.xemph.service;

import net.gbmb.xemph.Packet;

public class ExtractionResult {

    private Exception failingException;

    private Packet packet;

    private boolean performed;

    public ExtractionResult(Exception failingException) {
        this.failingException = failingException;
        this.performed = false;
    }

    public ExtractionResult(Packet packet) {
        this.packet = packet;
        this.performed = true;
    }

    public Exception getFailingException() {
        return failingException;
    }

    public Packet getPacket() {
        return packet;
    }

    public boolean isPerformed() {
        return performed;
    }
}
