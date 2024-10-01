package analysis;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CallGraph {
    // Ensemble des nœuds (méthodes)
    private Set<String> nodes = new HashSet<>();

    // Ensemble des arcs (appel entre deux méthodes)
    private Set<String> edges = new HashSet<>();

    // Ajouter un nœud (méthode)
    public void addNode(String method) {
        nodes.add(method);
    }

    // Ajouter un arc (appel de méthode)
    public void addEdge(String caller, String callee) {
        nodes.add(caller);  // Assurez-vous que le nœud existe
        nodes.add(callee);  // Assurez-vous que la méthode appelée existe
        edges.add(caller + " -> " + callee); // Représentation de l'arc
    }

    // Méthode pour afficher le graphe
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

    public void exportToDot(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("digraph CallGraph {\n");

        // Pour chaque arc, échapper les identifiants en utilisant des guillemets
        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            String caller = "\"" + parts[0] + "\"";
            String callee = "\"" + parts[1] + "\"";
            writer.write("  " + caller + " -> " + callee + ";\n");
        }

        writer.write("}\n");
        writer.close();
    }

}
