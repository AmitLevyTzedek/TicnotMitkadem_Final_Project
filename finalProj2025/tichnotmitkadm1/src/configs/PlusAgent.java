package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;

public class PlusAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;
    private double xValue; // Holds the value of x
    private double yValue; // Holds the value of y

    public PlusAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;
        xValue = Double.NaN;
        yValue = Double.NaN;

        if (subs.length > 1 && pubs.length>=1) {
            // Subscribe to the input topics
            TopicManagerSingleton.get().getTopic(pubs[0]).addPublisher(this);
            TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);
            TopicManagerSingleton.get().getTopic(subs[1]).subscribe(this);
        }

    }

    @Override
    public String getName() {
        return "PlusAgent";
    }

    @Override
    public void reset() {
        xValue = Double.NaN;
        yValue = Double.NaN;
    }




    @Override
    public void callback(String topic, Message msg) {
        if (subs.length < 2 || pubs.length != 1) {
            System.err.println("ERROR: PlusAgent requires exactly two subscription topics and one publish topic.");
            return;
        }
        // Store the received value
        if (topic.equals(subs[0])) {
            xValue = msg.asDouble;
        } else if (topic.equals(subs[1])) {
            yValue = msg.asDouble;
        }
        // Only publish when both values are available
        if (!Double.isNaN(xValue) && !Double.isNaN(yValue)) {
            double sum = xValue + yValue;
            Message mes = new Message(sum);
            TopicManagerSingleton.get().getTopic(pubs[0]).publish(mes);
            // Reset input values after publishing
            xValue = Double.NaN;
            yValue = Double.NaN;
        }
    }


    @Override
    public void close() {
        // סגירת הסוכן
    }
}
