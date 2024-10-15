package toAnalyse;

public class ClassB {

    private ClassA classA;

    public ClassB(ClassA classA) {
        this.classA = classA;
    }

    public void methodB1() {
        System.out.println("ClassB: methodB1");
        classA.methodA1(); // Appel à une méthode de ClassA
    }

    public void methodB2() {
        System.out.println("ClassB: methodB2");
        classA.methodA2(); // Appel à une autre méthode de ClassA
    }

    public void methodB3() {
        System.out.println("ClassB: methodB3");
        classA.methodA3(); // Appel répétitif de methodA3
    }
}
