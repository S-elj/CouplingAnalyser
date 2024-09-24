package analysis;

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
    }
}
