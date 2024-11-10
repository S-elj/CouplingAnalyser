package analysis;

import analysis.InfoModel.Dendrogram;
import analysis.graph.CallGraph;
import analysis.graph.CouplingGraph;

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
        CouplingGraph couplingGraph = new CouplingGraph(callGraph, analyzer.getClasses());

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
        //couplingGraph.printGraph();


        Clustering clustering = new Clustering(couplingGraph);

        double seuil = 0.02;

        //Dendrogram dendrogram = clustering.performClustering();
        Dendrogram minCouplingDendo = clustering.performClusturingWithMinimalCoupling(seuil);


        String outputDir = "src/output";
        File dir = new File(outputDir);

        // Créer le dossier s'il n'existe pas
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Exporter le graphe dans le fichier .dot
        String couplingGraphOutputPath = outputDir + "/coupleGraph.dot";
        String DendrogramOutputPath = outputDir + "/dendrogram.dot";
        String minCdendo = outputDir + "/MinCouplingdendrogram.dot";


        couplingGraph.exportToDot(couplingGraphOutputPath);
        //dendrogram.exportToDot(DendrogramOutputPath, 100);
        minCouplingDendo.exportToDot(minCdendo, 100);
        System.out.println("Le fichier .dot a été exporté vers : " + couplingGraphOutputPath);
        System.out.println("Le fichier .dot a été exporté vers : " + DendrogramOutputPath);
    }
}
