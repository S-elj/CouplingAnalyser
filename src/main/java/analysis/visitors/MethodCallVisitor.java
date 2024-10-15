package analysis.visitors;

import analysis.CallGraph;
import org.eclipse.jdt.core.dom.*;
public class MethodCallVisitor extends ASTVisitor {
    private CallGraph callGraph;
    private String currentMethod;

    public MethodCallVisitor(CallGraph callGraph, String currentMethod) {
        this.callGraph = callGraph;
        this.currentMethod = currentMethod;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        // Récupérer le nom de la méthode appelée
        String calledMethodName = node.getName().getFullyQualifiedName();

        // Essayer de résoudre le type du receveur
        String receiverType = "Unknown";
        Expression expression = node.getExpression();  // Expression sur laquelle la méthode est appelée

        if (expression != null) {
            // Si l'expression n'est pas nulle, essayons de résoudre son type
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                receiverType = typeBinding.getQualifiedName();  // Récupérer le nom qualifié du type (incluant le package)
            }
        } else {
            // Si l'expression est nulle, c'est probablement un appel sur "this" ou "super"
            ITypeBinding declaringClass = getDeclaringClass(node);
            if (declaringClass != null) {
                receiverType = declaringClass.getQualifiedName();  // Type de la classe englobante
            }
        }

        // Ajouter l'arc dans le graphe d'appel
        callGraph.addEdge(currentMethod, receiverType + "." + calledMethodName);

        return super.visit(node);
    }

    // Méthode pour obtenir la classe qui contient la méthode actuelle
    private ITypeBinding getDeclaringClass(ASTNode node) {
        MethodDeclaration methodDeclaration = getEnclosingMethod(node);
        if (methodDeclaration != null) {
            IMethodBinding methodBinding = methodDeclaration.resolveBinding();
            if (methodBinding != null) {
                return methodBinding.getDeclaringClass();  // Récupérer la classe contenant la méthode
            }
        }
        return null;
    }

    // Méthode utilitaire pour récupérer la méthode englobante d'un nœud
    private MethodDeclaration getEnclosingMethod(ASTNode node) {
        while (node != null) {
            if (node instanceof MethodDeclaration) {
                return (MethodDeclaration) node;
            }
            node = node.getParent();
        }
        return null;
    }
}
