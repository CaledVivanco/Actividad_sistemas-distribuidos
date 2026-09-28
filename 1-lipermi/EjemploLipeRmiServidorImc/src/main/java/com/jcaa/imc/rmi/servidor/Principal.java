package com.jcaa.imc.rmi.servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * Punto de entrada del servidor.
 *
 * <p>Arranca el servicio RMI, muestra el puerto y la direccion IP que debe
 * escribir el cliente, y queda escuchando hasta que se presione Enter.</p>
 */
public class Principal {

    public static void main(final String[] args) throws Exception {
        final int puerto = args.length > 0
                ? Integer.parseInt(args[0].trim())
                : Servidor.PUERTO_POR_DEFECTO;

        final Servidor servidor = new Servidor(puerto);
        servidor.agregarObservador(Servidor.observadorEnConsola());
        servidor.iniciar();

        System.out.println("=========================================================");
        System.out.println("  SERVIDOR RMI CON LIPERMI - CALCULO DEL IMC");
        System.out.println("=========================================================");
        System.out.println("  Puerto            : " + servidor.getPuerto());
        System.out.println("  IP de esta maquina: " + obtenerIpLocal());
        System.out.println("  IP local          : 127.0.0.1");
        System.out.println("=========================================================");
        System.out.println();
        System.out.println("Presione Enter para detener el servidor...");

        try (BufferedReader entrada = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            entrada.readLine();
        }
        servidor.detener();
        System.out.println("Servidor detenido.");
    }

    private static String obtenerIpLocal() {
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
