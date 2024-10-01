package analysis;

import analysis.InfoModel.ClassInfo;
import analysis.InfoModel.MethodCallInfo;
import analysis.InfoModel.MethodInfo;

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

        // Analyser le projet et construire le graphe
        CodeAnalyzer analyzer = new CodeAnalyzer(projectSourcePath);
        analyzer.analyze(callGraph); // Passez le graphe à l'analyseur

        // Afficher le graphe
        callGraph.printGraph();
        String outputDir = "src/res";
        File dir = new File(outputDir);

        // Créer le dossier s'il n'existe pas
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Exporter le graphe dans le fichier .dot
        String outputPath = outputDir + "/callgraph.dot";
        callGraph.exportToDot(outputPath);
        System.out.println("Le fichier .dot a été exporté vers : " + outputPath);
    }
}
