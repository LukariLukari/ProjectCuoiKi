package tiil.edu.cuoiki.model;

import java.io.Serializable;

public class TranslatedSegment implements Serializable {
    private int id;
    private String content;

    public TranslatedSegment(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 