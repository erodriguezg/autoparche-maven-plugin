package com.github.erodriguezg.autoparchemavenplugin.validadores;

import com.github.erodriguezg.autoparchemavenplugin.ParcheSql;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

/**
 * Created by takeda on 13-01-16.
 */
public class ParcheValidatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ParcheValidator validator;

    private ParcheSql parcheCorrecto;

    private ParcheSql parcheIncorrecto;

    private ParcheSql[] parchesInicialesIncorrectos;

    private ParcheSql[] parchesInicialesCorrectos;

    private String nombreTablaParches;

    @Before
    public void before() {
        File fileParcheCorrecto = new File(ParcheValidatorTest.class.getResource("/parches/validos/015.sql").getFile());
        this.parcheCorrecto = new ParcheSql(fileParcheCorrecto);

        File fileParcheIncorrecto = new File(ParcheValidatorTest.class.getResource("/parches/invalidos/015.sql").getFile());
        this.parcheIncorrecto = new ParcheSql(fileParcheIncorrecto);

        File fileParcheInicialCorrecto1 = new File(ParcheValidatorTest.class.getResource("/parches/validos/000.sql").getFile());
        File fileParcheInicialCorrecto2 = new File(ParcheValidatorTest.class.getResource("/parches/validos/000.sql.alternativo").getFile());
        File fileParcheInicialCorrecto3 = new File(ParcheValidatorTest.class.getResource("/parches/validos/000.sql.alternativo2").getFile());
        this.parchesInicialesCorrectos = new ParcheSql[]{
                new ParcheSql(fileParcheInicialCorrecto1, "000.sql"),
                new ParcheSql(fileParcheInicialCorrecto2, "000.sql"),
                new ParcheSql(fileParcheInicialCorrecto3, "000.sql")
        };

        File fileParcheInicialIncorrecto1 = new File(ParcheValidatorTest.class.getResource("/parches/invalidos/000.sql").getFile());
        File fileParcheInicialIncorrecto2 = new File(ParcheValidatorTest.class.getResource("/parches/invalidos/000.sql.alternativo").getFile());
        this.parchesInicialesIncorrectos = new ParcheSql[]{
                new ParcheSql(fileParcheInicialIncorrecto1, "000.sql"),
                new ParcheSql(fileParcheInicialIncorrecto2, "000.sql")
        };

        validator = new ParcheValidator();
        this.nombreTablaParches = "parches_sql";
    }

    @Test
    public void dadoParcheCorrectoCuandoVerificarEntoncesNoIllegalStateException() {
        validator.verificar(this.parcheCorrecto, this.nombreTablaParches);
    }

    @Test
    public void dadoParcheSinInsertEnTablaParchesCuadoVerificarEntoncesIllegalStateException() {
        String mensajeEsperado = "Parche SQL '@nombreParche' no tiene insert en tabla '@tablaParches' "
                .replace("@nombreParche", this.parcheIncorrecto.getNombre())
                .replace("@tablaParches", this.nombreTablaParches);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(mensajeEsperado);
        validator.verificar(this.parcheIncorrecto, this.nombreTablaParches);
    }

    @Test
    public void dadoParcheInicialSinTablaParchesCuandoVerificarEntoncesIllegalStateException() {
        for (ParcheSql parcheInicialIncorrecto : parchesInicialesIncorrectos) {
            String mensajeEsperado = "Parche SQL '@nombreParche' es inicial y no crea la tabla '@tablaParches' "
                    .replace("@nombreParche", parcheInicialIncorrecto.getNombre())
                    .replace("@tablaParches", this.nombreTablaParches);
            expectedException.expect(IllegalStateException.class);
            expectedException.expectMessage(mensajeEsperado);
            validator.verificar(parcheInicialIncorrecto, nombreTablaParches);
        }
    }

    @Test
    public void dadoParcheInicialConTablaParchesCuandoVerificarEntoncesNoIllegalStateException() {
        for (ParcheSql parcheInicialCorrecto : parchesInicialesCorrectos) {
            validator.verificar(parcheInicialCorrecto, nombreTablaParches);
        }
    }

}
