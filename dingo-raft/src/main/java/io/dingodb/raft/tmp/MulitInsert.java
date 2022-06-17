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

package io.dingodb.raft.tmp;

import io.dingodb.raft.Node;

import java.util.concurrent.CompletableFuture;

public class MulitInsert implements Runnable {

    private Node node;

    public MulitInsert(Node node) {
        this.node = node;
    }

    @Override
    public void run() {

        for (int j = 0; j < 1000; j++) {
            try {
                long start = System.currentTimeMillis();
                for (Integer i = 0; i < 10; i++) {
                    byte b = i.byteValue();
                    CompletableFuture future = RaftRawKVOperation.put(new byte[]{b}, new byte[]{b}).applyOnNode(node);
                    future.get();
                }
                System.out.println("mulit put 10 times: " + (System.currentTimeMillis() - start));
            } catch (Exception e) {

            }
        }
    }
}
