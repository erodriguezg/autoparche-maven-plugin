package com.github.erodriguezg.autoparchemavenplugin;

import com.github.erodriguezg.autoparchemavenplugin.executors.ModoExecutor;
import com.github.erodriguezg.autoparchemavenplugin.executors.ParcheSqlExecutor;
import com.github.erodriguezg.autoparchemavenplugin.executors.ParcheSqlSinRegistroExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.sql.SQLException;

/**
 * @goal autoparche
 * @phase none
 */
public class AutoparcheMojo extends AbstractMojo {

    /**
     * @parameter required="false" expression="${autoparche.skip}" default-value="true"
     */
    protected Boolean skip;

    /**
     * @parameter required="true" expression="${autoparche.driver}"
     */
    protected String driver;

    /**
     * @parameter required="true" expression="${autoparche.jdbcUrl}"
     */
    protected String jdbcUrl;

    /**
     * @parameter required="true" expression="${autoparche.username}"
     */
    protected String username;

    /**
     * @parameter required="true" expression="${autoparche.password}"
     */
    protected String password;

    /**
     * @parameter required="true" expression="${autoparche.tablaParches}" default-value="sql_parche"
     */
    protected String tablaParches;

    /**
     * @parameter required="false" expression="${autoparche.runTestSql}" default-value="false"
     */
    protected Boolean runTestSql;

    /**
     * @parameter required="true" expression="${autoparche.sqlDir}"
     */
    protected File sqlDir;

    /**
     * @parameter required="true" expression="${autoparche.dropSqlDir}"
     */
    protected File dropSqlDir;

    /**
     * @parameter required="false" expression="${autoparche.testSqlDir}"
     */
    protected File testSqlDir;

    /**
     * @parameter required="false" expression="${autoparche.modo}" default-value="UPDATE"
     */
    private String modo;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("--------------INICIANDO AUTOPARCHE-----------------");
        if (!skip) {
            try {
                cargarDriver();
                ejecutarParchesSql();
            } catch (Exception ex) {
                throw new MojoExecutionException("Error al ejecutar autoparche", ex);
            }
        } else {
            getLog().info("SKIP TRUE: No se ejecuta nada!");
        }
        getLog().info("--------------FINALIZADO AUTOPARCHE-----------------");
    }

    private void cargarDriver() throws ClassNotFoundException {
        Class.forName(driver);
    }

    private void ejecutarParchesSql() throws SQLException {
        if (getModoExecutor() == ModoExecutor.DROP_CREATE) {
            ejecutarParchesSql("DROP", new ParcheSqlSinRegistroExecutor(jdbcUrl, username, password, tablaParches, dropSqlDir));
        }
        ejecutarParchesSql("SQL", new ParcheSqlExecutor(jdbcUrl, username, password, tablaParches, sqlDir));
        if (runTestSql && testSqlDir != null) {
            ejecutarParchesSql("TEST", new ParcheSqlSinRegistroExecutor(jdbcUrl, username, password, tablaParches, testSqlDir));
        }
    }

    public void ejecutarParchesSql(String nombreTipoParches, ParcheSqlExecutor executor) throws SQLException {
        getLog().info("PARCHES " + nombreTipoParches + ": comenzando!");
        executor.execute();
        getLog().info("PARCHES " + nombreTipoParches + ": finalizado!");
    }

    protected ModoExecutor getModoExecutor() {
        return ModoExecutor.valueOf(modo);
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRunTestSql(Boolean runTestSql) {
        this.runTestSql = runTestSql;
    }

    public void setSqlDir(File sqlDir) {
        this.sqlDir = sqlDir;
    }

    public void setDropSqlDir(File dropSqlDir) {
        this.dropSqlDir = dropSqlDir;
    }

    public void setTestSqlDir(File testSqlDir) {
        this.testSqlDir = testSqlDir;
    }

    public String getTablaParches() {
        return tablaParches;
    }

    public void setTablaParches(String tablaParches) {
        this.tablaParches = tablaParches;
    }
}