package com.jcaa.imc.rmi.servidor;

import com.jcaa.imc.rmi.lib.DatosImc;
import com.jcaa.imc.rmi.lib.IRemotaCalculoImc;
import java.util.Objects;

/**
 * Implementacion del servicio remoto: calcula el IMC y lo clasifica.
 *
 * <p>Esta clase es la que realmente se ejecuta en el servidor: el cliente solo
 * invoca el metodo y recibe los datos ya calculados.</p>
 */
public class CalculoRmiImcImplem implements IRemotaCalculoImc {

    private static final double LIMITE_BAJO_PESO = 18.5;
    private static final double LIMITE_NORMAL = 24.9;
    private static final double LIMITE_SOBREPESO = 29.9;

    @Override
    public DatosImc calcularImc(final DatosImc datos) {
        if (Objects.isNull(datos)) {
            final DatosImc respuesta = new DatosImc();
            respuesta.setInterpretacion("ERROR: no se recibieron datos");
            return respuesta;
        }
        if (datos.getPeso() <= 0 || datos.getAltura() <= 0) {
            datos.setResultado(0);
            datos.setInterpretacion("ERROR: El peso y la altura deben ser mayores que 0");
            return datos;
        }

        final double resultado = datos.getPeso() / (datos.getAltura() * datos.getAltura());
        datos.setResultado(resultado);
        datos.setInterpretacion(interpretar(resultado));
        return datos;
    }

    private static String interpretar(final double imc) {
        if (imc < LIMITE_BAJO_PESO) {
            return "Debes consultar un Medico, tu peso es muy bajo";
        } else if (imc <= LIMITE_NORMAL) {
            return "Estas bien de peso";
        } else if (imc <= LIMITE_SOBREPESO) {
            return "Debes bajar un poco de peso";
        } else {
            return "Debes consultar un Medico, tu peso es muy alto";
        }
    }
}
