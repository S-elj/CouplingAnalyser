package analysis.visitors;

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
    private CompilationUnit compilationUnit;
    private int classCount = 0; // Compteur pour le nombre de classes
    private int methodCount = 0; // Compteur pour le nombre de méthodes

    // Liste des informations de classes trouvées
    private List<ClassInfo> classesInfo = new ArrayList<>();

    public ClassVisitor(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }


    @Override
    public boolean visit(TypeDeclaration node) {
        // Récupérer le nom de la classe
        String className = node.getName().getFullyQualifiedName();

        // Récupérer les méthodes de la classe
        List<MethodInfo> methods = new ArrayList<>();
        for (MethodDeclaration method : node.getMethods()) {
            String methodName = method.getName().getFullyQualifiedName();

            // Analyser les appels de méthode dans cette méthode
            MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
            method.accept(methodCallVisitor);
            List<MethodCallInfo> methodCalls = methodCallVisitor.getMethodCalls();

            methods.add(new MethodInfo(methodName, methodCalls));
            methodCount++;
        }

        // Créer l'objet ClassInfo pour stocker les informations de la classe
        ClassInfo classInfo = new ClassInfo(className, methods);
        classesInfo.add(classInfo);

        classCount++;
        return super.visit(node);
    }

    // Méthode pour déterminer l'encapsulation d'un attribut
    private String getEncapsulation(FieldDeclaration field) {
        for (Object modifierObj : field.modifiers()) {
            if (modifierObj instanceof Modifier) {
                Modifier modifier = (Modifier) modifierObj;
                if (modifier.isPrivate()) {
                    return "private";
                } else if (modifier.isProtected()) {
                    return "protected";
                } else if (modifier.isPublic()) {
                    return "public";
                }
            }
        }
        return "default"; // Package-private si aucun modificateur
    }

    // Méthode pour récupérer le nombre de classes trouvées
    public int getClassCount() {
        return classCount;
    }

    // Méthode pour récupérer le nombre de méthodes trouvées
    public int getMethodCount() {
        return methodCount;
    }

    // Méthode pour récupérer la liste des informations de classes trouvées
    public List<ClassInfo> getClassesInfo() {
        return classesInfo;
    }
}
