package modelo;

public class TipoEvento {
    private String codigo;
    private String nombre;
    private String descripcion;
    private int puntosBase;
    private String estado;

    public TipoEvento(String nombre, String descripcion, int puntosBase) {
        this("INF-" + System.currentTimeMillis() % 100000, nombre, descripcion, puntosBase, "Activo");
    }

    public TipoEvento(String codigo, String nombre, String descripcion, int puntosBase, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosBase = puntosBase;
        this.estado = estado == null ? "Activo" : estado;
    }

    public String getCodigo() { return codigo; }
    public int getPuntosBase() { return puntosBase; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPuntosBase(int puntosBase) { this.puntosBase = puntosBase; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean estaActivo() { return "Activo".equalsIgnoreCase(estado); }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + puntosBase + " pts) | " + estado;
    }
}
