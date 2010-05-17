/*
 * Copyright Unitils.org
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
package org.unitils.dataset.loader.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.DataSetRow;
import org.unitils.dataset.core.DataSetRowProcessor;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.loader.DataSetLoader;
import org.unitils.dataset.util.DatabaseAccessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseDataSetLoader implements DataSetLoader {

    protected DataSetRowProcessor dataSetRowProcessor;
    protected DatabaseAccessor databaseAccessor;


    public void init(DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor) {
        this.dataSetRowProcessor = dataSetRowProcessor;
        this.databaseAccessor = databaseAccessor;
    }


    public void load(DataSetRowSource dataSetRowSource, List<String> variables) {
        DataSetRow dataSetRow;
        while ((dataSetRow = dataSetRowSource.getNextDataSetRow()) != null) {
            loadDataSetRow(dataSetRow, variables);
        }
    }


    protected int loadDataSetRow(DataSetRow dataSetRow, List<String> variables) {
        try {
            DatabaseRow databaseRow = processDataSetRow(dataSetRow, variables);
            if (databaseRow.isEmpty()) {
                return 0;
            }
            return loadDatabaseRow(databaseRow);

        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set row: " + dataSetRow + ", variables: " + variables, e);
        }
    }

    protected DatabaseRow processDataSetRow(DataSetRow dataSetRow, List<String> variables) throws Exception {
        Set<String> unusedPrimaryKeyColumnNames = new HashSet<String>();
        DatabaseRow databaseRow = dataSetRowProcessor.process(dataSetRow, variables, unusedPrimaryKeyColumnNames);
        if (!unusedPrimaryKeyColumnNames.isEmpty()) {
            handleUnusedPrimaryKeyColumns(unusedPrimaryKeyColumnNames);
        }
        return databaseRow;
    }


    protected void handleUnusedPrimaryKeyColumns(Set<String> unusedPrimaryKeyColumnNames) {
        // nothing to do
    }

    protected abstract int loadDatabaseRow(DatabaseRow databaseRow) throws Exception;
}