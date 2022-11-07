/*
 * Copyright 2021 DataCanvas
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

package io.dingodb.sdk.client;

import io.dingodb.common.CommonId;
import io.dingodb.common.table.Index;
import io.dingodb.common.table.TableDefinition;
import io.dingodb.serial.schema.DingoSchema;
import io.dingodb.server.api.ExecutorApi;
import io.dingodb.server.executor.api.TableCoordinator;

import java.util.List;
import java.util.Map;

public class IndexOp {

    MetaClient metaClient;
    TableCoordinator tableCoordinator;

    public boolean insert(String tableName, Object[] values) {

        CommonId id = metaClient.getTableId(tableName);
        TableDefinition tableDefinition = metaClient.getTableDefinition(id);

        Map<String, String> allIndexAddr = tableCoordinator.getAllIndexAddr();

        Map<String, Index> allIndex = tableDefinition.getIndexMap();

        Map<String, List<DingoSchema>> allIndexSchema = tableDefinition.getIndexSchema();

        ExecutorApi keyExecutorApi = null;



        return false;
    }




}
