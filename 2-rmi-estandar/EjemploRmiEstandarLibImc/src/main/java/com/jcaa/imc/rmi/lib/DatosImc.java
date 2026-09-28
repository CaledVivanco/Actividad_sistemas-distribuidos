package com.jcaa.imc.rmi.lib;

import java.io.Serial;
import java.io.Serializable;

/**
 * Clase que transporta los datos del IMC entre el cliente y el servidor.
 *
 * <p>En RMI estandar tambien debe implementar {@link Serializable}: es el
 * requisito de la tecnologia para que los parametros y los valores de retorno
 * puedan viajar por la red.</p>
 */
public class DatosImc implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private double peso;
    private double altura;
    private double resultado;
    private String interpretacion;

    public DatosImc() {
        this.interpretacion = "";
    }

    public DatosImc(final double peso, final double altura) {
        this();
        this.peso = peso;
        this.altura = altura;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(final double peso) {
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(final double altura) {
        this.altura = altura;
    }

    public double getResultado() {
        return resultado;
    }

    public void setResultado(final double resultado) {
        this.resultado = resultado;
    }

    public String getInterpretacion() {
        return interpretacion;
    }

    public void setInterpretacion(final String interpretacion) {
        this.interpretacion = interpretacion;
    }

    @Override
    public String toString() {
        return "DatosImc{peso=" + peso + ", altura=" + altura
                + ", resultado=" + resultado + ", interpretacion=" + interpretacion + '}';
    }
}
