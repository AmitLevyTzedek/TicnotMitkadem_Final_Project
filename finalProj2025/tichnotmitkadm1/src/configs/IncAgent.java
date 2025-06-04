package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;


public class IncAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;

    public IncAgent(String[] subs, String[] pubs) {
        //System.out.println("subs:"+subs);
        //System.out.println("pubs:"+pubs);
        this.subs = subs;
        this.pubs = pubs;
        if (subs.length>0) {
            // Subscribe to the input topics
            TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);

        }
        if (pubs.length>0) {
            // Subscribe to the input topics
            TopicManagerSingleton.get().getTopic(pubs[0]).addPublisher(this);

        }

    }

    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {
        // לוגיקה של reset
    }




    @Override
    public void callback(String topic, Message msg) {
        if (subs.length < 1 || pubs.length != 1) {
            System.err.println("ERROR: IncAgent requires exactly one subscription topic and one publish topic.");
            return;
        }
        if (topic.equals(subs[0])) {
            double lastInput1Value = msg.asDouble;
            if (!Double.isNaN(lastInput1Value)) {
                double incremented = lastInput1Value + 1;
                Message mes = new Message(incremented);
                TopicManagerSingleton.get().getTopic(pubs[0]).publish(mes);
            }
        }
    }

    @Override
    public void close() {
        // סגירת הסוכן
    }
}