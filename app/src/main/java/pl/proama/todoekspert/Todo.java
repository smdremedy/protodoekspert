package pl.proama.todoekspert;

import java.io.Serializable;

public class Todo implements Serializable {

    public Todo(String content, boolean done) {
        this.content = content;
        this.done = done;
    }

    private String content;
    private boolean done;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "content='" + content + '\'' +
                ", done=" + done +
                '}';
    }
}
