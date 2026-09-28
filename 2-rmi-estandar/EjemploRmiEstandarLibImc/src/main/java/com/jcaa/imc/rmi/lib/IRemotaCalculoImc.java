package com.jcaa.imc.rmi.lib;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz para invocar los metodos de forma remota con RMI estandar.
 *
 * <p>Diferencias con LipeRMI: aqui la interfaz <b>debe</b> extender
 * {@link Remote} y <b>cada metodo debe declarar</b> {@link RemoteException},
 * porque es el contrato que exige el paquete {@code java.rmi}.</p>
 */
public interface IRemotaCalculoImc extends Remote {

    /** Nombre con el que el servicio se publica en el registro RMI. */
    String NOMBRE_SERVICIO = "CalculoImc";

    /**
     * Calcula el IMC de forma remota.
     *
     * @param datos peso y altura de la persona.
     * @return los mismos datos con el resultado y la interpretacion.
     * @throws RemoteException si falla la comunicacion con el servidor.
     */
    DatosImc calcularImc(DatosImc datos) throws RemoteException;
}
