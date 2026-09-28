package com.jcaa.imc.rmi.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jcaa.imc.rmi.lib.DatosImc;
import org.junit.jupiter.api.Test;

class CalculoRmiImcImplemTest {

    private CalculoRmiImcImplem nuevoCalculo() throws Exception {
        return new CalculoRmiImcImplem();
    }

    @Test
    void calculaElImcEsperado() throws Exception {
        final DatosImc respuesta = nuevoCalculo().calcularImc(new DatosImc(70, 1.75));

        assertEquals(22.86, respuesta.getResultado(), 0.01);
        assertEquals("Estas bien de peso", respuesta.getInterpretacion());
    }

    @Test
    void clasificaSegunElResultado() throws Exception {
        final CalculoRmiImcImplem calculo = nuevoCalculo();
        assertEquals("Debes consultar un Medico, tu peso es muy bajo",
                calculo.calcularImc(new DatosImc(45, 1.75)).getInterpretacion());
        assertEquals("Estas bien de peso",
                calculo.calcularImc(new DatosImc(70, 1.75)).getInterpretacion());
        assertEquals("Debes bajar un poco de peso",
                calculo.calcularImc(new DatosImc(85, 1.75)).getInterpretacion());
        assertEquals("Debes consultar un Medico, tu peso es muy alto",
                calculo.calcularImc(new DatosImc(120, 1.75)).getInterpretacion());
    }

    @Test
    void rechazaDatosInvalidos() throws Exception {
        final CalculoRmiImcImplem calculo = nuevoCalculo();
        assertTrue(calculo.calcularImc(new DatosImc(0, 1.75)).getInterpretacion().startsWith("ERROR"));
        assertTrue(calculo.calcularImc(new DatosImc(70, -1)).getInterpretacion().startsWith("ERROR"));
        assertTrue(calculo.calcularImc(null).getInterpretacion().startsWith("ERROR"));
    }
}
