package analysis.InfoModel;

public class ClusterPair {
    private Cluster cluster1;
    private Cluster cluster2;
    private double coupling; // Distance ou couplage entre les deux clusters

    public ClusterPair(Cluster cluster1, Cluster cluster2, double coupling) {
        this.cluster1 = cluster1;
        this.cluster2 = cluster2;
        this.coupling = coupling;
    }

    public ClusterPair(double coupling) {
        this.coupling = coupling;
    }

    public Cluster getCluster1() {
        return cluster1;
    }

    public Cluster getCluster2() {
        return cluster2;
    }

    public double getCoupling() {
        return coupling;
    }

    @Override
    public String toString() {
        return "Pair: " + cluster1.getClasses() + " <-> " + cluster2.getClasses() + " (Coupling: " + coupling + ")";
    }
}
