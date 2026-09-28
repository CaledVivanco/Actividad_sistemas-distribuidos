package com.jcaa.imc.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

/**
 * Ejemplo minimo de servidor con gRPC.
 *
 * <p>Se implementa el servicio generado a partir de {@code imc.proto} y se
 * publica en el puerto 50051. gRPC transporta las llamadas sobre HTTP/2 y
 * serializa los mensajes con Protocol Buffers.</p>
 */
public final class ServidorGrpc {

    private static final int PUERTO = 50051;

    private ServidorGrpc() {
        // Evita instanciacion - clase de arranque estatica
    }

    public static void main(final String[] args) throws IOException, InterruptedException {
        final Server servidor = ServerBuilder.forPort(PUERTO)
                .addService(new CalculoImcService())
                .build()
                .start();

        System.out.println("Servidor gRPC escuchando en el puerto " + PUERTO + ".");
        System.out.println("Presione Ctrl+C para detenerlo.");
        servidor.awaitTermination();
    }

    /** Implementacion del servicio definido en imc.proto. */
    static final class CalculoImcService extends CalculoImcGrpc.CalculoImcImplBase {

        @Override
        public void calcular(final DatosImc peticion,
                             final StreamObserver<ResultadoImc> observador) {
            final double peso = peticion.getPeso();
            final double altura = peticion.getAltura();

            final ResultadoImc.Builder respuesta = ResultadoImc.newBuilder();
            if (peso <= 0 || altura <= 0) {
                respuesta.setImc(0);
                respuesta.setInterpretacion("ERROR: El peso y la altura deben ser mayores que 0");
            } else {
                final double imc = peso / (altura * altura);
                respuesta.setImc(imc);
                respuesta.setInterpretacion(interpretar(imc));
            }
            observador.onNext(respuesta.build());
            observador.onCompleted();
        }

        private static String interpretar(final double imc) {
            if (imc < 18.5) {
                return "Debes consultar un Medico, tu peso es muy bajo";
            } else if (imc <= 24.9) {
                return "Estas bien de peso";
            } else if (imc <= 29.9) {
                return "Debes bajar un poco de peso";
            } else {
                return "Debes consultar un Medico, tu peso es muy alto";
            }
        }
    }
}
