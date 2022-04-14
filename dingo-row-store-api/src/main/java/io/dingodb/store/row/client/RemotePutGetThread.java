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

package io.dingodb.store.row.client;

import io.dingodb.store.row.metadata.Region;
import io.dingodb.store.row.storage.KVEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RemotePutGetThread implements Callable<Long> {

    private static final Logger LOG = LoggerFactory.getLogger(RemotePutGetThread.class);

    private RegionOp regionOp;
    private DingoRowStoreRpcService dingoRowStoreRpcService;
    private List<Region> regions;
    private int times;
    private int batchSize;

    public RemotePutGetThread(DingoRowStoreRpcService dingoRowStoreRpcService,
                              List<Region> regions, int times, int batchSize) {
        this.dingoRowStoreRpcService = dingoRowStoreRpcService;
        this.regions = regions;
        this.regionOp = new RegionOp();
        this.times = times;
        this.batchSize = batchSize;
    }

    @Override
    public Long call() {
        int times = 0;
        try {
            long totalGap = 0;
            for (; times < this.times; times++) {
                int putCount = times * this.batchSize;
                List<KVEntry> kvEntries = new ArrayList<KVEntry>();

                for (int i = putCount; i < putCount + this.batchSize; i++) {
                    byte[] value = (i + "MPG").getBytes(StandardCharsets.UTF_8);
                    KVEntry kvEntry = new KVEntry(value, value);
                    kvEntries.add(kvEntry);
                }

                long startPutTime = System.currentTimeMillis();

                FutureGroup<Boolean> futureGroup = regionOp.internalPut(regions,
                    kvEntries, 3, null, dingoRowStoreRpcService);

                for (Future future : futureGroup.futures()) {
                    future.get(3, TimeUnit.SECONDS);
                }

                long endPutTime = System.currentTimeMillis();

                long gap = endPutTime - startPutTime;

                totalGap += gap;

                LOG.info("FutureGet Put time " + times + " Gap = " + gap + "  TotalGap = " + totalGap);
            }
            return totalGap;
        } catch (Exception e) {
            LOG.error("FutureGet Put time " + times + " " + e.toString());
        }
        return 0L;
    }
}
