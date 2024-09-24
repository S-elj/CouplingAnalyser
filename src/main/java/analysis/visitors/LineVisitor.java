package analysis.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class LineVisitor extends ASTVisitor {

    private CompilationUnit compilationUnit;
    private int lineCount = 0;

    public LineVisitor(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        int startLine = compilationUnit.getLineNumber(node.getStartPosition());
        int endLine = compilationUnit.getLineNumber(node.getStartPosition() + node.getLength() -1);

        if (startLine != -1 && endLine != -1 && endLine >= startLine) {
            lineCount += (endLine - startLine + 1); // +1 pour inclure la ligne de début
        }
    return super.visit(node);
    }
    public int getLineCount() {
        return lineCount;
    }
}
