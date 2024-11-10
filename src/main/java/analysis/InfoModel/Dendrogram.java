package analysis.InfoModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class Dendrogram {
    private List<ClusteringStep> clusteringSteps;

    public Dendrogram(List<ClusteringStep> steps) {
        this.clusteringSteps = steps;
    }

    // Méthode pour exporter le dendrogramme au format .dot
    public void exportToDot(String filePath, int maxChars) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        writer.write("digraph Dendrogram {\n");
        writer.write("node [shape=box, width=0.5, height=0.3];\n"); // Forme des nœuds
        writer.write("splines=ortho;\n"); // Lignes orthogonales (angles droits)
        writer.write("rankdir=TB;\n"); // Organisation de haut en bas
        writer.write("edge [arrowhead=none];\n"); // Suppression des flèches aux extrémités des arêtes

        AtomicInteger clusterId = new AtomicInteger();
        Map<Cluster, String> clusterLabels = new HashMap<>(); // Pour stocker les noms des clusters
        Map<Cluster, Integer> clusterLevels = new HashMap<>(); // Pour stocker les niveaux des clusters

        // Étape initiale : tous les clusters de cardinalité 1
        ClusteringStep initialStep = clusteringSteps.get(0);
        if (initialStep.isInitialStep()) {
            for (Cluster initialCluster : initialStep.getInitialClusters()) {
                String nodeName = "Cluster" + (clusterId.getAndIncrement());
                clusterLabels.put(initialCluster, nodeName);
                clusterLevels.put(initialCluster, 0); // Niveau 0 pour les clusters initiaux
                writer.write(nodeName + " [label=\"" + formatClusterLabel(initialCluster) + "\"];\n");
            }
        }

        int maxLevel = 0; // Suivre le niveau le plus élevé (plus bas dans l'arbre)

        // Parcourir les étapes de fusion
        for (int i = 1; i < clusteringSteps.size(); i++) {
            ClusteringStep step = clusteringSteps.get(i);

            // Récupérer ou générer les noms des clusters
            String node1 = clusterLabels.get(step.getCluster1());
            String node2 = clusterLabels.get(step.getCluster2());
            String mergedNode = "Cluster" + (clusterId.getAndIncrement());

            // Nœud invisible pour la jonction des enfants
            String invisibleNodeParent = "InvisibleParent" + (clusterId.getAndIncrement()); // Nœud invisible parent
            String invisibleNodeChild1 = "InvisibleChild1" + (clusterId.getAndIncrement()); // Nœud invisible enfant 1
            String invisibleNodeChild2 = "InvisibleChild2" + (clusterId.getAndIncrement()); // Nœud invisible enfant 2

            // Utiliser la méthode toString pour obtenir l'affichage limité des classes
            String mergedLabel = step.toString(maxChars);

            // Ajouter le nœud fusionné avec la chaîne de caractères formatée
            writer.write(mergedNode + " [label=\"" + mergedLabel + "\"];\n");

            // Ajouter les nœuds invisibles
            writer.write(invisibleNodeParent + " [shape=point, width=0, height=0, label=\"\", style=invis];\n");
            writer.write(invisibleNodeChild1 + " [shape=point, width=0, height=0, label=\"\", style=invis];\n");
            writer.write(invisibleNodeChild2 + " [shape=point, width=0, height=0, label=\"\", style=invis];\n");

            // Relier le nœud parent au nœud invisible correspondant (vertical)
            writer.write(mergedNode + " -> " + invisibleNodeParent + " [headport=n, tailport=s];\n");

            // Relier les nœuds invisibles horizontalement
            writer.write(invisibleNodeParent + " -> " + invisibleNodeChild1 + ";\n");
            writer.write(invisibleNodeParent + " -> " + invisibleNodeChild2 + ";\n");

            // Relier chaque nœud enfant à son nœud invisible correspondant (vertical)
            writer.write(invisibleNodeChild1 + " -> " + node1 + " [headport=n, tailport=s];\n");
            writer.write(invisibleNodeChild2 + " -> " + node2 + " [headport=n, tailport=s];\n");

            // Calculer le niveau du cluster fusionné
            int level1 = clusterLevels.get(step.getCluster1());
            int level2 = clusterLevels.get(step.getCluster2());
            int mergedLevel = Math.max(level1, level2) + 1;
            clusterLevels.put(step.getMergedCluster(), mergedLevel);
            clusterLabels.put(step.getMergedCluster(), mergedNode);

            // Mettre à jour le niveau maximum
            maxLevel = Math.max(maxLevel, mergedLevel);
        }

        // Aligner les nœuds invisibles par niveau en utilisant des contraintes de rang
        for (int level = 0; level <= maxLevel; level++) {
            StringBuilder rank = new StringBuilder("{ rank=same; ");
            int count = 0;
            for (Map.Entry<Cluster, Integer> entry : clusterLevels.entrySet()) {
                if (entry.getValue() == level) {
                    rank.append(clusterLabels.get(entry.getKey())).append("; ");
                    count++;
                }
            }
            rank.append("}\n");
            writer.write(rank.toString());
            if (count <= 1) {
                break;
            }
        }

        writer.write("{ rank=min; " + clusterLabels.get(clusteringSteps.get(clusteringSteps.size() - 1).getMergedCluster()) + "; }\n");

        writer.write("}\n"); // Fin du fichier .dot
        writer.close();
    }


    // Formater l'étiquette d'un cluster pour qu'elle affiche la liste des classes
    private String formatClusterLabel(Cluster cluster) {
        return cluster.getClasses().toString().replace("[", "").replace("]", ""); // Supprimer les crochets []
    }
}
