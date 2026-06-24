package arbol;

import java.util.ArrayList;
import java.util.List;

public class ArbolBinarioBusqueda<T extends Comparable<T>> {
    private NodoABB<T> raiz;

    public ArbolBinarioBusqueda() {
        this.raiz = null;
    }

    public void insertar(T dato) {
        if (dato == null) return;
        raiz = insertarRecursivo(raiz, dato);
    }

    private NodoABB<T> insertarRecursivo(NodoABB<T> nodo, T dato) {
        if (nodo == null) {
            return new NodoABB<>(dato);
        }
        int comparacion = dato.compareTo(nodo.getDato());
        if (comparacion < 0) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), dato));
        } else if (comparacion > 0) {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), dato));
        }
        return nodo;
    }

    public T buscar(T dato) {
        if (dato == null) return null;
        NodoABB<T> resultado = buscarRecursivo(raiz, dato);
        return resultado != null ? resultado.getDato() : null;
    }

    private NodoABB<T> buscarRecursivo(NodoABB<T> nodo, T dato) {
        if (nodo == null) return null;
        int comparacion = dato.compareTo(nodo.getDato());
        if (comparacion == 0) return nodo;
        if (comparacion < 0) {
            return buscarRecursivo(nodo.getIzquierdo(), dato);
        } else {
            return buscarRecursivo(nodo.getDerecho(), dato);
        }
    }

    public void eliminar(T dato) {
        if (dato == null) return;
        raiz = eliminarRecursivo(raiz, dato);
    }

    private NodoABB<T> eliminarRecursivo(NodoABB<T> nodo, T dato) {
        if (nodo == null) return null;

        int comparacion = dato.compareTo(nodo.getDato());
        if (comparacion < 0) {
            nodo.setIzquierdo(eliminarRecursivo(nodo.getIzquierdo(), dato));
        } else if (comparacion > 0) {
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), dato));
        } else {
            if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
                return null;
            }
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            T sucesor = obtenerMinimo(nodo.getDerecho());
            nodo.setDato(sucesor);
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), sucesor));
        }
        return nodo;
    }

    private T obtenerMinimo(NodoABB<T> nodo) {
        while (nodo.getIzquierdo() != null) {
            nodo = nodo.getIzquierdo();
        }
        return nodo.getDato();
    }

    public void modificar(T viejo, T nuevo) {
        if (viejo == null || nuevo == null) return;
        eliminar(viejo);
        insertar(nuevo);
    }

    public List<T> recorridoInorden() {
        List<T> lista = new ArrayList<>();
        recorridoInordenRecursivo(raiz, lista);
        return lista;
    }

    private void recorridoInordenRecursivo(NodoABB<T> nodo, List<T> lista) {
        if (nodo != null) {
            recorridoInordenRecursivo(nodo.getIzquierdo(), lista);
            lista.add(nodo.getDato());
            recorridoInordenRecursivo(nodo.getDerecho(), lista);
        }
    }

    public List<T> obtenerTodos() {
        return recorridoInorden();
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}