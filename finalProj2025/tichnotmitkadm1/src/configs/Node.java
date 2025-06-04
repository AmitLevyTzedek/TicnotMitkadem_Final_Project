package configs;

import graph.Message;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String name;       // שם ה-Node
    private List<Node> edges;  // רשימה של קודקודים המקשרים ל-Node הזה
    private Message message;   // הודעה שנשמרת ב-Node

    // Constructor
    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
        this.message = null;  // הודעה אינה מוקצת בהתחלה
    }

    // Getter ל-Name
    public String getName() {
        return name;
    }

    // Setter ל-Name
    public void setName(String name) {
        this.name = name;
    }

    // Getter ל-Edges
    public List<Node> getEdges() {
        return edges;
    }

    // Setter ל-Edges
    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    // Getter להודעה
    public Message getMessage() {
        return message;
    }

    // Setter להודעה
    public void setMessage(Message message) {
        this.message = message;
    }

    // Method to add an edge to this node
    public void addEdge(Node node) {
        if (!edges.contains(node)) {
            edges.add(node);
        }
    }

    // Method to check for cycles (Depth-First Search)
    public boolean hasCycles(List<Node> visitedNodes, List<Node> inStack) {
        // If the node is already in the current recursion stack, we found a cycle
        if (inStack.contains(this)) {
            return true;
        }

        // If it's already visited, skip it
        if (visitedNodes.contains(this)) {
            return false;
        }

        // Mark the node as visited and add it to the recursion stack
        visitedNodes.add(this);
        inStack.add(this);

        // Check all neighbors (edges)
        for (Node neighbor : edges) {
            if (neighbor.hasCycles(visitedNodes, inStack)) {
                return true;
            }
        }

        // Remove node from the recursion stack after processing
        inStack.remove(this);
        return false;
    }
    // Overloaded method to check for cycles without parameters
    public boolean hasCycles() {
        List<Node> visitedNodes = new ArrayList<>();
        List<Node> inStack = new ArrayList<>();
        return hasCycles(visitedNodes, inStack);  // Call the original method with parameters
    }
}
