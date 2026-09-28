package com.jcaa.imc.rmi.servidor;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;

/**
 * Publica el objeto remoto con RMI estandar de Java.
 *
 * <p>Pasos que exige esta tecnologia (y que LipeRMI no necesita):</p>
 * <ol>
 *   <li>crear o tener disponible el registro RMI ({@code rmiregistry});</li>
 *   <li>exportar el objeto remoto en un puerto conocido;</li>
 *   <li>registrarlo con un nombre usando {@code Naming.rebind}.</li>
 * </ol>
 */
public class Servidor {

    public static final int PUERTO_REGISTRO_POR_DEFECTO = 1099;
    public static final int PUERTO_OBJETO_POR_DEFECTO = 1100;

    private final int puertoRegistro;
    private final int puertoObjeto;
    private Registry registro;
    private CalculoRmiImcImplem implementacion;
    private boolean activo;

    public Servidor() {
        this(PUERTO_REGISTRO_POR_DEFECTO, PUERTO_OBJETO_POR_DEFECTO);
    }

    public Servidor(final int puertoRegistro, final int puertoObjeto) {
        this.puertoRegistro = puertoRegistro;
        this.puertoObjeto = puertoObjeto;
    }

    /** Crea el registro, exporta el objeto y lo publica con un nombre. */
    public void iniciar() throws RemoteException {
        implementacion = new CalculoRmiImcImplem(puertoObjeto);
        registro = LocateRegistry.createRegistry(puertoRegistro);
        registro.rebind(com.jcaa.imc.rmi.lib.IRemotaCalculoImc.NOMBRE_SERVICIO, implementacion);
        activo = true;
    }

    public void detener() throws RemoteException, NoSuchObjectException {
        if (registro != null) {
            try {
                registro.unbind(com.jcaa.imc.rmi.lib.IRemotaCalculoImc.NOMBRE_SERVICIO);
            } catch (final Exception excepcion) {
                System.out.println("Aviso al desregistrar: " + excepcion.getMessage());
            }
        }
        if (implementacion != null) {
            UnicastRemoteObject.unexportObject(implementacion, true);
        }
        activo = false;
    }

    public boolean estaActivo() {
        return activo;
    }

    public int getPuertoRegistro() {
        return puertoRegistro;
    }

    public int getPuertoObjeto() {
        return puertoObjeto;
    }

    /** URL que debe escribir el cliente: rmi://IP:PUERTO/NOMBRE. */
    public String getUrlPublica() {
        return "rmi://" + obtenerIpLocal() + ":" + puertoRegistro + "/"
                + com.jcaa.imc.rmi.lib.IRemotaCalculoImc.NOMBRE_SERVICIO;
    }

    public static String obtenerIpLocal() {
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                final NetworkInterface interfaz = interfaces.nextElement();
                if (interfaz.isLoopback() || !interfaz.isUp()) {
                    continue;
                }
                final Enumeration<InetAddress> direcciones = interfaz.getInetAddresses();
                while (direcciones.hasMoreElements()) {
                    final InetAddress direccion = direcciones.nextElement();
                    if (direccion instanceof Inet4Address && !direccion.isLoopbackAddress()) {
                        return direccion.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (final Exception excepcion) {
            return "127.0.0.1";
        }
    }
}
