package com.jcaa.imc.rmi.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jcaa.imc.rmi.lib.DatosImc;
import com.jcaa.imc.rmi.lib.IRemotaCalculoImc;
import java.io.IOException;
import java.net.ServerSocket;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;
import org.junit.jupiter.api.Test;

/**
 * Prueba de integracion: levanta el servidor LipeRMI en un puerto libre y lo
 * invoca con un cliente LipeRMI, de manera que los objetos viajan serializados
 * por la red.
 */
class ServidorLipeRmiIntegracionTest {

    @Test
    void elClienteInvocaElMetodoRemoto() throws Exception {
        final int puerto = puertoLibre();
        final Servidor servidor = new Servidor(puerto);
        servidor.iniciar();
        assertTrue(servidor.estaActivo());

        final Client cliente = new Client("127.0.0.1", puerto, new CallHandler());
        try {
            final IRemotaCalculoImc remoto =
                    (IRemotaCalculoImc) cliente.getGlobal(IRemotaCalculoImc.class);
            assertNotNull(remoto);

            final DatosImc respuesta = remoto.calcularImc(new DatosImc(70, 1.75));
            assertEquals(22.86, respuesta.getResultado(), 0.01);
            assertEquals("Estas bien de peso", respuesta.getInterpretacion());

            final DatosImc invalido = remoto.calcularImc(new DatosImc(0, 1.75));
            assertTrue(invalido.getInterpretacion().startsWith("ERROR"));
        } finally {
            cliente.close();
            servidor.detener();
        }
        assertTrue(!servidor.estaActivo());
    }

    private static int puertoLibre() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
