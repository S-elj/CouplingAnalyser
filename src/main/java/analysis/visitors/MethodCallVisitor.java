package analysis.visitors;

import analysis.InfoModel.MethodCallInfo;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class MethodCallVisitor extends ASTVisitor {
    private List<MethodCallInfo> methodCalls = new ArrayList<>();

    @Override
    public boolean visit(MethodInvocation node) {
        // Récupérer le nom de la méthode appelée
        String calledMethodName = node.getName().getFullyQualifiedName();

        // Récupérer le type statique de l'objet receveur si disponible
        Expression expression = node.getExpression();
        String receiverType = "Unknown"; // Valeur par défaut si on ne peut pas déterminer le type

        // Si l'expression est non nulle, on essaie de résoudre son type
        if (expression != null) {
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                receiverType = typeBinding.getQualifiedName(); // Récupère le nom qualifié complet du type
            }
        } else {
            // Si l'expression est nulle, on vérifie si c'est un appel implicite à "this" ou "super"
            MethodDeclaration methodDeclaration = getEnclosingMethod(node);
            if (methodDeclaration != null) {
                ITypeBinding declaringClass = methodDeclaration.resolveBinding().getDeclaringClass();
                if (declaringClass != null) {
                    receiverType = declaringClass.getQualifiedName(); // Type de la classe contenant la méthode
                }
            }
        }

        // Ajouter l'appel de méthode à la liste
        methodCalls.add(new MethodCallInfo(calledMethodName, receiverType));

        return super.visit(node);
    }

    // Méthode utilitaire pour récupérer la méthode englobante d'un noeud
    private MethodDeclaration getEnclosingMethod(ASTNode node) {
        while (node != null) {
            if (node instanceof MethodDeclaration) {
                return (MethodDeclaration) node;
            }
            node = node.getParent();
        }
        return null;
    }

    // Récupérer la liste des méthodes appelées
    public List<MethodCallInfo> getMethodCalls() {
        return methodCalls;
    }
}
