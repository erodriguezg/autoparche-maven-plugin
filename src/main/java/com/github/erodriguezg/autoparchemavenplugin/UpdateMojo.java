package com.github.erodriguezg.autoparchemavenplugin;

import com.github.erodriguezg.autoparchemavenplugin.executors.ModoExecutor;

/**
 * @author rockman
 */

/**
 * @goal update
 * @phase none
 */
public class UpdateMojo extends AutoparcheMojo {

    @Override
    protected ModoExecutor getModoExecutor() {
        return ModoExecutor.UPDATE;
    }
}
