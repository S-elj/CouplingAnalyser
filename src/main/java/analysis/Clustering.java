package analysis;

import analysis.InfoModel.Cluster;
import analysis.InfoModel.ClusterPair;
import analysis.InfoModel.ClusteringStep;
import analysis.InfoModel.Dendrogram;
import analysis.graph.CouplingGraph;

import java.util.*;

public class Clustering {
    private CouplingGraph couplingGraph; // Graph qui contient les informations de couplage
    private List<ClusteringStep> steps; // Stocke les étapes du clustering
    private Map<String, Double> couplingMap;

    public Clustering(CouplingGraph couplingGraph) {
        this.couplingGraph = couplingGraph;
        this.couplingMap = couplingGraph.getWeightedEdges(); // Map des poids de couplage
        this.steps = new ArrayList<>();
    }

    //System.out.println("Etape: " + count + " nb clusters: " + clusters.size());
    //System.out.println("Debut du clusturing, nb classes: " + clusters.size());

    // Exécute l'algorithme de clustering hiérarchique
    public Dendrogram performClustering() {
        Set<Cluster> clusters = getClassesAsCluster();
        steps.add(new ClusteringStep(new ArrayList<>(clusters))); // Étape initiale avec tous les clusters de cardinalité 1

        int count = 0;

        // Tant qu'il reste plus d'un cluster, continuez à fusionner les plus proches
        while (clusters.size() > 1) {
            System.out.println("Étape: " + count + " nb clusters: " + clusters.size());
            // Trouver les deux clusters les plus proches
            ClusterPair closestPair = findClosestClusters(clusters);
            if (closestPair == null) break;
            mergeAndUpdate(closestPair, clusters);
            System.out.println("Fusion: " + closestPair);

            count++;
        }
        // Création du dendrogramme avec les étapes enregistrées
        return new Dendrogram(steps);
    }

    public Dendrogram performClusturingWithMinimalCoupling(double seuil) {
        Set<Cluster> clusters = getClassesAsCluster();
        steps.add(new ClusteringStep(new ArrayList<>(clusters))); // Étape initiale avec tous les clusters de cardinalité 1

        int count = 0;

        // Tant qu'il reste plus d'un cluster, continuez à fusionner les plus proches
        while (clusters.size() > 1 || clusters.size() > (couplingGraph.getClasses().size()) / 2) {
            System.out.println("Étape: " + count + " nb clusters: " + clusters.size());
            // Trouver les deux clusters les plus proches
            ClusterPair closestPair = findClustersWithMinimalCoupling(clusters, seuil);
            if (closestPair == null || closestPair.getCoupling() == -5) break;
            mergeAndUpdate(closestPair, clusters);
            System.out.println("Fusion: " + closestPair);

            count++;
        }
        // Création du dendrogramme avec les étapes enregistrées
        return new Dendrogram(steps);
    }

    private Set<Cluster> getClassesAsCluster() {
        Set<Cluster> clusters = new HashSet<>();
        for (String clazz : couplingGraph.getClasses()) {
            clusters.add(new Cluster(clazz));
        }
        return clusters;
    }

    private void mergeAndUpdate(ClusterPair cp, Set<Cluster> clusterlist) {
        Cluster c1 = cp.getCluster1();
        Cluster c2 = cp.getCluster2();
        // Fusionner les clusters trouvés
        Cluster mergedCluster = new Cluster(c1, c2, cp.getCoupling());
        clusterlist.remove(cp.getCluster1());
        clusterlist.remove(cp.getCluster2());
        clusterlist.add(mergedCluster);
        steps.add(new ClusteringStep(cp.getCluster1(), cp.getCluster2(), mergedCluster, mergedCluster.getCoupling()));

    }

    private ClusterPair findClosestClusters(Set<Cluster> clusters) {
        ClusterPair closestPair = null;
        double maxCoupling = -1; // On initialise à -1 car on cherche le couplage maximum

        // Nous utilisons deux boucles imbriquées pour parcourir chaque paire de clusters
        for (Cluster c1 : clusters) {
            for (Cluster c2 : clusters) {
                if (!c1.equals(c2)) {  // S'assurer qu'on ne compare pas un cluster avec lui-même
                    double coupling = computeCouplingBetweenClusters(c1, c2);
                    if (coupling > maxCoupling) {
                        maxCoupling = coupling;
                        closestPair = new ClusterPair(c1, c2, coupling);
                    }
                }
            }
        }

        return closestPair;
    }

    private ClusterPair findClustersWithMinimalCoupling(Set<Cluster> clusters, double seuil) {
        ClusterPair closestPair = null;
        boolean satisfied = false;
        double maxCoupling = -1; // On initialise à -1 car on cherche le couplage maximum

        // Nous utilisons deux boucles imbriquées pour parcourir chaque paire de clusters
        for (Cluster c1 : clusters) {
            for (Cluster c2 : clusters) {
                if (!c1.equals(c2)) {  // S'assurer qu'on ne compare pas un cluster avec lui-même
                    double coupling = computeCouplingBetweenClusters(c1, c2);
                    if (coupling > maxCoupling) {
                        maxCoupling = coupling;
                        if (coupling >= seuil) {
                            satisfied = true;
                            closestPair = new ClusterPair(c1, c2, coupling);
                        }
                    }
                }
            }
        }

        if (satisfied) {
            return closestPair;
        } else {
            System.out.println("Aucun coulage possible en conservant le seuil minimum : " + seuil + ",   couplage le plus haut toruvé : " + maxCoupling + "\n");
            return new ClusterPair(-5);
        }

    }


    // Calculer le couplage moyen entre deux clusters (méthode "average linkage")
    private double computeCouplingBetweenClusters(Cluster cluster1, Cluster cluster2) {
        double totalCoupling = 0.0;
        int count = 0;

        for (String classA : cluster1.getClasses()) {
            for (String classB : cluster2.getClasses()) {
                String pair = classA.compareTo(classB) < 0 ? classA + "->" + classB : classB + "->" + classA;
                if (couplingMap.containsKey(pair)) {
                    totalCoupling += couplingMap.get(pair);
                    count++;
                }
            }
        }


        return Math.round(((double) totalCoupling / count) * 1000.0) / 1000.0;
        //return count == 0 ? 0 : totalCoupling / count; // Moyenne du couplage
    }
}