package modelo;

import static modelo.Main.*;
import java.time.LocalDate;

public class Infraccion extends EventoConduccion {
    public Infraccion(String idEvento, LocalDate fecha, String ubicacion, int gravedad,
                      String desc, TipoEvento tipo) throws EventoNoValidoException {
        super(idEvento, fecha, ubicacion, gravedad, desc, tipo);
    }
}
