package com.github.erodriguezg.autoparchemavenplugin.executors;

import com.github.erodriguezg.autoparchemavenplugin.ParcheSql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;

/**
 * @author rockman
 */
public class ParcheSqlSinRegistroExecutor extends ParcheSqlExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ParcheSqlSinRegistroExecutor.class);

    public ParcheSqlSinRegistroExecutor(String jdbcUrl, String username, String password, String tablaParches, File directorioParches) throws SQLException {
        super(jdbcUrl, username, password, tablaParches, directorioParches);
    }

    @Override
    protected void validarParche(ParcheSql parche) {
        //no hace nada porque no es necesario validar parches drop
    }

    protected boolean esParcheTestDrop() {
        return true;
    }


    @Override
    public void execute() throws SQLException {
        try {
            super.execute();
        } catch (SQLException ex) {
            LOG.debug("Error al momento de executar, se omite la excepción: ", ex);
        }

    }
}
