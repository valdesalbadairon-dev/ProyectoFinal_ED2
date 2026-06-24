package modelo;

public class Almacen implements Comparable<Almacen> {
    private int id;
    private String nombre;
    private String ciudad;
    private String region;

    public Almacen(int id, String nombre, String ciudad, String region) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.region = region;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCiudad() { return ciudad; }
    public String getRegion() { return region; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setRegion(String region) { this.region = region; }

    public int compareTo(Almacen o) {
        return Integer.compare(this.id, o.id);
    }

    public String toString() {
        return id + " - " + nombre + " (" + ciudad + ", " + region + ")";
    }
}
