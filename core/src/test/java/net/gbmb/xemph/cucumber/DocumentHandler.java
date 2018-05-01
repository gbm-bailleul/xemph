package net.gbmb.xemph.cucumber;

import org.w3c.dom.Document;

public class DocumentHandler {

    private static ThreadLocal<Document> document = new ThreadLocal<>();

    public static Document getDocument() {
        return document.get();
    }

    protected static void setDocument (Document doc) {
        document.set(doc);
    }

}
