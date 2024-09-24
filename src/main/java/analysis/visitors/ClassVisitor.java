package analysis.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClassVisitor extends ASTVisitor {
    private CompilationUnit compilationUnit;
    private int classCount = 0; // Compteur pour le nombre de classes
    private  int methodCount = 0;

    public ClassVisitor(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        // Récupérer le nom de la classe
        String className = node.getName().getFullyQualifiedName();

        // Récupérer les positions de début et de fin de la classe
        int startLine = compilationUnit.getLineNumber(node.getStartPosition());
        int endLine = compilationUnit.getLineNumber(node.getStartPosition() + node.getLength() -1 );


        if (startLine != -1 && endLine != -1 && endLine >= startLine) {
            int classLineCount = endLine - startLine + 1;
            System.out.println("Classe: " + className + " | Nombre de lignes de code: " + classLineCount);
        } else {
            System.out.println("Classe: " + className + " | Erreur de calcul des lignes (positions invalides)");
        }

        classCount++;

        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {

        methodCount++;
        return super.visit(node);
    }

    // Méthode pour récupérer le nombre de classes trouvées
    public int getClassCount() {
        return classCount;
    }
    public int getMethodCount() {return methodCount;}

}