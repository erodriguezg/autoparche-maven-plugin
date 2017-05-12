package com.github.erodriguezg.autoparchemavenplugin;

import com.github.erodriguezg.autoparchemavenplugin.executors.ModoExecutor;

/**
 * @author rockman
 */
/**
 * @goal drop-create
 * @phase none
 */
public class DropCreateMojo extends AutoparcheMojo {

    @Override
    protected ModoExecutor getModoExecutor() {
        return ModoExecutor.DROP_CREATE;
    }
}
