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
import io.dingodb.common.codec.KeyValueCodec;
import io.dingodb.common.store.KeyValue;
import io.dingodb.common.table.TableDefinition;
import io.dingodb.sdk.operation.StoreOperationUtils;
import io.dingodb.server.api.ExecutorApi;

public class TestUDF {
    public static void main(String[] args) throws Exception {
        DingoConnection connection = new DingoConnection(
            "127.0.0.1:19181,127.0.0.1:19182,127.0.0.1:19183");
        connection.initConnection();
        StoreOperationUtils utils =  new StoreOperationUtils(connection, 1);
        RouteTable routeTable = utils
            .getAndRefreshRouteTable("PERSONS",true);

        KeyValueCodec keyValueCodec = routeTable.getCodec();
        int key = 2;
        byte[] primaryKey = keyValueCodec.encodeKey(new Object[]{key});

//
        ExecutorApi executorApi = utils.getExecutor("PERSONS", primaryKey);
//
        MetaClient metaClient = connection.getMetaClient();
        CommonId tableId = metaClient.getTableId("PERSONS");
//        TableDefinition tableDefinition = metaClient.getTableDefinition(tableId);
//        executorApi.initTableDefinition(tableId, tableDefinition);
//        String luajFunction = "function test(o) \r\n" +
//            "    o[3] = o[3] + 5 \r\n" +
//            "    return o \r\n" +
//            "end";
//        executorApi.addLuaFunction(tableId, luajFunction);
//        KeyValue keyValue = executorApi.getKeyValueByUDF(tableId, "test", primaryKey);
//        Object[] result = keyValueCodec.decode(keyValue);
//        System.out.println(result[0]);
//        System.out.println(result[1]);
//        System.out.println(result[2]);
//        System.out.println(result[3]);
//        System.out.println(result[4]);
        executorApi.updateKeyValueByUDF(tableId, "test", primaryKey);
    }
}
