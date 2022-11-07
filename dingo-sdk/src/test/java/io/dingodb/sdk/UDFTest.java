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

package io.dingodb.sdk;

import io.dingodb.common.operation.context.BasicContext;
import io.dingodb.common.operation.context.OperationContext;
import io.dingodb.common.table.TableDefinition;
import io.dingodb.sdk.client.DingoClient;

import java.util.Collections;

public class UDFTest {
    public static void main(String[] args) throws Exception {
        DingoClient client
            = new DingoClient("172.20.3.30:19181,172.20.3.30:19182,172.20.3.30:19183");

        client.open();

        String udfName = "udfTest";

        String function = "function test(o) \r\n"
            + "    o[5][2] = 1 \r\n"
            + "    return o \r\n"
            + "end";


        TableDefinition definition = client.getTableDefinition("PERSONS1");
        System.out.println(definition);

        OperationContext context = new BasicContext().definition(client.getTableDefinition("PERSONS1"));

        Integer version = client.registerUDF("PERSONS1", udfName, function);

//        Record record = client.getRecordByUDF("PERSONS1", udfName, "test",
//            version, Collections.singletonList(1));
//
//        System.out.println(record);

        client.updateRecordUsingUDF("PERSONS1", udfName, "test",
            version, Collections.singletonList(1));

        client.close();

    }
}
