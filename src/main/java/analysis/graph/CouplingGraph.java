package analysis.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CouplingGraph {
    private CallGraph callGraph;
    private Map<String, Double> weightedEdges = new HashMap<>();
    private Set<String> nodes;
    private int totalBinaryRelations = -1;
    private int actualBinaryRelations = -1;

    public CouplingGraph(CallGraph callGraph, Set<String> nodes) {
        this.callGraph = callGraph;
        this.nodes = nodes;
    }

    public void buildCouplingGraph() {
        System.out.println("CLASSES BEFORE COUPLINGE= " + nodes.toString());
        double sum = 0.0;


        for (String classA : nodes) {
            int count = 0;
            for (String classB : nodes) {
                if (classA.compareTo(classB) < 0) {
                    double coupling = computeCoupling(classA, classB);
                    if (coupling > 0) {
                        String pair = classA.compareTo(classB) < 0 ? classA + "->" + classB : classB + "->" + classA;
                        weightedEdges.put(pair, coupling);
                        System.out.println("Added: " + pair + ": " + coupling);
                        count++;
                        sum += coupling;
                    }
                }
            }
        }
        System.out.println("nb relatios totales calculées:" + totalBinaryRelations);
        System.out.println("nb relations enrgistrées: " + actualBinaryRelations);
        System.out.println("coupling sum: " + sum);
    }


    public int countCallsBetweenClasses(String classA, String classB) {
        Set<String> uniqueCalls = new HashSet<>();

        for (String edge : callGraph.getEdges()) {
            String[] parts = edge.split(" -> ");
            String callerClass = getClassFromMethod(parts[0]);
            String calleeClass = getClassFromMethod(parts[1]);

            if ((callerClass.equals(classA) && calleeClass.equals(classB)) ||
                    (callerClass.equals(classB) && calleeClass.equals(classA))) {
                uniqueCalls.add(parts[0] + "->" + parts[1]);
            }
        }

        return uniqueCalls.size();
    }

    // Compter toutes les relations binaires dans l'application (caché pour optimisation)
    public int countTotalBinaryRelations() {
        if (totalBinaryRelations == -1) {
            double sum = 0.0;
            for (String classA : nodes) {
                for (String classB : nodes) {
                    if (classA.compareTo(classB) < 0) {
                        double calls = countCallsBetweenClasses(classA, classB);
                        totalBinaryRelations += calls;
                    }
                }
            }
        }
        return totalBinaryRelations;
    }

    // Calculer la métrique de couplage entre deux classes
    public double computeCoupling(String classA, String classB) {
        int callsBetweenAandB = countCallsBetweenClasses(classA, classB);
        if (callsBetweenAandB != 0) {
            System.out.println(callsBetweenAandB + "   appels entre " + classA + " et " + classB);
            actualBinaryRelations += callsBetweenAandB;
            int totalBinaryRelations = countTotalBinaryRelations();


            return Math.round(((double) callsBetweenAandB / totalBinaryRelations) * 1000.0) / 1000.0;

        }
        return callsBetweenAandB;
    }

    public String getClassFromMethod(String method) {
        String[] parts = method.split("\\.");
        if (parts.length < 2) {
            System.err.println("Format de méthode incorrect: " + method);
            return "Unknown";
        }
        return parts[parts.length - 2];
    }

    public void printGraph() {
        Set<String> classes = callGraph.getClasses();
        System.out.println("Nodes (Classes):");
        for (String className : classes) {
            System.out.println(className);
        }

        System.out.println("\nEdges (Class Coupling):");
        for (Map.Entry<String, Double> entry : weightedEdges.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    public void exportToDot(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("digraph CouplingGraph {\n");

        // Constante pour ajuster les longueurs des arcs
        double scalingFactor = 10.0;

        // Exporter chaque arc (relation de couplage pondéré entre classes)
        for (Map.Entry<String, Double> entry : weightedEdges.entrySet()) {
            String[] parts = entry.getKey().split("->");
            String classA = "\"" + parts[0].trim() + "\"";
            String classB = "\"" + parts[1].trim() + "\"";
            double weight = entry.getValue();

            // Calcul de la longueur de l'arc (plus le poids est élevé, plus la longueur est courte)
            // On utilise un scaling factor pour rendre les différences plus visibles dans la visualisation
            double length = scalingFactor / weight;  // Inverser proportionnellement le poids pour la longueur

            // Écrire l'arc avec l'étiquette et la longueur
            writer.write("  " + classA + " -> " + classB + " [label=\"" + weight + "\", len=\"" + length + "\", dir=\"both\"];\n");
        }

        writer.write("}\n");
        writer.close();
    }

    public Map<String, Double> getWeightedEdges() {
        return this.weightedEdges;
    }

    public Set<String> getClasses() {
        return this.nodes;
    }


}
