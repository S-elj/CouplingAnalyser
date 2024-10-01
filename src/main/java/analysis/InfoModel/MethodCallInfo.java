package analysis.InfoModel;

public class MethodCallInfo {
    private String calledMethodName;
    private String receiverType;

    public MethodCallInfo(String calledMethodName, String receiverType) {
        this.calledMethodName = calledMethodName;
        this.receiverType = receiverType;
    }

    public String getCalledMethodName() {
        return calledMethodName;
    }

    public String getReceiverType() {
        return receiverType;
    }

    @Override
    public String toString() {
        return "Called method: " + calledMethodName + " (Receiver type: " + receiverType + ")";
    }
}