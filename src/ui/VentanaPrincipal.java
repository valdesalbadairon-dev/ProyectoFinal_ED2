package ui;

import arbol.ArbolBinarioBusqueda;
import grafo.Dijkstra;
import grafo.Grafo;
import modelo.Almacen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private ArbolBinarioBusqueda<Almacen> arbolAlmacenes;
    private Grafo grafo;
    private Dijkstra dijkstra;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboOrigen;
    private JComboBox<String> comboDestino;
    private JTextArea areaResultados;

    public VentanaPrincipal() {
        setTitle("Sistema de Logistica y Rutas de Distribucion");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        inicializarDatosPrueba();
        crearPanelSuperior();
        crearPanelCentral();
        crearPanelInferior();

        actualizarTabla();
        actualizarCombos();
    }

    private void inicializarDatosPrueba() {
        arbolAlmacenes = new ArbolBinarioBusqueda<>();

        arbolAlmacenes.insertar(new Almacen(1, "Central", "La Habana", "Occidente"));
        arbolAlmacenes.insertar(new Almacen(2, "Este", "Santiago de Cuba", "Oriente"));
        arbolAlmacenes.insertar(new Almacen(3, "Oeste", "Pinar del Rio", "Occidente"));
        arbolAlmacenes.insertar(new Almacen(4, "Norte", "Santa Clara", "Centro"));
        arbolAlmacenes.insertar(new Almacen(5, "Sur", "Camaguey", "Centro"));
        arbolAlmacenes.insertar(new Almacen(6, "Isla", "Isla de la Juventud", "Occidente"));

        construirGrafoDesdeArbol();
    }

    private void construirGrafoDesdeArbol() {
        List<Almacen> almacenes = arbolAlmacenes.obtenerTodos();
        int n = almacenes.size();
        grafo = new Grafo(n);

        for (int i = 0; i < n; i++) {
            grafo.setNombreVertice(i, almacenes.get(i).getNombre());
        }

        int[] idx = new int[7];
        for (int i = 0; i < n; i++) {
            idx[almacenes.get(i).getId()] = i;
        }

        grafo.agregarArista(idx[1], idx[2], 900);
        grafo.agregarArista(idx[1], idx[3], 180);
        grafo.agregarArista(idx[1], idx[4], 280);
        grafo.agregarArista(idx[1], idx[5], 550);
        grafo.agregarArista(idx[1], idx[6], 150);
        grafo.agregarArista(idx[2], idx[5], 650);
        grafo.agregarArista(idx[2], idx[4], 780);
        grafo.agregarArista(idx[4], idx[5], 320);
        grafo.agregarArista(idx[4], idx[3], 400);
        grafo.agregarArista(idx[5], idx[3], 700);
        grafo.agregarArista(idx[6], idx[3], 200);

        dijkstra = new Dijkstra(grafo);
    }

    private void crearPanelSuperior() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("CRUD - Gestion de Almacenes (Arbol ABB)"));

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
        panel.setBorder(BorderFactory.createTitledBorder("Almacenes Registrados (Recorrido Inorden)"));

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Ciudad", "Region"}, 0);
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        panel.add(scroll, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
    }

    private void crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Rutas y Distribucion (Grafo + Dijkstra)"));

        JPanel panelRutas = new JPanel(new GridLayout(2, 2, 10, 10));
        panelRutas.setBorder(BorderFactory.createTitledBorder("Calcular Ruta Mas Corta"));

        comboOrigen = new JComboBox<>();
        comboDestino = new JComboBox<>();
        JButton btnCalcular = new JButton("Calcular Ruta");
        JButton btnVerGrafo = new JButton("Ver Matriz");

        panelRutas.add(new JLabel("Origen:"));
        panelRutas.add(comboOrigen);
        panelRutas.add(new JLabel("Destino:"));
        panelRutas.add(comboDestino);

        btnCalcular.addActionListener(e -> calcularRuta());
        btnVerGrafo.addActionListener(e -> mostrarGrafo());

        areaResultados = new JTextArea(10, 50);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollResultados = new JScrollPane(areaResultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnCalcular);
        panelBotones.add(btnVerGrafo);

        panel.add(panelRutas, BorderLayout.NORTH);
        panel.add(scrollResultados, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        add(panel, BorderLayout.SOUTH);
    }

    private void insertarAlmacen() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("ID del almacen:"));
            String nombre = JOptionPane.showInputDialog("Nombre:");
            String ciudad = JOptionPane.showInputDialog("Ciudad:");
            String region = JOptionPane.showInputDialog("Region:");

            Almacen nuevo = new Almacen(id, nombre, ciudad, region);
            arbolAlmacenes.insertar(nuevo);
            reconstruirGrafo();
            actualizarTabla();
            actualizarCombos();
            areaResultados.append("Insertado: " + nuevo + "\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void buscarAlmacen() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("ID del almacen a buscar:"));
            Almacen buscado = arbolAlmacenes.buscar(new Almacen(id, "", "", ""));
            if (buscado != null) {
                JOptionPane.showMessageDialog(this, "Encontrado:\n" + buscado);
            } else {
                JOptionPane.showMessageDialog(this, "Almacen no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void modificarAlmacen() {
        try {
            int idViejo = Integer.parseInt(JOptionPane.showInputDialog("ID del almacen a modificar:"));
            Almacen viejo = arbolAlmacenes.buscar(new Almacen(idViejo, "", "", ""));
            if (viejo != null) {
                int idNuevo = Integer.parseInt(JOptionPane.showInputDialog("Nuevo ID:", viejo.getId()));
                String nombreNuevo = JOptionPane.showInputDialog("Nuevo nombre:", viejo.getNombre());
                String ciudadNueva = JOptionPane.showInputDialog("Nueva ciudad:", viejo.getCiudad());
                String regionNueva = JOptionPane.showInputDialog("Nueva region:", viejo.getRegion());

                Almacen nuevo = new Almacen(idNuevo, nombreNuevo, ciudadNueva, regionNueva);
                arbolAlmacenes.modificar(viejo, nuevo);
                reconstruirGrafo();
                actualizarTabla();
                actualizarCombos();
                areaResultados.append("Modificado: " + viejo + " → " + nuevo + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Almacen no encontrado.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminarAlmacen() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("ID del almacen a eliminar:"));
            Almacen eliminar = arbolAlmacenes.buscar(new Almacen(id, "", "", ""));
            if (eliminar != null) {
                arbolAlmacenes.eliminar(eliminar);
                reconstruirGrafo();
                actualizarTabla();
                actualizarCombos();
                areaResultados.append("Eliminado: " + eliminar + "\n");
            } else {
                JOptionPane.showMessageDialog(this, "Almacen no encontrado.");
            }
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