package grafo;

import java.util.ArrayList;
import java.util.List;

public class Grafo {
    private int numVertices;
    private double[][] matrizAdyacencia;
    private List<String> nombresVertices;

    public Grafo(int numVertices) {
        this.numVertices = numVertices;
        this.matrizAdyacencia = new double[numVertices][numVertices];
        this.nombresVertices = new ArrayList<>();

        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                matrizAdyacencia[i][j] = (i == j) ? 0 : Double.POSITIVE_INFINITY;
            }
            nombresVertices.add("V" + i);
        }
    }

    public void setNombreVertice(int indice, String nombre) {
        if (indice >= 0 && indice < numVertices) {
            nombresVertices.set(indice, nombre);
        }
    }

    public String getNombreVertice(int indice) {
        return (indice >= 0 && indice < numVertices) ? nombresVertices.get(indice) : null;
    }

    public void agregarArista(int origen, int destino, double peso) {
        if (origen >= 0 && origen < numVertices && destino >= 0 && destino < numVertices) {
            matrizAdyacencia[origen][destino] = peso;
            matrizAdyacencia[destino][origen] = peso;
        }
    }

    public double getPeso(int origen, int destino) {
        return matrizAdyacencia[origen][destino];
    }

    public List<Integer> getAdyacentes(int vertice) {
        List<Integer> adyacentes = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            if (matrizAdyacencia[vertice][i] != Double.POSITIVE_INFINITY && vertice != i) {
                adyacentes.add(i);
            }
        }
        return adyacentes;
    }

    public int getNumVertices() { return numVertices; }
    public double[][] getMatrizAdyacencia() { return matrizAdyacencia; }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MATRIZ DE ADYACENCIA\n");
        sb.append("      ");
        for (int i = 0; i < numVertices; i++) {
            sb.append(String.format("%-12s", nombresVertices.get(i)));
        }
        sb.append("\n");
        for (int i = 0; i < numVertices; i++) {
            sb.append(String.format("%-10s", nombresVertices.get(i)));
            for (int j = 0; j < numVertices; j++) {
                String valor = (matrizAdyacencia[i][j] == Double.POSITIVE_INFINITY) ? "∞" : String.format("%.0f", matrizAdyacencia[i][j]);
                sb.append(String.format("%-12s", valor));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}