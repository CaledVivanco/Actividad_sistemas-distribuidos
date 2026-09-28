package com.jcaa.imc.rmi.servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Punto de entrada del servidor con RMI estandar.
 *
 * <p>Antes de exportar el objeto se publica la propiedad
 * {@code java.rmi.server.hostname} para que los clientes de otras maquinas
 * reciban la direccion IP correcta en el stub.</p>
 */
public class Principal {

    public static void main(final String[] args) throws Exception {
        final int puertoRegistro = args.length > 0
                ? Integer.parseInt(args[0].trim())
                : Servidor.PUERTO_REGISTRO_POR_DEFECTO;
        final int puertoObjeto = args.length > 1
                ? Integer.parseInt(args[1].trim())
                : Servidor.PUERTO_OBJETO_POR_DEFECTO;

        System.setProperty("java.rmi.server.hostname", Servidor.obtenerIpLocal());

        final Servidor servidor = new Servidor(puertoRegistro, puertoObjeto);
        servidor.iniciar();

        System.out.println("=========================================================");
        System.out.println("  SERVIDOR RMI ESTANDAR DE JAVA - CALCULO DEL IMC");
        System.out.println("=========================================================");
        System.out.println("  Registro RMI (rmiregistry) : puerto " + servidor.getPuertoRegistro());
        System.out.println("  Objeto remoto exportado    : puerto " + servidor.getPuertoObjeto());
        System.out.println("  IP de esta maquina         : " + Servidor.obtenerIpLocal());
        System.out.println("  URL del servicio           : " + servidor.getUrlPublica());
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
}
