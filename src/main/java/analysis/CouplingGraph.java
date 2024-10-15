package analysis;

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

    // Constructeur qui prend le graphe d'appel
    public CouplingGraph(CallGraph callGraph) {
        this.callGraph = callGraph;
    }


    public void buildCouplingGraph() {
        Set<String> nodes = callGraph.getClasses();  // Obtenir les classes (à partir des méthodes)

        // Générer toutes les paires possibles de classes et calculer le couplage
        for (String classA : nodes) {
            for (String classB : nodes) {
                if (!classA.equals(classB)) {
                    double coupling = computeCoupling(classA, classB);
                    if (coupling > 0) {
                        String pair = classA.compareTo(classB) < 0 ?
                                classA + "->" + classB : classB + "->" + classA;
                        weightedEdges.put(pair, coupling);
                        System.out.println("Added: " + pair + ": " + coupling);

                    }
                }
            }
        }
    }


    // Compter les appels entre les classes A et B
    public int countCallsBetweenClasses(String classA, String classB) {
        Set<String> uniqueCalls = new HashSet<>();

        for (String edge : callGraph.getEdges()) {
            String[] parts = edge.split(" -> ");
            String caller = parts[0];  // Récupérer l'appelant complet (Class.Method)
            String callee = parts[1];  // Récupérer l'appelé complet (Class.Method)

            String callerClass =getClassFromMethod(caller);
            String calleeClass = getClassFromMethod(callee);

            // Vérifier si l'appel est entre les classes A et B (A->B ou B->A)
            if ((callerClass.equals(classA) && calleeClass.equals(classB)) ||
                    (callerClass.equals(classB) && calleeClass.equals(classA))) {
                // Ajouter un appel unique entre A et B
                String call = caller + "->" + callee;
                uniqueCalls.add(call);
            }
        }

        return uniqueCalls.size();  // Retourner le nombre d'appels uniques entre les méthodes de A et B
    }

    // Méthode pour compter toutes les relations binaires dans l'application
    public int countTotalBinaryRelations() {
        Set<String> uniqueRelations = new HashSet<>();

        for (String edge : callGraph.getEdges()) {
            String[] parts = edge.split(" -> ");
            String callerClass = getClassFromMethod(parts[0]);  // Récupérer la classe appelante
            String calleeClass = getClassFromMethod(parts[1]);  // Récupérer la classe appelée

            // On s'assure que la relation est ajoutée de manière unique (indépendamment de l'ordre)
            String pair = callerClass.compareTo(calleeClass) < 0 ?
                    callerClass + "->" + calleeClass : calleeClass + "->" + callerClass;

            uniqueRelations.add(pair);
        }

        return uniqueRelations.size();  // Retourner le nombre total de relations binaires
    }

    // Calculer la métrique de couplage entre deux classes
    public double computeCoupling(String classA, String classB) {
        // Compter les appels entre les méthodes des classes A et B
        int callsBetweenAandB = countCallsBetweenClasses(classA, classB);
        System.out.println("Appels entre " + classA + " et " + classB + ": " + callsBetweenAandB);

        // Compter le nombre total de relations binaires entre toutes les classes
        int totalBinaryRelations = countTotalBinaryRelations();
        System.out.println("Nombre total de relations binaires dans l'application: " + totalBinaryRelations);

        if (totalBinaryRelations == 0) {
            return 0;  // Éviter la division par zéro
        }

        // Calculer et retourner la métrique de couplage arrondie à trois chiffres après la virgule
        return Math.round(((double) callsBetweenAandB / totalBinaryRelations) * 1000.0) / 1000.0;
    }

    public String getClassFromMethod(String method) {
        String[] parts = method.split("\\.");
        if (parts.length < 2) {
            return "Unknown";  // Retourner "Unknown" si le format n'est pas conforme
        }
        return parts[parts.length - 2];  // La classe se trouve juste avant le nom de la méthode
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
    // Méthode pour exporter le graphe pondéré au format DOT avec longueur d'arc proportionnelle au poids

}
