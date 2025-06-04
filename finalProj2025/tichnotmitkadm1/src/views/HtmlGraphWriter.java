package views;

import graph.Graph;
import configs.Node;
import graph.Topic;
import graph.Agent;

import java.util.ArrayList;
import java.util.List;

public class HtmlGraphWriter {
    // Creates interactive HTML visualization of the graph using vis.js
    public static List<String> getGraphHTML(Graph graph) {
        List<String> html = new ArrayList<>();

        // Start HTML document
        html.add("<!DOCTYPE html>");
        html.add("<html lang=\"en\">");
        html.add("<head>");
        html.add("    <meta charset=\"UTF-8\">");
        html.add("    <title>Graph Visualization</title>");
        html.add("    <script type=\"text/javascript\" src=\"https://unpkg.com/vis-network/standalone/umd/vis-network.min.js\"></script>");
        html.add("    <style type=\"text/css\">");
        html.add("        #graph {");
        html.add("            width: 100%;");
        html.add("            height: 100vh;");
        html.add("            border: 1px solid #ddd;");
        html.add("            background-color: white;");
        html.add("        }");
        html.add("        body {");
        html.add("            margin: 0;");
        html.add("            padding: 0;");
        html.add("            font-family: Arial, sans-serif;");
        html.add("        }");
        html.add("    </style>");
        html.add("</head>");
        html.add("<body>");
        html.add("    <div id=\"graph\"></div>");
        html.add("    <script type=\"text/javascript\">");
        html.add("        // Create a network");
        html.add("        var container = document.getElementById('graph');");
        html.add("        ");
        html.add("        // Create nodes array");
        html.add("        var nodes = new vis.DataSet([");

        // Add nodes
        for (Node node : graph.getNodes()) {
            String nodeId = node.getName();
            String nodeName = node.getName();
            String shape = nodeName.startsWith("T") ? "box" : "circle";
            String color = nodeName.startsWith("T") ? "#A5D6A7" : "#90CAF9";
            String label = nodeName.substring(1); // Remove the T or A prefix

            html.add(String.format(
                    "            { id: '%s', label: '%s', shape: '%s', color: '%s' },",
                    nodeId, label, shape, color
            ));
        }

        html.add("        ]);");
        html.add("        ");
        html.add("        // Create edges array");
        html.add("        var edges = new vis.DataSet([");

        // Add edges
        for (Node node : graph.getNodes()) {
            for (Node target : node.getEdges()) {
                html.add(String.format(
                        "            { from: '%s', to: '%s', arrows: 'to' },",
                        node.getName(), target.getName()
                ));
            }
        }

        html.add("        ]);");
        html.add("        ");
        html.add("        // Create data object");
        html.add("        var data = {");
        html.add("            nodes: nodes,");
        html.add("            edges: edges");
        html.add("        };");
        html.add("        ");
        html.add("        // Create options object");
        html.add("        var options = {");
        html.add("            nodes: {");
        html.add("                font: {");
        html.add("                    size: 16,");
        html.add("                    multi: true,");
        html.add("                    face: 'Arial',");
        html.add("                    color: '#333333'");
        html.add("                },");
        html.add("                size: 40,");
        html.add("                borderWidth: 2,");
        html.add("                shadow: true,");
        html.add("                margin: 10");
        html.add("            },");
        html.add("            edges: {");
        html.add("                width: 3,");
        html.add("                color: {");
        html.add("                    color: '#2B7CE9',");
        html.add("                    highlight: '#1B4B99',");
        html.add("                    hover: '#1B4B99'");
        html.add("                },");
        html.add("                smooth: {");
        html.add("                    type: 'curvedCW',");
        html.add("                    roundness: 0.2");
        html.add("                },");
        html.add("                arrows: {");
        html.add("                    to: {");
        html.add("                        enabled: true,");
        html.add("                        scaleFactor: 1.5,");
        html.add("                        type: 'arrow'");
        html.add("                    }");
        html.add("                },");
        html.add("                shadow: true");
        html.add("            },");
        html.add("            physics: {");
        html.add("                enabled: true,");
        html.add("                solver: 'forceAtlas2Based',");
        html.add("                forceAtlas2Based: {");
        html.add("                    gravitationalConstant: -200,");
        html.add("                    springLength: 300,");
        html.add("                    springConstant: 0.05,");
        html.add("                    damping: 0.4");
        html.add("                },");
        html.add("                stabilization: {");
        html.add("                    enabled: true,");
        html.add("                    iterations: 1000,");
        html.add("                    updateInterval: 25");
        html.add("                }");
        html.add("            },");
        html.add("            layout: {");
        html.add("                improvedLayout: true,");
        html.add("                hierarchical: {");
        html.add("                    enabled: false");
        html.add("                }");
        html.add("            },");
        html.add("            interaction: {");
        html.add("                hover: true,");
        html.add("                tooltipDelay: 200");
        html.add("            }");
        html.add("        };");
        html.add("        ");
        html.add("        // Create the network");
        html.add("        var network = new vis.Network(container, data, options);");
        html.add("        ");
        html.add("        // Add event listeners for better interaction");
        html.add("        network.on('stabilizationProgress', function(params) {");
        html.add("            console.log('Stabilization progress:', params.iterations, '/', params.total);");
        html.add("        });");
        html.add("        ");
        html.add("        network.on('stabilizationIterationsDone', function() {");
        html.add("            console.log('Stabilization finished');");
        html.add("        });");
        html.add("    </script>");
        html.add("</body>");
        html.add("</html>");

        return html;
    }
}