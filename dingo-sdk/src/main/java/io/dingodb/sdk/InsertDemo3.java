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

import io.dingodb.sdk.client.DingoClient;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class InsertDemo3 {
    public static void main(String[] args) {
        DingoClient client = new DingoClient("172.20.3.30:19181,172.20.3.30:19182,172.20.3.30:19183");
        client.open();
        List<Object[]> record = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            record.add(new Object[]{i, "1", "1", 1, 1d});
            if (i % 1000 == 0) {
                System.out.println(record.size()+"|"+i);
                log.info("StartInsert|{}|{}", i, System.currentTimeMillis());
                client.insert("PERSONS", record);
                log.info("EndInsert|{}|{}", i, System.currentTimeMillis());
                record = new ArrayList<>();
            }
        }
        client.close();
    }
}
