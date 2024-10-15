package analysis.visitors;

import analysis.CallGraph;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
public class GraphVisitor extends ASTVisitor {
    private CallGraph callGraph;

    public GraphVisitor(CallGraph callGraph) {
        this.callGraph = callGraph;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        // Récupérer le nom de la classe avec son package
        String className = getQualifiedClassName(node);

        for (MethodDeclaration method : node.getMethods()) {
            // Construire le nom complet de la méthode avec la classe et le package
            String methodName = className + "." + method.getName().getFullyQualifiedName();

            // Ajouter le nœud (la méthode) dans le graphe
            callGraph.addNode(methodName);

            // Analyser les appels de méthode à l'intérieur de cette méthode
            MethodCallVisitor methodCallVisitor = new MethodCallVisitor(callGraph, methodName);
            method.accept(methodCallVisitor);
        }
        return super.visit(node);
    }

    // Méthode utilitaire pour récupérer le nom qualifié complet d'une classe (avec package)
    private String getQualifiedClassName(TypeDeclaration node) {
        ITypeBinding typeBinding = node.resolveBinding();
        if (typeBinding != null) {
            return typeBinding.getQualifiedName();  // Nom complet de la classe avec package
        } else {
            return node.getName().getFullyQualifiedName();  // Si on ne peut pas résoudre le type, utiliser le nom simple
        }
    }
}
