package com.jcaa.imc.rmi.servidor;

import com.jcaa.imc.rmi.lib.IRemotaCalculoImc;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.IServerListener;
import net.sf.lipermi.net.Server;

/**
 * Publica el objeto remoto con la libreria LipeRMI.
 *
 * <p>LipeRMI concentra el servicio en un unico puerto TCP y evita el registro
 * RMI clasico ({@code rmiregistry}).</p>
 */
public class Servidor {

    public static final int PUERTO_POR_DEFECTO = 9007;

    private final int puerto;
    private final CallHandler invocador;
    private final Server servidor;
    private final CalculoRmiImcImplem calculoImc;
    private boolean activo;

    public Servidor(final int puerto) {
        this.puerto = puerto;
        this.invocador = new CallHandler();
        this.servidor = new Server();
        this.calculoImc = new CalculoRmiImcImplem();
    }

    /** Registra el objeto remoto global y abre el puerto. */
    public void iniciar() throws IOException, LipeRMIException {
        invocador.registerGlobal(IRemotaCalculoImc.class, calculoImc);
        servidor.bind(puerto, invocador);
        activo = true;
    }

    public void detener() {
        servidor.close();
        activo = false;
    }

    public boolean estaActivo() {
        return activo;
    }

    public int getPuerto() {
        return puerto;
    }

    /** Avisa en consola cuando un cliente se conecta o se desconecta. */
    public void agregarObservador(final IServerListener observador) {
        servidor.addServerListener(Objects.requireNonNull(observador));
    }

    /** Observador que imprime en consola las conexiones de los clientes. */
    public static IServerListener observadorEnConsola() {
        return new IServerListener() {
            @Override
            public void clientConnected(final Socket socket) {
                System.out.println("[CLIENTE CONECTADO] "
                        + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            }

            @Override
            public void clientDisconnected(final Socket socket) {
                System.out.println("[CLIENTE DESCONECTADO] "
                        + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            }
        };
    }
}
