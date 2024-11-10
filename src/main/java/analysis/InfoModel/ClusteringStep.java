package analysis.InfoModel;

import java.util.List;
import java.util.Set;

public class ClusteringStep {
    private Cluster cluster1;
    private Cluster cluster2;
    private Cluster mergedCluster;
    private double couplingScore;
    private List<Cluster> initialClusters; // Pour stocker l'étape initiale si nécessaire

    // Constructeur pour une étape de fusion entre deux clusters
    public ClusteringStep(Cluster cluster1, Cluster cluster2, Cluster mergedCluster, double couplingScore) {
        this.cluster1 = cluster1;
        this.cluster2 = cluster2;
        this.mergedCluster = mergedCluster;
        this.couplingScore = couplingScore;
        this.initialClusters = null; // Non utilisé pour les étapes de fusion
    }

    // Constructeur pour l'étape initiale (cluster de cardinalité 1)
    public ClusteringStep(List<Cluster> initialClusters) {
        this.initialClusters = initialClusters;
        this.cluster1 = null;
        this.cluster2 = null;
        this.mergedCluster = null;
        this.couplingScore = 0.0; // Pas de score pour l'étape initiale
    }

    // Méthodes getters et autres pour accéder aux clusters
    public Cluster getCluster1() { return cluster1; }
    public Cluster getCluster2() { return cluster2; }
    public Cluster getMergedCluster() { return mergedCluster; }
    public double getCouplingScore() { return couplingScore; }
    public List<Cluster> getInitialClusters() { return initialClusters; }

    public boolean isInitialStep() {
        return initialClusters != null;
    }
    public String toString(int maxChars) {
        if (mergedCluster == null) return ""; // Si le cluster est nul

        String clusterLabel = mergedCluster.getClasses().toString().replace("[", "").replace("]", "");

        if (clusterLabel.length() > maxChars) {
            clusterLabel = clusterLabel.substring(0, maxChars) + "...";
        }
        // Ajouter la mesure de couplage à la fin
        return clusterLabel + " : " + couplingScore ;
    }


}
