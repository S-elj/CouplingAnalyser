package analysis;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java Main <path_to_project>");
            return;
        }

        String projectSourcePath = args[0];

        // Créer le graphe d'appel
        CallGraph callGraph = new CallGraph();

        // Créer l'analyseur avec le chemin du projet
        CodeAnalyzer analyzer = new CodeAnalyzer(projectSourcePath);

        // Analyser le projet et construire le graphe d'appel
        analyzer.buildGraph(callGraph);

        // Afficher le graphe d'appel
       // callGraph.printGraph();

        // Créer le graphe de couplage
        CouplingGraph couplingGraph = new CouplingGraph(callGraph);

//        // Calculer la métrique de couplage entre deux classes, par exemple Tobacco et Necessity
//        String classA = "Dump1";
//        String classB = "Dump2";
//
//        double couplingMetric = couplingGraph.computeCoupling(classA, classB);
//        System.out.println("\nCouplage entre " + classA + " et " + classB + " : " + couplingMetric);
//
//
//        double couplingMetric2 = couplingGraph.computeCoupling(classB, classA);
//        System.out.println("\nCouplage entre " + classB + " et " + classA + " : " + couplingMetric2);

        couplingGraph.buildCouplingGraph();
        couplingGraph.printGraph();

        String outputDir = "src/output";
        File dir = new File(outputDir);

        // Créer le dossier s'il n'existe pas
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Exporter le graphe dans le fichier .dot
        String outputPath = outputDir + "/coupleGraph.dot";
        couplingGraph.exportToDot(outputPath);
        System.out.println("Le fichier .dot a été exporté vers : " + outputPath);
    }
}
