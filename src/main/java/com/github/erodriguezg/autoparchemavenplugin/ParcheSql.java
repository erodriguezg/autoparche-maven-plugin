package com.github.erodriguezg.autoparchemavenplugin;

import java.io.File;

/**
 * @author rockman
 */
public class ParcheSql implements Comparable<ParcheSql> {

    private String nombre;
    private File file;

    public ParcheSql(File file) {
        this.nombre = file.getName();
        this.file = file;
    }

    public ParcheSql(File file, String fileName) {
        this.nombre = fileName;
        this.file = file;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public int compareTo(ParcheSql parcheSql) {
        return this.getNombre().compareToIgnoreCase(parcheSql.getNombre());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParcheSql parcheSql = (ParcheSql) o;
        return nombre != null ? nombre.equals(parcheSql.nombre) : parcheSql.nombre == null;
    }

    @Override
    public int hashCode() {
        return nombre != null ? nombre.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ParcheSql{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
