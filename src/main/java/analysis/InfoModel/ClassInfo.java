package analysis.InfoModel;

import java.util.List;

public class ClassInfo {
    private String className;
    private List<MethodInfo> methods;

    public ClassInfo(String className, List<MethodInfo> methods) {
        this.className = className;
        this.methods = methods;
    }

    public String getClassName() {
        return className;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        return "Class: " + className + ", Methods: " + methods;
    }
}
