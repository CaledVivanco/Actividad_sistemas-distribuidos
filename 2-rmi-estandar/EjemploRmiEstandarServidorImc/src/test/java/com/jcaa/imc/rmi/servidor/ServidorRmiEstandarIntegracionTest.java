package com.jcaa.imc.rmi.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jcaa.imc.rmi.lib.DatosImc;
import com.jcaa.imc.rmi.lib.IRemotaCalculoImc;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.Naming;
import org.junit.jupiter.api.Test;

/**
 * Prueba de integracion: crea el registro RMI, publica el objeto remoto y lo
 * invoca con {@code Naming.lookup}, igual que lo hace el cliente real.
 */
class ServidorRmiEstandarIntegracionTest {

    @Test
    void elClienteInvocaElObjetoRemotoPorElRegistro() throws Exception {
        final int puertoRegistro = puertoLibre();
        final int puertoObjeto = puertoLibre();

        final Servidor servidor = new Servidor(puertoRegistro, puertoObjeto);
        servidor.iniciar();
        assertTrue(servidor.estaActivo());

        try {
            final String url = "rmi://127.0.0.1:" + puertoRegistro + "/"
                    + IRemotaCalculoImc.NOMBRE_SERVICIO;
            final IRemotaCalculoImc remoto = (IRemotaCalculoImc) Naming.lookup(url);
            assertNotNull(remoto);

            final DatosImc respuesta = remoto.calcularImc(new DatosImc(70, 1.75));
            assertEquals(22.86, respuesta.getResultado(), 0.01);
            assertEquals("Estas bien de peso", respuesta.getInterpretacion());

            final DatosImc invalido = remoto.calcularImc(new DatosImc(0, 1.75));
            assertTrue(invalido.getInterpretacion().startsWith("ERROR"));
        } finally {
            servidor.detener();
        }
        assertFalse(servidor.estaActivo());
    }

    private static int puertoLibre() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
