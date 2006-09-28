/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.script;

import be.ordina.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * Interface for an executer of a script, written in some language
 */
public interface ScriptRunner {

    void execute(String script) throws StatementHandlerException;

}