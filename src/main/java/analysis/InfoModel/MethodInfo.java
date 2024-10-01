package analysis.InfoModel;

import java.util.List;

public class MethodInfo {
    private String methodName;
    private List<MethodCallInfo> methodCalls;

    public MethodInfo(String methodName, List<MethodCallInfo> methodCalls) {
        this.methodName = methodName;
        this.methodCalls = methodCalls;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<MethodCallInfo> getMethodCalls() {
        return methodCalls;
    }

    @Override
    public String toString() {
        return "Method: " + methodName + ", Calls: " + methodCalls;
    }
}