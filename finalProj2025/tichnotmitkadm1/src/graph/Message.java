package graph;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    // Constructor for string message
    public Message(String text) {
        this.asText = text;
        this.data = text.getBytes();
        double parsed;
        try {
            parsed = Double.parseDouble(text);
        } catch (Exception e) {
            parsed = Double.NaN;
        }
        this.asDouble = parsed;
        this.date = new Date();
    }

    // Constructor for double message
    public Message(double number) {
        this.asText = String.valueOf(number);
        this.data = this.asText.getBytes();
        this.asDouble = number;
        this.date = new Date();
    }

    // Constructor for byte[] data
    public Message(byte[] data) {
        this.data = data;
        this.asText = new String(data);
        this.asDouble = Double.NaN;  // Default to NaN for byte array input
        this.date = new Date();
    }
    public String getString(){
        return this.asText;
    }
}
