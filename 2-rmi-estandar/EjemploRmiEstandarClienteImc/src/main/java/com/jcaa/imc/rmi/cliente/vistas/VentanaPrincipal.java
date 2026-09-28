package com.jcaa.imc.rmi.cliente.vistas;

import com.jcaa.imc.rmi.lib.DatosImc;
import com.jcaa.imc.rmi.lib.IRemotaCalculoImc;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.Serial;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Ventana del cliente con RMI estandar.
 *
 * <p>Diferencias con LipeRMI: la conexion se hace con {@code Naming.lookup}
 * contra el registro RMI (por defecto en el puerto 1099) y cada llamada puede
 * lanzar {@code RemoteException}.</p>
 */
public class VentanaPrincipal extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int PUERTO_REGISTRO_POR_DEFECTO = 1099;

    private final JTextField campoIpServidor = new JTextField("127.0.0.1");
    private final JTextField campoPuertoRegistro =
            new JTextField(String.valueOf(PUERTO_REGISTRO_POR_DEFECTO));
    private final JButton botonConectar = new JButton("Conectar");
    private final JLabel textoEstado = new JLabel("Desconectado");
    private final JTextField campoPeso = new JTextField();
    private final JTextField campoAltura = new JTextField();
    private final JButton botonCalcular = new JButton("Calcular IMC");
    private final JTextArea textoResultado = new JTextArea(2, 30);
    private final JTextArea textoMensaje = new JTextArea(2, 30);
    private final JTextArea textoRegistro = new JTextArea();

    private transient IRemotaCalculoImc calculoImcRemoto;

    public VentanaPrincipal() {
        super("Cliente IMC - RMI estandar de Java");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 560);
        setLocationRelativeTo(null);

        final JPanel panelConexion = new JPanel(new GridLayout(3, 2, 8, 8));
        panelConexion.setBorder(BorderFactory.createTitledBorder("Registro RMI (rmiregistry)"));
        panelConexion.add(new JLabel("Direccion IP:"));
        panelConexion.add(campoIpServidor);
        panelConexion.add(new JLabel("Puerto del registro:"));
        panelConexion.add(campoPuertoRegistro);
        panelConexion.add(textoEstado);
        panelConexion.add(botonConectar);

        final JPanel panelDatos = new JPanel(new GridLayout(3, 2, 8, 8));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos de la persona"));
        panelDatos.add(new JLabel("Peso (kg):"));
        panelDatos.add(campoPeso);
        panelDatos.add(new JLabel("Altura (m):"));
        panelDatos.add(campoAltura);
        panelDatos.add(new JLabel(" "));
        panelDatos.add(botonCalcular);

        final JPanel centro = new JPanel(new GridLayout(2, 1, 8, 8));
        centro.add(panelConexion);
        centro.add(panelDatos);

        textoResultado.setEditable(false);
        textoResultado.setFont(new Font("Consolas", Font.BOLD, 16));
        final JPanel panelResultado = new JPanel(new BorderLayout());
        panelResultado.setBorder(BorderFactory.createTitledBorder("Resultado (calculado en el servidor)"));
        panelResultado.add(new JScrollPane(textoResultado), BorderLayout.NORTH);
        panelResultado.add(new JScrollPane(textoMensaje), BorderLayout.CENTER);

        textoRegistro.setEditable(false);
        textoRegistro.setFont(new Font("Consolas", Font.PLAIN, 11));
        final JPanel panelRegistro = new JPanel(new BorderLayout());
        panelRegistro.setBorder(BorderFactory.createTitledBorder("Registro"));
        panelRegistro.add(new JScrollPane(textoRegistro), BorderLayout.CENTER);

        final JPanel sur = new JPanel(new GridLayout(2, 1, 8, 8));
        sur.add(panelResultado);
        sur.add(panelRegistro);

        final JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(centro, BorderLayout.NORTH);
        contenido.add(sur, BorderLayout.CENTER);
        add(contenido);

        botonConectar.addActionListener(evento -> alternarConexion());
        botonCalcular.addActionListener(evento -> calcular());
        actualizarEstado();
    }

    private void alternarConexion() {
        if (esConectado()) {
            calculoImcRemoto = null;
            registrar("Desconectado del registro RMI.");
            actualizarEstado();
            return;
        }
        try {
            final int puerto = Integer.parseInt(campoPuertoRegistro.getText().trim());
            final String url = construirUrl(campoIpServidor.getText().trim(), puerto);
            calculoImcRemoto = (IRemotaCalculoImc) Naming.lookup(url);
            registrar("Conectado al servicio " + url);
            actualizarEstado();
        } catch (final NumberFormatException excepcion) {
            avisar("El puerto '" + campoPuertoRegistro.getText() + "' no es un numero valido.");
        } catch (final Exception excepcion) {
            avisar("ERROR AL CONECTAR: " + excepcion.getMessage());
        }
    }

    private void calcular() {
        if (!esConectado()) {
            avisar("Primero debe conectarse con el servidor.");
            return;
        }
        final Double peso = aNumero(campoPeso.getText());
        final Double altura = aNumero(campoAltura.getText());
        if (peso == null || altura == null) {
            avisar("Debe ingresar valores numericos de peso y altura.");
            return;
        }
        botonCalcular.setEnabled(false);
        final Thread hilo = new Thread(() -> invocarServidor(peso, altura));
        hilo.start();
    }

    private void invocarServidor(final double peso, final double altura) {
        try {
            registrar("Enviados los datos -> peso: " + peso + " kg, altura: " + altura + " m");
            final DatosImc respuesta = calculoImcRemoto.calcularImc(new DatosImc(peso, altura));
            registrar("Respuesta del servidor -> IMC: " + respuesta.getResultado()
                    + " | " + respuesta.getInterpretacion());
            SwingUtilities.invokeLater(() -> {
                textoResultado.setText(respuesta.getResultado() > 0
                        ? "IMC: " + formatear(respuesta.getResultado())
                        : "");
                textoMensaje.setText(respuesta.getInterpretacion());
                botonCalcular.setEnabled(true);
            });
        } catch (final RemoteException excepcion) {
            SwingUtilities.invokeLater(() -> {
                avisar("ERROR de comunicacion: " + excepcion.getMessage());
                botonCalcular.setEnabled(true);
            });
        }
    }

    private void actualizarEstado() {
        final boolean conectado = esConectado();
        botonConectar.setText(conectado ? "Desconectar" : "Conectar");
        textoEstado.setText(conectado ? "Conectado" : "Desconectado");
        textoEstado.setForeground(conectado ? new Color(0, 128, 0) : Color.RED);
        campoIpServidor.setEditable(!conectado);
        campoPuertoRegistro.setEditable(!conectado);
        campoPeso.setEnabled(conectado);
        campoAltura.setEnabled(conectado);
        botonCalcular.setEnabled(conectado);
    }

    private boolean esConectado() {
        return calculoImcRemoto != null;
    }

    private void registrar(final String mensaje) {
        SwingUtilities.invokeLater(() -> textoRegistro.append(mensaje + System.lineSeparator()));
    }

    private void avisar(final String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Cliente IMC", JOptionPane.WARNING_MESSAGE);
    }

    private static String construirUrl(final String host, final int puerto) {
        return "rmi://" + host + ":" + puerto + "/" + IRemotaCalculoImc.NOMBRE_SERVICIO;
    }

    private static Double aNumero(final String texto) {
        try {
            return Double.valueOf(texto.trim().replace(',', '.'));
        } catch (final NumberFormatException excepcion) {
            return null;
        }
    }

    private static String formatear(final double valor) {
        final DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#.##", simbolos).format(valor);
    }
}
