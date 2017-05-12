package com.github.erodriguezg.autoparchemavenplugin.ant;

import java.sql.Connection;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;

/**
 *
 * @author rockman
 */
public class PlSqlAntTask extends SQLExec {

    private Connection con;

    @Override
    public Connection getConnection() {
        return con;
    }

    public PlSqlAntTask(Connection con) {
        this.con = con;
        Project project = new Project();
        project.init();

        setProject(project);
        setTaskType("sql");
        setTaskName("sql");
        setEncoding("UTF-8");

        setDelimiter("/");
        SQLExec.DelimiterType delimiterType = new SQLExec.DelimiterType();
        delimiterType.setValue(SQLExec.DelimiterType.ROW);
        setDelimiterType(delimiterType);
    }
}
