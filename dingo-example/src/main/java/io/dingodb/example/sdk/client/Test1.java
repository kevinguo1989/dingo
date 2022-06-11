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

package io.dingodb.example.sdk.client;

import io.dingodb.sdk.client.DingoClient;

public class Test1 {
    public static void main(String[] args) {
        String config = "/Users/kevg/ZetYun/project3/dingo/dingo-dist/conf/client.yaml";

        if (args!=null && args.length >0) {
            config = args[0];
        }

        try {
            DingoClient dingoClient = new DingoClient(config, "PERSONS1");

            for (int i = 0; i < 100000000; i++) {
                Object[] record = new Object[]{i, "k-" + i, "v-" + i};
                dingoClient.insert(record);
                System.out.println("insert " + i);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
