package net.donotturnoff.simpledoc.common;

// Convenience class, represents either a Request or a Response
public class Message {
    private final String head;
    private final byte[] body;

    public Message(Request request) {
        this.head = request.getHead();
        this.body = request.getBody();
    }

    public Message(Response response) {
        this.head = response.getHead();
        this.body = response.getBody();
    }

    public Message(String head, byte[] body) {
        this.head = head;
        this.body = body;
    }

    public String getHead() {
        return head;
    }

    public byte[] getBody() {
        return body;
    }
}
