package graph;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    private List<Agent> subscribers = new ArrayList<>();
    private List<Agent> publishers = new ArrayList<>();
    private Message message=new Message("");

    // Constructor for Topic class
    Topic(String name) {
        this.name = name;
    }

    public List<Agent> getSubscribers() {
        return this.subscribers;
    }
    public List<Agent> getpublishers() {
        return this.publishers;
    }

    // Subscribe an agent to the topic
    public void subscribe(Agent a) {
        if (!subscribers.contains(a)) {
            subscribers.add(a);
        }
    }

    // Unsubscribe an agent from the topic
    public void unsubscribe(Agent a) {
        subscribers.remove(a);
    }

    // Publish a message to all subscribers
    public void publish(Message m) {
        for (Agent agent : subscribers) {
            agent.callback(name, m);
        }
        this.message = m;
    }

    // Add a publisher to the topic
    public void addPublisher(Agent a) {
        if (!publishers.contains(a)) {
            publishers.add(a);
        }
    }

    public Message getMessage() {

        return this.message;
    }
    public String getName() {
        return this.name;
    }

    // Remove a publisher from the topic
    public void removePublisher(Agent a) {
        publishers.remove(a);
    }
}
