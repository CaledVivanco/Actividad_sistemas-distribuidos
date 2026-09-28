package com.jcaa.imc.rmi.servidor;

import com.jcaa.imc.rmi.lib.DatosImc;
import com.jcaa.imc.rmi.lib.IRemotaCalculoImc;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

/**
 * Implementacion del objeto remoto con RMI estandar.
 *
 * <p>Diferencias con LipeRMI: la clase <b>debe extender</b>
 * {@link UnicastRemoteObject} (o exportarse con
 * {@code UnicastRemoteObject.exportObject}) y su constructor lanza
 * {@link RemoteException}. LipeRMI no exige ninguna de las dos cosas.</p>
 */
public class CalculoRmiImcImplem extends UnicastRemoteObject implements IRemotaCalculoImc {

    private static final long serialVersionUID = 1L;

    private static final double LIMITE_BAJO_PESO = 18.5;
    private static final double LIMITE_NORMAL = 24.9;
    private static final double LIMITE_SOBREPESO = 29.9;

    /** Exporta el objeto remoto en un puerto anonimo. */
    public CalculoRmiImcImplem() throws RemoteException {
        super();
    }

    /** Exporta el objeto remoto en un puerto fijo (util para atravesar el firewall). */
    public CalculoRmiImcImplem(final int puerto) throws RemoteException {
        super(puerto);
    }

    @Override
    public DatosImc calcularImc(final DatosImc datos) throws RemoteException {
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
