package analysis;

import analysis.InfoModel.ClassInfo;
import analysis.InfoModel.MethodCallInfo;
import analysis.InfoModel.MethodInfo;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java Main <path_to_project>");
            return;
        }
        String projectSourcePath = args[0];

        CodeAnalyzer analyzer = new CodeAnalyzer(projectSourcePath);
        analyzer.analyze();

        System.out.println("Nombre de classes : " + analyzer.getClassCount());
        System.out.println("Nombre total de lignes de code : " + analyzer.getLineCount());
        System.out.println("Nombre total de méthodes : " + analyzer.getMethodCount());

        // Afficher les détails des classes, méthodes et appels de méthode
        for (ClassInfo classInfo : analyzer.getClassesInfo()) {
            System.out.println("Classe : " + classInfo.getClassName());

            for (MethodInfo methodInfo : classInfo.getMethods()) {
                System.out.println("  Méthode : " + methodInfo.getMethodName());

                for (MethodCallInfo methodCall : methodInfo.getMethodCalls()) {
                    System.out.println("    Appel de méthode : " + methodCall.getCalledMethodName() +
                            " (Type du receveur : " + methodCall.getReceiverType() + ")");
                }
            }
        }
    }
}
