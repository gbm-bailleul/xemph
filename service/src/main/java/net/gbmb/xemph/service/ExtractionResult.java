package net.gbmb.xemph.service;

import net.gbmb.xemph.Packet;

import java.util.UUID;

public class ExtractionResult {

    private Exception failingException;

    private Packet packet;

    private String identifier;

    private boolean performed;

    public ExtractionResult() {
        identifier = UUID.randomUUID().toString();
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

    public String getIdentifier() {
        return identifier;
    }

    public void setFailingException(Exception failingException) {
        this.failingException = failingException;
        this.performed = false;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
        this.performed = true;
    }
}
