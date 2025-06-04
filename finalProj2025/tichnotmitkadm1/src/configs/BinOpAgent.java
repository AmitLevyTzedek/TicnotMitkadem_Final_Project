package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent {
    private String name;
    private String input1Topic;
    private String input2Topic;
    private String outputTopic;
    private BinaryOperator<Double> operation;

    private Double lastInput1Value;
    private Double lastInput2Value;

    // Constructor to initialize the BinOpAgent with operation and topics
    public BinOpAgent(String name, String input1Topic, String input2Topic, String outputTopic, BinaryOperator<Double> operation) {
        this.name = name;
        this.input1Topic = input1Topic;
        this.input2Topic = input2Topic;
        this.outputTopic = outputTopic;
        this.operation = operation;

        // Subscribe to the input topics
        TopicManagerSingleton.get().getTopic(outputTopic).addPublisher(this);



        TopicManagerSingleton.get().getTopic(input1Topic).subscribe(this);
        TopicManagerSingleton.get().getTopic(input2Topic).subscribe(this);
    }

    @Override
    public void callback(String topic, Message msg) {
        // When a message is received from a topic, check if it's one of the input topics
        if (topic.equals(input1Topic)) {
            lastInput1Value = msg.asDouble;
        } else if (topic.equals(input2Topic)) {
            lastInput2Value = msg.asDouble;
        }

        // Perform the operation if both inputs are available
        if (lastInput1Value != null && lastInput2Value != null) {
            Double result = operation.apply(lastInput1Value, lastInput2Value); // Apply the binary operation
            Message resultMessage = new Message(result); // Create a new message with the result
            TopicManagerSingleton.get().getTopic(outputTopic).publish(resultMessage); // Publish the result
        }
    }

    @Override
    public void reset() {
        // Reset the agent state (if needed)
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() {
        // Close the agent if needed
    }
}
