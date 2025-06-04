package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
    private final Agent agent; // The agent we are decorating
    private final BlockingQueue<Message> queue; // The queue for messages
    private final Thread processingThread; // The thread for processing messages

    // Constructor to initialize the agent and the queue
    public ParallelAgent(Agent agent, int queueCapacity) {
        this.agent = agent;
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        // Create and start a new thread to process the queue
        this.processingThread = new Thread(this::processMessages);
        this.processingThread.start();
    }

    // Implementing the callback method to put the message in the queue
    @Override
    public void callback(String topic, Message msg) {
        try {
            queue.put(msg); // Put the message in the queue, blocking if full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // The method that runs in the background thread to process messages
    private void processMessages() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message msg = queue.take(); // Take a message from the queue
                String topic = msg.asText; // Extract the topic from the message
                // Call the original agent's callback
                agent.callback(topic, msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Exit the loop if interrupted
            }
        }
    }

    // Method to close the agent and stop the processing thread
    public void close() {
        processingThread.interrupt(); // Interrupt the thread to stop processing
        agent.close();  // Close the decorated agent if needed
    }

    // Implementing other methods of Agent as a decorator
    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        agent.reset();
    }
}
