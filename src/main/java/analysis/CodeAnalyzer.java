package analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import analysis.visitors.ClassVisitor;
import analysis.visitors.LineVisitor;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CodeAnalyzer {

    private String projectSourcePath;

    // Compteurs
    private int classCount = 0;
    private int lineCount = 0;

    public CodeAnalyzer(String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
    }

    public void analyze() throws IOException {
        // Récupérer tous les fichiers .java
        final File folder = new File(projectSourcePath);
        ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

        // Parcourir chaque fichier .java
        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry, "UTF-8");

            CompilationUnit parse = parse(content.toCharArray());

            ClassVisitor classVisitor = new ClassVisitor(parse);
            parse.accept(classVisitor);

            classCount += classVisitor.getClassCount();

            LineVisitor lineVisitor = new LineVisitor(parse);
            parse.accept(lineVisitor);
            lineCount += lineVisitor.getLineCount();
        }
    }

    // Fonction pour lister les fichiers .java
    private ArrayList<File> listJavaFilesForFolder(final File folder) {
        ArrayList<File> javaFiles = new ArrayList<File>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                javaFiles.addAll(listJavaFilesForFolder(fileEntry));
            } else if (fileEntry.getName().endsWith(".java")) {
                javaFiles.add(fileEntry);
            }
        }
        return javaFiles;
    }

    // Fonction pour créer l'AST à partir du contenu d'un fichier .java
    private CompilationUnit parse(char[] classSource) {
        ASTParser parser = ASTParser.newParser(AST.JLS4); // Pour Java 1.6+
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        parser.setBindingsRecovery(true);

        Map<?, ?> options = JavaCore.getOptions();
        parser.setCompilerOptions(options);

        parser.setUnitName("");

        String[] sources = { projectSourcePath };

        parser.setSource(classSource);

        return (CompilationUnit) parser.createAST(null);
    }

    // Accesseurs pour les résultats
    public int getClassCount() {
        return classCount;
    }

    public int getLineCount() {
        return lineCount;
    }
}
