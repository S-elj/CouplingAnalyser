package analysis.InfoModel;

import java.util.HashSet;
import java.util.Set;


public class Cluster {
    private Set<String> classes;
    private double coupling;

    public Cluster(String className) {
        this.classes = new HashSet<>();
        this.classes.add(className);
        this.coupling = 0.0;
    }

    public Cluster(Cluster left, Cluster right) {
        this.classes = new HashSet<>(left.getClasses());
        this.classes.addAll(right.getClasses());
    }

    public Cluster(Cluster left, Cluster right, double coupling) {
        this.classes = new HashSet<>(left.getClasses());
        this.classes.addAll(right.getClasses());
        this.coupling = coupling;
    }

    public Set<String> getClasses() {
        return this.classes;
    }

    public double getCoupling() {
        return coupling;
    }

    public void setCoupling(double coupling) {
        this.coupling = coupling;
    }

    @Override
    public String toString() {
        return "Cluster: " + this.classes;
    }
}
