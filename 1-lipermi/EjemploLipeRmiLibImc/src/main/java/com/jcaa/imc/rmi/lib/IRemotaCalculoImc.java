package com.jcaa.imc.rmi.lib;

/**
 * Interfaz para invocar los metodos de forma remota.
 *
 * <p>Con LipeRMI la interfaz no necesita extender {@code java.rmi.Remote} ni
 * declarar {@code RemoteException}: la libreria genera el proxy y transporta la
 * llamada por la red.</p>
 */
public interface IRemotaCalculoImc {

    /**
     * Calcula el IMC de forma remota.
     *
     * @param datos peso y altura de la persona.
     * @return los mismos datos con el resultado y la interpretacion.
     */
    DatosImc calcularImc(DatosImc datos);
}
