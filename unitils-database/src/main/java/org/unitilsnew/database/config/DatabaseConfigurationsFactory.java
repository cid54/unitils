/*
 * Copyright 2012,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.unitilsnew.database.config;

import org.unitils.core.UnitilsException;
import org.unitilsnew.core.Factory;
import org.unitilsnew.core.config.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tim Ducheyne
 */
public class DatabaseConfigurationsFactory implements Factory<DatabaseConfigurations> {

    protected Configuration configuration;


    public DatabaseConfigurationsFactory(Configuration configuration) {
        this.configuration = configuration;
    }


    public DatabaseConfigurations create() {
        DatabaseConfiguration defaultDatabaseConfiguration = null;
        Map<String, DatabaseConfiguration> databaseConfigurations = new HashMap<String, DatabaseConfiguration>(3);

        List<String> databaseNames = configuration.getOptionalStringList("database.names");
        for (String databaseName : databaseNames) {
            boolean defaultDatabase = defaultDatabaseConfiguration == null;
            DatabaseConfiguration databaseConfiguration = createDatabaseConfiguration(databaseName, defaultDatabase);

            databaseConfigurations.put(databaseName, databaseConfiguration);
            if (defaultDatabase) {
                defaultDatabaseConfiguration = databaseConfiguration;
            }
        }
        if (defaultDatabaseConfiguration == null) {
            defaultDatabaseConfiguration = createDatabaseConfiguration(null, true);
        }
        return new DatabaseConfigurations(defaultDatabaseConfiguration, databaseConfigurations);
    }


    // todo validate here???
    protected DatabaseConfiguration createDatabaseConfiguration(String databaseName, boolean defaultDatabase) {
        String driverClassName = configuration.getOptionalString("database.driverClassName", databaseName);
        String url = configuration.getOptionalString("database.url", databaseName);
        String userName = configuration.getOptionalString("database.userName", databaseName);
        String password = configuration.getOptionalString("database.password", databaseName);
        String dialect = configuration.getOptionalString("database.dialect", databaseName);
        List<String> schemaNames = configuration.getOptionalStringList("database.schemaNames", databaseName);
        Boolean updateDisabled = configuration.getOptionalBoolean("database.updateDisabled", databaseName);

        String defaultSchemaName = schemaNames.isEmpty() ? null : schemaNames.get(0);
        boolean disabled = updateDisabled == null ? false : updateDisabled;
        if (disabled && defaultDatabase) {
            // todo exception message
            throw new UnitilsException("Default database cannot be disabled.");
        }
        return new DatabaseConfiguration(databaseName, dialect, driverClassName, url, userName, password, defaultSchemaName, schemaNames, disabled, defaultDatabase);
    }
}