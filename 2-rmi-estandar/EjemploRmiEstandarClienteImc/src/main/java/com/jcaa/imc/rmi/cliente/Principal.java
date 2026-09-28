package com.jcaa.imc.rmi.cliente;

import com.jcaa.imc.rmi.cliente.vistas.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada del cliente con RMI estandar. */
public class Principal {

    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception excepcion) {
            System.out.println("Se usara la apariencia predeterminada.");
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
