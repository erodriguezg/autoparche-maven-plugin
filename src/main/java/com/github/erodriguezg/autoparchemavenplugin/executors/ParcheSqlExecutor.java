package com.github.erodriguezg.autoparchemavenplugin.executors;

import com.github.erodriguezg.autoparchemavenplugin.ParcheSql;
import com.github.erodriguezg.autoparchemavenplugin.ant.PlSqlAntTask;
import com.github.erodriguezg.autoparchemavenplugin.ant.SqlAntTask;
import com.github.erodriguezg.autoparchemavenplugin.validadores.ParcheValidator;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParcheSqlExecutor extends DatabaseExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ParcheSqlExecutor.class);

    private final File directorioParches;
    private static final String REGEX = "[0-9]*.sql";

    public ParcheSqlExecutor(String jdbcUrl, String username, String password, String tablaParchesSql, File directorioParches) throws SQLException {
        super(jdbcUrl, username, password, tablaParchesSql);
        this.directorioParches = directorioParches;
    }

    public void execute() throws SQLException {
        try (Connection conexionPrimaria = getConnection()) {
            List<ParcheSql> parches = obtenerParchesSqlDesdeDirectorio();
            LOG.debug("PARCHES ENCONTRADO: '@parches'".replace("@parches", parches.toString()));
            ejecutarParches(conexionPrimaria, parches);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private void ejecutarParches(Connection conexionPrimaria, List<ParcheSql> parches) throws InterruptedException, SQLException {
        for (ParcheSql parche : parches) {
            if (parche.getNombre().matches(REGEX)) {
                try {
                    LOG.debug("validando: '@parche'".replace("@parche", parche.getNombre()));
                    validarParche(parche);
                    LOG.debug("ejecutando: '@parche'".replace("@parche", parche.getNombre()));
                    ejecutarSQL(parche, conexionPrimaria);
                } catch (SQLException | RuntimeException e) {
                    LOG.error("error ejecutar parche", e);
                }
                if (!esParcheTestDrop() && !fueAplicado(conexionPrimaria, parche)) {
                    throw new IllegalStateException("No fue aplicado parche: '@parche'"
                            .replace("@parche", parche.getNombre()));
                }
            }
        }
    }

    /**
     * METODOS PROTEGIDOS
     */

    protected void validarParche(ParcheSql parche) {
        ParcheValidator validator = new ParcheValidator();
        validator.verificar(parche, getNombreTablaParchesSql());
    }

    protected void ejecutarSQL(ParcheSql parche, Connection conexionPrimaria) throws SQLException {
        try {
            LOG.debug("Ejecutando SQL NORMAL ... ");
            ejecutarAntTask(conexionPrimaria, parche, SqlAntTask.class);
        } catch (SQLException ex) {
            LOG.debug("No se pudo ejecutar SQL NORMAL. Ejecutando PLSQL ... ", ex);
            ejecutarAntTask(conexionPrimaria, parche, PlSqlAntTask.class);
        }
    }

    private void ejecutarAntTask(Connection conexionPrimaria, ParcheSql parche, Class<? extends SQLExec> clazz) throws SQLException {
        Connection conexionAntTask = null;
        try {
            if (esParcheTestDrop() || !fueAplicado(conexionPrimaria, parche)) {
                conexionAntTask = getConnection();
                conexionAntTask.setAutoCommit(false);
                SQLExec task = crearAntTask(conexionAntTask, clazz, parche);
                task.execute();
                LOG.info("parche aplicado: '@parche'".replace("@parche", parche.getNombre()));
            }
        } catch (SQLException e) {
            if (conexionAntTask != null) {
                try {
                    conexionAntTask.rollback();
                } catch (Exception ex) {
                    LOG.error("error al hacer rollback conexion 1", ex);
                }
            }
            throw e;
        } finally {
            if (conexionAntTask != null && !conexionAntTask.isClosed()) {
                try {
                    conexionAntTask.close();
                } catch (Exception ex) {
                    LOG.error("error cerrando conexionParche 1", ex);
                }
            }
        }
    }

    protected boolean esParcheTestDrop() {
        return false;
    }

    private SQLExec crearAntTask(Connection conexionAntTask, Class<? extends SQLExec> clazz, ParcheSql parche) {
        if (clazz.equals(SqlAntTask.class)) {
            SqlAntTask task = new SqlAntTask(conexionAntTask);
            task.setSrc(new File(directorioParches + "/" + parche.getNombre()));
            return task;
        } else if (clazz.equals(PlSqlAntTask.class)) {
            PlSqlAntTask task = new PlSqlAntTask(conexionAntTask);
            task.setSrc(new File(directorioParches + "/" + parche.getNombre()));
            return task;
        }
        throw new IllegalStateException("No se encontro la clase: " + clazz);
    }

    protected boolean fueAplicado(Connection connection, ParcheSql parche) {
        int contador;
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT count(nombre) FROM " + getNombreTablaParchesSql() + " WHERE nombre like ?")) {
            pstmt.setString(1, parche.getNombre());
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                contador = rs.getInt(1);
            }
        }catch (SQLException ex) {
            LOG.debug("Al verificar si fue aplicado: ", ex);
            return false;
        }
        return contador > 0;
    }

    protected List<ParcheSql> obtenerParchesSqlDesdeDirectorio() {
        List<ParcheSql> parches = new ArrayList<>();
        try {
            for (File file : directorioParches.listFiles()) {
                parches.add(new ParcheSql(file));
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Ocurrio un error al buscar los archivos SQL", ex);
        }
        Collections.sort(parches);
        return parches;
    }


}
