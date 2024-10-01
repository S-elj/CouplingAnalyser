package analysis;

import analysis.CallGraph;
import analysis.InfoModel.ClassInfo;
import analysis.visitors.ClassVisitor;
import analysis.visitors.LineVisitor;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeAnalyzer {

    private String projectSourcePath;

    // Compteurs
    private int classCount = 0;
    private int lineCount = 0;
    private int methodCount = 0;

    // Liste des informations de classes
    private List<ClassInfo> classesInfo = new ArrayList<>();

    public CodeAnalyzer(String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
    }

    public void analyze(CallGraph callGraph) throws IOException {
        // Récupérer tous les fichiers .java
        final File folder = new File(projectSourcePath);
        ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

        // Parcourir chaque fichier .java
        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry, "UTF-8");

            // Créer l'AST pour le fichier courant
            CompilationUnit parse = parse(content.toCharArray());

            // Analyse des classes et ajout des informations d'appel au graphe
            ClassVisitor classVisitor = new ClassVisitor(callGraph);  // Passer le CallGraph
            parse.accept(classVisitor);


            // Analyse des lignes de code
            LineVisitor lineVisitor = new LineVisitor(parse);
            parse.accept(lineVisitor);
            lineCount += lineVisitor.getLineCount();
        }
    }

    // Méthode pour lister les fichiers .java
    private ArrayList<File> listJavaFilesForFolder(final File folder) {
        ArrayList<File> javaFiles = new ArrayList<>();
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
        ASTParser parser = ASTParser.newParser(AST.JLS4); // Utiliser JLS4 pour la compatibilité avec Java 1.6+
        parser.setResolveBindings(true); // Activer la résolution des bindings
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);

        // Configurer les options du compilateur
        Map<?, ?> options = JavaCore.getOptions();
        parser.setCompilerOptions(options);

        // Configurer l'environnement pour la résolution des types
        String[] classpathEntries = {"/path/to/jdk/lib/rt.jar", "/path/to/project/lib/some-library.jar"};
        String[] sourceFolders = {projectSourcePath}; // Dossier source du projet
        parser.setEnvironment(classpathEntries, sourceFolders, null, true);

        parser.setUnitName(""); // Nécessaire pour la résolution des types
        parser.setSource(classSource);

        // Créer l'AST
        return (CompilationUnit) parser.createAST(null);
    }

    // Accesseurs pour les résultats
    public int getClassCount() {
        return classCount;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getMethodCount() {
        return methodCount;
    }

    public List<ClassInfo> getClassesInfo() {
        return classesInfo;
    }
}
