package analysis.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class CallGraph {
    private Set<String> nodes = new HashSet<>();
    private Set<String> edges = new HashSet<>();

    public void addNode(String method) {
        nodes.add(method);
    }

    public void addEdge(String caller, String callee) {
        nodes.add(caller);
        nodes.add(callee);
        edges.add(caller + " -> " + callee); // Représentation de l'arc
    }


    public Set<String> getClasses() {
        System.out.println("demande des classes au graphe d'appel");
        Set<String> classes = new HashSet<>();
        for (String method : nodes) {
            String className = getClassFromMethod(method);
            if (!className.equals("Unknown")) {
                classes.add(className);
            }
        }
        System.out.println(classes.toString());
        return classes;
    }

    public String getClassFromMethod(String method) {
        String[] parts = method.split("\\.");
        if (parts.length < 2) {
            return "Unknown";
        }
        return parts[parts.length - 2];
    }

    public void exportToDot(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("digraph CallGraph {\n");

        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            String caller = "\"" + parts[0] + "\"";
            String callee = "\"" + parts[1] + "\"";
            writer.write("  " + caller + " -> " + callee + ";\n");
        }

        writer.write("}\n");
        writer.close();
    }

    public void printGraph() {
        System.out.println("Nodes (Methods):");
        for (String node : nodes) {
            System.out.println(node);
        }

        System.out.println("\nEdges (Method Calls):");
        for (String edge : edges) {
            System.out.println(edge);
        }
    }

    public Set<String> getEdges() {
        return edges;
    }

}
