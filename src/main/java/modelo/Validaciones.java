package modelo;

import static modelo.Main.*;
import java.time.LocalDate;
import java.time.Period;

/**
 * Validaciones generales del sistema SECR.
 * Se centralizan aquí para no repetir código en la interfaz, gestores y Main.
 */
public final class Validaciones {
    private Validaciones() { }

    public static void validarCedula(String cedula) throws SecrException {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            throw new SecrException("La cédula debe tener exactamente 10 números.");
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (provincia < 1 || provincia > 24 || tercerDigito >= 6) {
            throw new SecrException("La cédula no cumple la regla de validación ecuatoriana.");
        }

        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            if (valor >= 10) valor -= 9;
            suma += valor;
        }
        int digitoCalculado = (10 - (suma % 10)) % 10;
        int digitoIngresado = Character.getNumericValue(cedula.charAt(9));
        if (digitoCalculado != digitoIngresado) {
            throw new SecrException("La cédula ingresada no es válida según el dígito verificador.");
        }
    }

    public static void validarNombre(String nombre) throws SecrException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new SecrException("El nombre no puede estar vacío.");
        }
        if (!nombre.trim().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+")) {
            throw new SecrException("El nombre solo debe contener letras y espacios.");
        }
    }

    public static void validarEdadMayorEdad(LocalDate fechaNacimiento) throws EdadNoValidaException {
        if (fechaNacimiento == null) {
            throw new EdadNoValidaException("La fecha de nacimiento no puede estar vacía.");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new EdadNoValidaException("La fecha de nacimiento no puede ser futura.");
        }
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < 18) {
            throw new EdadNoValidaException("El conductor debe ser mayor de edad: mínimo 18 años.");
        }
    }

    public static String normalizarNumeroLicencia(String licencia, String categoria) {
        if (licencia == null) return "";
        String valor = licencia.trim().toUpperCase();
        String cat = categoria == null ? "" : categoria.trim().toUpperCase();
        if (valor.matches("[BCE]-\\d{3,6}")) return valor;
        if (valor.matches("\\d{3,6}") && cat.matches("B|C|E")) return cat + "-" + valor;
        return valor;
    }

    public static void validarNumeroLicencia(String licencia) throws LicenciaNoValidaException {
        if (licencia == null || !licencia.trim().toUpperCase().matches("[BCE]-\\d{3,6}")) {
            throw new LicenciaNoValidaException("El número de licencia debe tener formato B-001, C-009 o E-007890. También puede ingresar solo 001 si selecciona la categoría.");
        }
    }

    public static void validarCategoria(String categoria) throws LicenciaNoValidaException {
        if (categoria == null || !categoria.toUpperCase().matches("B|C|E")) {
            throw new LicenciaNoValidaException("La categoría debe ser B, C o E.");
        }
    }

    public static void validarFechaEmision(LocalDate fechaEmision, LocalDate fechaNacimiento) throws LicenciaNoValidaException {
        if (fechaEmision == null) {
            throw new LicenciaNoValidaException("La fecha de emisión no puede estar vacía.");
        }
        if (fechaEmision.isAfter(LocalDate.now())) {
            throw new LicenciaNoValidaException("La fecha de emisión no puede ser futura.");
        }
        if (fechaNacimiento != null) {
            LocalDate fechaMinimaLicencia = fechaNacimiento.plusYears(18);
            if (fechaEmision.isBefore(fechaMinimaLicencia)) {
                throw new LicenciaNoValidaException(
                        "La fecha de emisión de la licencia debe ser cuando el conductor ya tenga mínimo 18 años. " +
                        "Fecha mínima permitida: " + fechaMinimaLicencia + ".");
            }
        }
    }

    public static void validarCodigoPregunta(String codigo) throws PruebaNoValidaException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new PruebaNoValidaException("El código de la pregunta no puede estar vacío.");
        }
        if (!codigo.trim().toUpperCase().matches("PREG-\\d{1,6}")) {
            throw new PruebaNoValidaException("El código de pregunta debe tener formato PREG-001. También puede ingresar solo 001 en la pantalla de catálogos.");
        }
    }

    public static void validarUbicacion(String ubicacion) throws EventoNoValidoException {
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            throw new EventoNoValidoException("La ubicación no puede estar vacía.");
        }
        if (!ubicacion.trim().matches(".*[A-Za-zÁÉÍÓÚáéíóúÑñ].*")) {
            throw new EventoNoValidoException("La ubicación debe contener texto descriptivo, no solo números.");
        }
        if (!ubicacion.trim().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s.,#°-]+")) {
            throw new EventoNoValidoException("La ubicación solo debe contener letras, números y signos básicos.");
        }
    }

    public static void validarFechaEvento(LocalDate fecha) throws EventoNoValidoException {
        if (fecha == null) {
            throw new EventoNoValidoException("La fecha del evento no puede estar vacía.");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new EventoNoValidoException("La fecha del evento no puede ser futura.");
        }
        if (fecha.isBefore(LocalDate.now().minusYears(5))) {
            throw new EventoNoValidoException("La fecha del evento no puede ser menor a 5 años desde la fecha actual.");
        }
    }

    public static void validarPuntos(int puntos) throws PuntajeNoValidoException {
        if (puntos < 0) {
            throw new PuntajeNoValidoException("No se permiten puntos negativos.");
        }
    }
}
