package com.github.erodriguezg.autoparchemavenplugin.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by takeda on 13-01-16.
 */
public abstract class DatabaseExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseExecutor.class);

    private String jdbcUrl;

    private String username;

    private String password;

    private String nombreTablaParchesSql;

    public DatabaseExecutor(String jdbcUrl, String username, String password, String nombreTablaParchesSql) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.nombreTablaParchesSql = nombreTablaParchesSql;
    }

    protected Connection getConnection() throws SQLException {
        Connection conn;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.username);
        connectionProps.put("password", this.password);
        conn = DriverManager.getConnection(this.jdbcUrl, connectionProps);
        LOG.debug("call: getConnection()");
        return conn;
    }

    public String getNombreTablaParchesSql() {
        return nombreTablaParchesSql;
    }

    public abstract void execute() throws SQLException;
}
