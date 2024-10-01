package analysis.visitors;

import analysis.CallGraph;
import analysis.InfoModel.AttributeInfo;
import analysis.InfoModel.ClassInfo;
import analysis.InfoModel.MethodCallInfo;
import analysis.InfoModel.MethodInfo;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class ClassVisitor extends ASTVisitor {
    private CallGraph callGraph;

    public ClassVisitor(CallGraph callGraph) {
        this.callGraph = callGraph;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        String className = node.getName().getFullyQualifiedName();
        for (MethodDeclaration method : node.getMethods()) {
            String methodName = className + "." + method.getName().getFullyQualifiedName();

            // Ajouter le nœud (la méthode) dans le graphe
            callGraph.addNode(methodName);

            // Analyser les appels de méthode à l'intérieur de cette méthode
            MethodCallVisitor methodCallVisitor = new MethodCallVisitor(callGraph, methodName);
            method.accept(methodCallVisitor);
        }
        return super.visit(node);
    }


}