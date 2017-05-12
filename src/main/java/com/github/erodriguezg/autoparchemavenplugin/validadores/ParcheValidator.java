package com.github.erodriguezg.autoparchemavenplugin.validadores;

import com.github.erodriguezg.autoparchemavenplugin.ParcheSql;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by takeda on 13-01-16.
 */
public class ParcheValidator {
    public static final Logger LOG = LoggerFactory.getLogger(ParcheValidator.class);

    private static final String TEMPLATE_REGEX_QUERY_INSERT = ".*(INSERT)\\s+(INTO)\\s+(@TABLE)\\s*(\\(NOMBRE\\))\\s+(VALUES)\\s+(\\('@FILE'\\)).*";
    private static final String TEMPLATE_REGEX_TABLE_CREATE = ".*(CREATE)\\s+(TABLE)\\s+(@TABLE)\\s+(\\()\\s+(NOMBRE)\\s+(CHARACTER\\s+VARYING|VARCHAR|STRING).*(PRIMARY)\\s+(KEY)\\s*(\\(NOMBRE\\))\\s*(\\))";


    private static final String TEMPLATE_MSJ_ERROR_QUERY_INSERT = "Parche SQL '@nombreParche' no tiene insert en tabla '@tablaParches' ";
    private static final String TEMPLATE_MSJ_ERROR_TABLE_CREATE = "Parche SQL '@nombreParche' es inicial y no crea la tabla '@tablaParches' ";

    public void verificar(ParcheSql parcheSql, String tablaParches) {
        if (esParcheInicial(parcheSql)) {
            LOG.debug("verificando parche inicial: parche '@parche', tabla '@tabla'"
                    .replace("@parche", parcheSql.getNombre())
                    .replace("@tabla", tablaParches)
            );
            validarCreateTablaParches(parcheSql, tablaParches);
        }
        LOG.debug("verificando insert query: parche '@parche', tabla '@tabla'"
                .replace("@parche", parcheSql.getNombre())
                .replace("@tabla", tablaParches)
        );
        validarInsertQuery(parcheSql, tablaParches);
    }

    private static boolean esParcheInicial(ParcheSql parcheSql) {
        Integer numeroParche = Integer.valueOf(parcheSql.getNombre().split("\\.")[0]);
        return numeroParche == 0;
    }

    private static void validarCreateTablaParches(ParcheSql parcheSql, String tablaParches) {
        String regex = TEMPLATE_REGEX_TABLE_CREATE
                .replace("@TABLE", tablaParches.toUpperCase());
        String fileText = obtenerTextoParche(parcheSql);
        if (!regexValida(regex, fileText.toUpperCase().replace("\n", " "))) {
            String mensaje = TEMPLATE_MSJ_ERROR_TABLE_CREATE
                    .replace("@nombreParche", parcheSql.getNombre())
                    .replace("@tablaParches", tablaParches);
            throw new IllegalStateException(mensaje);
        }
    }

    private static void validarInsertQuery(ParcheSql parcheSql, String tablaParches) {
        String regex = TEMPLATE_REGEX_QUERY_INSERT
                .replace("@TABLE", tablaParches.toUpperCase())
                .replace("@FILE", parcheSql.getNombre().toUpperCase());
        String fileText = obtenerTextoParche(parcheSql);
        if (!regexValida(regex, fileText.toUpperCase().replace("\n", " "))) {
            String mensaje = TEMPLATE_MSJ_ERROR_QUERY_INSERT
                    .replace("@nombreParche", parcheSql.getNombre())
                    .replace("@tablaParches", tablaParches);
            throw new IllegalStateException(mensaje);
        }
    }

    private static String obtenerTextoParche(ParcheSql parcheSql) {
        try {
            return FileUtils.readFileToString(parcheSql.getFile(), "UTF-8").replaceAll("\\p{C}", " ");
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static boolean regexValida(String regex, String texto) {
        LOG.debug("REGEX: " + regex);
        LOG.debug("TEXTO: " + texto);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(texto);
        return matcher.find();
    }

}
