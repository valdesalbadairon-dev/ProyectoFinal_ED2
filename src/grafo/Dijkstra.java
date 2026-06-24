package grafo;

import java.util.*;

public class Dijkstra {
    private Grafo grafo;

    public Dijkstra(Grafo grafo) {
        this.grafo = grafo;
    }

    public Resultado encontrarCaminoMasCorto(int origen, int destino) {
        int n = grafo.getNumVertices();
        double[] dist = new double[n];
        int[] prev = new int[n];
        boolean[] visitado = new boolean[n];

        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        dist[origen] = 0;

        for (int i = 0; i < n; i++) {
            int u = seleccionarMinimoNoVisitado(dist, visitado);
            if (u == -1) break;
            visitado[u] = true;

            for (int v : grafo.getAdyacentes(u)) {
                if (!visitado[v]) {
                    double nuevaDist = dist[u] + grafo.getPeso(u, v);
                    if (nuevaDist < dist[v]) {
                        dist[v] = nuevaDist;
                        prev[v] = u;
                    }
                }
            }
        }

        return new Resultado(dist, prev, origen, destino);
    }

    private int seleccionarMinimoNoVisitado(double[] dist, boolean[] visitado) {
        double min = Double.POSITIVE_INFINITY;
        int minIdx = -1;
        for (int i = 0; i < dist.length; i++) {
            if (!visitado[i] && dist[i] < min) {
                min = dist[i];
                minIdx = i;
            }
        }
        return minIdx;
    }

    public class Resultado {
        private double[] distancias;
        private int[] predecesores;
        private int origen;
        private int destino;

        public Resultado(double[] distancias, int[] predecesores, int origen, int destino) {
            this.distancias = distancias;
            this.predecesores = predecesores;
            this.origen = origen;
            this.destino = destino;
        }

        public double getDistancia() { return distancias[destino]; }

        public List<Integer> getCamino() {
            List<Integer> camino = new ArrayList<>();
            int actual = destino;
            while (actual != -1) {
                camino.add(0, actual);
                actual = predecesores[actual];
            }
            return camino;
        }

        public String getCaminoConNombres() {
            List<Integer> camino = getCamino();
            if (camino.size() <= 1) {
                return "No hay camino disponible";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < camino.size(); i++) {
                sb.append(grafo.getNombreVertice(camino.get(i)));
                if (i < camino.size() - 1) {
                    sb.append(" → ");
                }
            }
            return sb.toString();
        }

        public boolean hayCamino() {
            return distancias[destino] != Double.POSITIVE_INFINITY;
        }
    }
}
