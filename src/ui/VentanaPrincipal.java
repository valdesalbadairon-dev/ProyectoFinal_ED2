package ui;

import arbol.ArbolBinarioBusqueda;
import grafo.Dijkstra;
import grafo.Grafo;
import modelo.Almacen;
import modelo.GestorArchivos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VentanaPrincipal extends JFrame {
    private ArbolBinarioBusqueda<Almacen> arbolAlmacenes;
    private Grafo grafo;
    private Dijkstra dijkstra;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboOrigen;
    private JComboBox<String> comboDestino;
    private JTextArea areaResultados;
    private Random random;

    private static final Map<String, Integer> DISTANCIAS_REGIONALES = new HashMap<>();
    static {
        DISTANCIAS_REGIONALES.put("Occidente", 1);
        DISTANCIAS_REGIONALES.put("Centro", 2);
        DISTANCIAS_REGIONALES.put("Oriente", 3);
    }

    public VentanaPrincipal() {
        setTitle("Sistema de Logistica y Rutas de Distribucion");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        random = new Random();
        arbolAlmacenes = new ArbolBinarioBusqueda<>();
        grafo = new Grafo(0);
        dijkstra = new Dijkstra(grafo);

        cargarAlmacenesGuardados();

        crearPanelSuperior();
        crearPanelCentral();
        crearPanelInferior();

        actualizarTabla();
        actualizarCombos();
    }

    private void cargarAlmacenesGuardados() {
        List<Almacen> almacenes = GestorArchivos.cargarAlmacenes();
        for (Almacen a : almacenes) {
            arbolAlmacenes.insertar(a);
        }
        reconstruirGrafo();
        if (!almacenes.isEmpty()) {
            areaResultados.append("Cargados " + almacenes.size() + " almacenes guardados.\n");
        }
    }

    private void guardarAlmacenes() {
        List<Almacen> almacenes = arbolAlmacenes.obtenerTodos();
        GestorArchivos.guardarAlmacenes(almacenes);
    }

    private boolean soloLetras(String texto) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }
        return texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    }

    private boolean idExiste(int id) {
        return arbolAlmacenes.buscar(new Almacen(id, "", "", "")) != null;
    }

    private double generarDistancia(String region1, String region2) {
        int base = 50 + random.nextInt(200);
        int factor1 = DISTANCIAS_REGIONALES.getOrDefault(region1.trim(), 2);
        int factor2 = DISTANCIAS_REGIONALES.getOrDefault(region2.trim(), 2);
        int distanciaBase = Math.abs(factor1 - factor2) * 150;
        return Math.round((base + distanciaBase + random.nextInt(100)) * 10.0) / 10.0;
    }

    private void construirGrafoDesdeArbol() {
        List<Almacen> almacenes = arbolAlmacenes.obtenerTodos();
        int n = almacenes.size();

        if (n == 0) {
            grafo = new Grafo(0);
            dijkstra = new Dijkstra(grafo);
            return;
        }

        grafo = new Grafo(n);
        for (int i = 0; i < n; i++) {
            grafo.setNombreVertice(i, almacenes.get(i).getNombre());
        }

        if (n >= 2) {
            int[] idx = new int[1000000];
            for (int i = 0; i < n; i++) {
                idx[almacenes.get(i).getId()] = i;
            }

            int conexionesAgregadas = 0;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double peso = generarDistancia(
                        almacenes.get(i).getRegion(),
                        almacenes.get(j).getRegion()
                    );
                    grafo.agregarArista(i, j, peso);
                    conexionesAgregadas++;
                }
            }
            areaResultados.append("Conexiones generadas: " + conexionesAgregadas + "\n");
        }

        dijkstra = new Dijkstra(grafo);
    }

    private void crearPanelSuperior() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Gestion de Almacenes"));

        JButton btnInsertar = new JButton("Insertar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        btnInsertar.addActionListener(e -> insertarAlmacen());
        btnBuscar.addActionListener(e -> buscarAlmacen());
        btnModificar.addActionListener(e -> modificarAlmacen());
        btnEliminar.addActionListener(e -> eliminarAlmacen());

        panel.add(btnInsertar);
        panel.add(btnBuscar);
        panel.add(btnModificar);
        panel.add(btnEliminar);

        add(panel, BorderLayout.NORTH);
    }

    private void crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Almacenes Registrados"));

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Ciudad", "Region"}, 0);
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        panel.add(scroll, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
    }

    private void crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Rutas y Distribucion"));

        JPanel panelRutas = new JPanel(new GridLayout(2, 2, 10, 10));
        panelRutas.setBorder(BorderFactory.createTitledBorder("Calcular Ruta Mas Corta"));

        comboOrigen = new JComboBox<>();
        comboDestino = new JComboBox<>();

        comboOrigen.addActionListener(e -> verificarCombos());
        comboDestino.addActionListener(e -> verificarCombos());

        JButton btnCalcular = new JButton("Calcular Ruta");
        JButton btnVerGrafo = new JButton("Ver Matriz");
        JButton btnRegenerar = new JButton("Regenerar Conexiones");

        panelRutas.add(new JLabel("Origen:"));
        panelRutas.add(comboOrigen);
        panelRutas.add(new JLabel("Destino:"));
        panelRutas.add(comboDestino);

        btnCalcular.addActionListener(e -> calcularRuta());
        btnVerGrafo.addActionListener(e -> mostrarGrafo());
        btnRegenerar.addActionListener(e -> regenerarConexiones());

        areaResultados = new JTextArea(12, 50);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollResultados = new JScrollPane(areaResultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnCalcular);
        panelBotones.add(btnRegenerar);
        panelBotones.add(btnVerGrafo);

        panel.add(panelRutas, BorderLayout.NORTH);
        panel.add(scrollResultados, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        add(panel, BorderLayout.SOUTH);
    }

    private void verificarCombos() {
        String origen = (String) comboOrigen.getSelectedItem();
        String destino = (String) comboDestino.getSelectedItem();

        if (origen != null && destino != null && origen.equals(destino)) {
            JOptionPane.showMessageDialog(this, "El origen y el destino no pueden ser el mismo almacen.");
            comboDestino.setSelectedIndex(-1);
        }
    }

    private void regenerarConexiones() {
        reconstruirGrafo();
        areaResultados.append("Conexiones regeneradas automaticamente.\n");
    }

    private void insertarAlmacen() {
        try {
            String idStr = JOptionPane.showInputDialog("ID del almacen:");
            if (idStr == null) return;

            int id = Integer.parseInt(idStr);

            if (String.valueOf(id).length() < 5) {
                JOptionPane.showMessageDialog(this, "Error: El ID debe tener al menos 5 digitos.");
                return;
            }

            if (idExiste(id)) {
                JOptionPane.showMessageDialog(this, "Error: Ya existe un almacen con el ID " + id + ".");
                return;
            }

            String nombre = JOptionPane.showInputDialog("Nombre:");
            if (nombre == null) return;
            if (!soloLetras(nombre)) {
                JOptionPane.showMessageDialog(this, "Error: El nombre solo debe contener letras y espacios.");
                return;
            }

            String ciudad = JOptionPane.showInputDialog("Ciudad:");
            if (ciudad == null) return;
            if (!soloLetras(ciudad)) {
                JOptionPane.showMessageDialog(this, "Error: La ciudad solo debe contener letras y espacios.");
                return;
            }

            String region = JOptionPane.showInputDialog("Region (Occidente, Centro, Oriente):");
            if (region == null) return;
            if (!soloLetras(region)) {
                JOptionPane.showMessageDialog(this, "Error: La region solo debe contener letras y espacios.");
                return;
            }

            Almacen nuevo = new Almacen(id, nombre, ciudad, region);
            arbolAlmacenes.insertar(nuevo);
            guardarAlmacenes();
            reconstruirGrafo();
            actualizarTabla();
            actualizarCombos();
            areaResultados.append("Insertado: " + nuevo + "\n");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: El ID debe ser un numero valido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void buscarAlmacen() {
        try {
            String idStr = JOptionPane.showInputDialog("ID del almacen a buscar:");
            if (idStr == null) return;

            int id = Integer.parseInt(idStr);
            Almacen buscado = arbolAlmacenes.buscar(new Almacen(id, "", "", ""));
            if (buscado != null) {
                JOptionPane.showMessageDialog(this, "Encontrado:\n" + buscado);
            } else {
                JOptionPane.showMessageDialog(this, "Almacen no encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: El ID debe ser un numero valido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void modificarAlmacen() {
        try {
            String idViejoStr = JOptionPane.showInputDialog("ID del almacen a modificar:");
            if (idViejoStr == null) return;

            int idViejo = Integer.parseInt(idViejoStr);
            Almacen viejo = arbolAlmacenes.buscar(new Almacen(idViejo, "", "", ""));

            if (viejo != null) {
                String idNuevoStr = JOptionPane.showInputDialog("Nuevo ID:", viejo.getId());
                if (idNuevoStr == null) return;

                int idNuevo = Integer.parseInt(idNuevoStr);
                if (String.valueOf(idNuevo).length() < 5) {
                    JOptionPane.showMessageDialog(this, "Error: El ID debe tener al menos 5 digitos.");
                    return;
                }

                if (idNuevo != idViejo && idExiste(idNuevo)) {
                    JOptionPane.showMessageDialog(this, "Error: Ya existe un almacen con el ID " + idNuevo + ".");
                    return;
                }

                String nombreNuevo = JOptionPane.showInputDialog("Nuevo nombre:", viejo.getNombre());
                if (nombreNuevo == null) return;
                if (!soloLetras(nombreNuevo)) {
                    JOptionPane.showMessageDialog(this, "Error: El nombre solo debe contener letras y espacios.");
                    return;
                }

                String ciudadNueva = JOptionPane.showInputDialog("Nueva ciudad:", viejo.getCiudad());
                if (ciudadNueva == null) return;
                if (!soloLetras(ciudadNueva)) {
                    JOptionPane.showMessageDialog(this, "Error: La ciudad solo debe contener letras y espacios.");
                    return;
                }

                String regionNueva = JOptionPane.showInputDialog("Nueva region:", viejo.getRegion());
                if (regionNueva == null) return;
                if (!soloLetras(regionNueva)) {
                    JOptionPane.showMessageDialog(this, "Error: La region solo debe contener letras y espacios.");
                    return;
                }

                Almacen nuevo = new Almacen(idNuevo, nombreNuevo, ciudadNueva, regionNueva);
                arbolAlmacenes.modificar(viejo, nuevo);
                guardarAlmacenes();
                reconstruirGrafo();
                actualizarTabla();
                actualizarCombos();
                areaResultados.append("Modificado: " + viejo + " → " + nuevo + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Almacen no encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: El ID debe ser un numero valido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminarAlmacen() {
        try {
            String idStr = JOptionPane.showInputDialog("ID del almacen a eliminar:");
            if (idStr == null) return;

            int id = Integer.parseInt(idStr);
            Almacen eliminar = arbolAlmacenes.buscar(new Almacen(id, "", "", ""));

            if (eliminar != null) {
                arbolAlmacenes.eliminar(eliminar);
                guardarAlmacenes();
                reconstruirGrafo();
                actualizarTabla();
                actualizarCombos();
                areaResultados.append("Eliminado: " + eliminar + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Almacen no encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: El ID debe ser un numero valido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void reconstruirGrafo() {
        construirGrafoDesdeArbol();
        dijkstra = new Dijkstra(grafo);
    }

    private void calcularRuta() {
        String origenStr = (String) comboOrigen.getSelectedItem();
        String destinoStr = (String) comboDestino.getSelectedItem();

        if (origenStr == null || destinoStr == null) {
            areaResultados.append("Seleccione origen y destino.\n");
            return;
        }

        if (origenStr.equals(destinoStr)) {
            areaResultados.append("El origen y el destino no pueden ser el mismo almacen.\n");
            return;
        }

        try {
            int origenId = Integer.parseInt(origenStr.split(" - ")[0]);
            int destinoId = Integer.parseInt(destinoStr.split(" - ")[0]);

            List<Almacen> almacenes = arbolAlmacenes.obtenerTodos();
            int idxOrigen = -1, idxDestino = -1;
            for (int i = 0; i < almacenes.size(); i++) {
                if (almacenes.get(i).getId() == origenId) idxOrigen = i;
                if (almacenes.get(i).getId() == destinoId) idxDestino = i;
            }

            if (idxOrigen == -1 || idxDestino == -1) {
                areaResultados.append("Almacen no encontrado.\n");
                return;
            }

            Dijkstra.Resultado resultado = dijkstra.encontrarCaminoMasCorto(idxOrigen, idxDestino);

            areaResultados.append("\n========== RUTA MAS CORTA ==========\n");
            areaResultados.append("Origen: " + grafo.getNombreVertice(idxOrigen) + "\n");
            areaResultados.append("Destino: " + grafo.getNombreVertice(idxDestino) + "\n");

            if (resultado.hayCamino()) {
                areaResultados.append("Camino: " + resultado.getCaminoConNombres() + "\n");
                areaResultados.append("Distancia total: " + resultado.getDistancia() + " km\n");
            } else {
                areaResultados.append("No hay ruta disponible.\n");
            }
            areaResultados.append("=======================================\n\n");
        } catch (Exception ex) {
            areaResultados.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void mostrarGrafo() {
        JTextArea textArea = new JTextArea(grafo.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, scroll, "Matriz de Adyacencia", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Almacen a : arbolAlmacenes.obtenerTodos()) {
            modeloTabla.addRow(new Object[]{a.getId(), a.getNombre(), a.getCiudad(), a.getRegion()});
        }
    }

    private void actualizarCombos() {
        comboOrigen.removeAllItems();
        comboDestino.removeAllItems();
        for (Almacen a : arbolAlmacenes.obtenerTodos()) {
            String item = a.getId() + " - " + a.getNombre();
            comboOrigen.addItem(item);
            comboDestino.addItem(item);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}