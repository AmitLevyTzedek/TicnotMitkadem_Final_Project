package graph;

import configs.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Graph extends ArrayList<Node> {
    // Constructor for Graph class
    public Graph() {
        super(); // Calls ArrayList constructor
    }
    public ArrayList<Node> getNodes() {return this;}

    // Method to check if the graph has cycles (using depth-first search)
    public boolean hasCycles() {
        List<Node> visited = new ArrayList<>();
        List<Node> inStack = new ArrayList<>();

        // Check each node for cycles
        for (Node node : this) {
            if (node.hasCycles(visited, inStack)) {
                return true;
            }
        }
        return false;
    }

    // Method to create the graph from topics and agents
    public void createFromTopics() {
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        Collection<Topic> allTopics=topicManager.getTopics();



        for (Topic topic : allTopics) {


            Node node = new Node('T'+topic.name);
            this.add(node);

            for (Agent topicAgent : topic.getSubscribers()) {
                // יצירת קודקוד חדש עבור כל סוכן ברשימה
                Node agentNode = null;
                for (Node existingNode : this) {


                    if (existingNode.getName().equals('A'+topicAgent.getName())) {
                        agentNode = existingNode;  // נמצא נוד קיים עבור הסוכן

                        break;
                    }
                }

                // אם לא נמצא נוד עבור הסוכן, ניצור נוד חדש
                if (agentNode == null) {
                    agentNode = new Node('A'+topicAgent.getName());
                    this.add(agentNode);  // הוספת הנוד לגרף

                }


                // הוספת קשת יוצאת מהנושא לכל סוכן
                node.addEdge(agentNode);  // הוספת קשת בין הנושא לסוכן


            }
            for (Agent topicAgent : topic.getpublishers()) {

                // בדיקה אם כבר קיים נוד עבור הסוכן הזה
                Node agentNode = null;
                for (Node existingNode : this) {


                    if (existingNode.getName().equals('A'+topicAgent.getName())) {
                        agentNode = existingNode;  // נמצא נוד קיים עבור הסוכן

                        break;
                    }
                }

                // אם לא נמצא נוד עבור הסוכן, ניצור נוד חדש
                if (agentNode == null) {
                    agentNode = new Node('A'+topicAgent.getName());
                    this.add(agentNode);  // הוספת הנוד לגרף

                }

                // הוספת קשת יוצאת מהנושא לכל סוכן
                agentNode.addEdge(node);


            }
            }
    }

}