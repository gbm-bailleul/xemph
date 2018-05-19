package net.gbmb.xemph.service;

import net.gbmb.xemph.Packet;

import java.util.UUID;

public class ExtractionResult {

    private Exception failingException;

    private Packet packet;

    private boolean performed;

    private String identifier;

    public ExtractionResult() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public void setFailingException(Exception failingException) {
        this.failingException = failingException;
        this.performed = false;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
        this.performed = true;
    }
}
