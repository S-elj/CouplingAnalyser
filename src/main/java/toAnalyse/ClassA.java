package toAnalyse;


public class ClassA {

    private ClassB classB;

    public ClassA(ClassB classB) {
        this.classB = classB;
    }

    public void methodA1() {
        System.out.println("ClassA: methodA1");
        classB.methodB1(); // Appel à une méthode de ClassB
    }

    public void methodA2() {
        System.out.println("ClassA: methodA2");
        classB.methodB2(); // Appel à une autre méthode de ClassB
    }

    public void methodA3() {
        System.out.println("ClassA: methodA3");
        classB.methodB1(); // Appel répétitif de methodB1
        classB.methodB3(); // Appel à une méthode supplémentaire de ClassB
    }
}

