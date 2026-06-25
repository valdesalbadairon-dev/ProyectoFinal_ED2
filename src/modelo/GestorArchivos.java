package modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {
    private static final String ARCHIVO = "almacenes.txt";

    public static void guardarAlmacenes(List<Almacen> almacenes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Almacen a : almacenes) {
                writer.println(a.getId() + "," + a.getNombre() + "," + a.getCiudad() + "," + a.getRegion());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    public static List<Almacen> cargarAlmacenes() {
        List<Almacen> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return lista;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    try {
                        int id = Integer.parseInt(datos[0]);
                        String nombre = datos[1];
                        String ciudad = datos[2];
                        String region = datos[3];
                        lista.add(new Almacen(id, nombre, ciudad, region));
                    } catch (NumberFormatException e) {
                        System.err.println("Error al leer linea: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar: " + e.getMessage());
        }
        return lista;
    }
}