package com.jcaa.imc.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Ejemplo minimo de cliente con gRPC.
 *
 * <p>El cliente no necesita conocer la implementacion: usa el stub generado a
 * partir de {@code imc.proto} y llama al metodo como si fuera local.</p>
 */
public final class ClienteGrpc {

    private static final String HOST = "localhost";
    private static final int PUERTO = 50051;

    private ClienteGrpc() {
        // Evita instanciacion - clase de arranque estatica
    }

    public static void main(final String[] args) throws InterruptedException {
        final ManagedChannel canal = ManagedChannelBuilder
                .forAddress(HOST, PUERTO)
                .usePlaintext()
                .build();
        try {
            final CalculoImcGrpc.CalculoImcBlockingStub stub = CalculoImcGrpc.newBlockingStub(canal);

            final double peso = args.length > 0 ? Double.parseDouble(args[0]) : 70;
            final double altura = args.length > 1 ? Double.parseDouble(args[1]) : 1.75;

            final ResultadoImc respuesta = stub.calcular(DatosImc.newBuilder()
                    .setPeso(peso)
                    .setAltura(altura)
                    .build());

            System.out.printf(Locale.US, "Peso: %.2f kg - Altura: %.2f m%n", peso, altura);
            System.out.printf(Locale.US, "IMC: %.2f%n", respuesta.getImc());
            System.out.println("Interpretacion: " + respuesta.getInterpretacion());
        } finally {
            canal.shutdown().awaitTermination(3, TimeUnit.SECONDS);
        }
    }
}
