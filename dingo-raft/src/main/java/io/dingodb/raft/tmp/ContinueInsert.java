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
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ContinueInsert implements Runnable {

    private Node node;

    public ContinueInsert(Node node) {
        this.node = node;
    }

    @Override
    public void run() {

        long totalTime = 0;
        for (int j = 0; j < 40000; j++) {
            try {
                long start = System.currentTimeMillis();
                for (Integer i = 0; i < 100; i++) {
                    byte[] data = (node.getGroupId() + (j*100+i)).getBytes(StandardCharsets.UTF_8);
                    CompletableFuture future = RaftRawKVOperation.put(data, data).applyOnNode(node);
                    future.get();
                }
                long duration = System.currentTimeMillis() - start;
                totalTime += duration;
                log.info("MulitPart Put NotScan 100 Group [{}] No {} times: [{}], {}", node.getGroupId(), j, duration, totalTime);
            } catch (Exception e) {

            }
        }
    }
}
