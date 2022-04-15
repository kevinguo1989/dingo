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
import io.dingodb.store.row.util.ByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class RemoteGetThread implements Callable<Long> {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteGetThread.class);

    private RegionOp regionOp;
    private DingoRowStoreRpcService dingoRowStoreRpcService;
    private List<Region> regions;
    private int times = 0;
    private int batchSize = 0;

    public RemoteGetThread(DingoRowStoreRpcService dingoRowStoreRpcService,
                           List<Region> regions, int times, int batchSize) {
        this.dingoRowStoreRpcService = dingoRowStoreRpcService;
        this.regions = regions;
        this.regionOp = new RegionOp();
        this.times = times;
        this.batchSize = batchSize;
    }

    @Override
    public Long call() {
        //List<String> resultList = new ArrayList<>();
        int times = 0;
        try {
            long totalGap = 0;
            for (; times < this.times; times++) {
                int getCount = times * this.batchSize;
                List<byte[]> keys = new ArrayList<byte[]>();

                for (int i = getCount; i < getCount + this.batchSize; i++) {
                    byte[] value = (i + "MPG").getBytes(StandardCharsets.UTF_8);
                    keys.add(value);
                }

                long startGetTime = System.currentTimeMillis();

                FutureGroup<Map<ByteArray, byte[]>> futureGroupResult = regionOp.internalMultiGet(keys, false,
                    regions, 3, null, dingoRowStoreRpcService);

                for (Future<Map<ByteArray, byte[]>> future : futureGroupResult.futures()) {
                    //for (Map.Entry<ByteArray, byte[]> results : future.get().entrySet()) {
                    //    resultList.add(new String(results.getValue(), StandardCharsets.UTF_8));
                    //}
                    future.get();
                }

                long endGetTime = System.currentTimeMillis();

                long gap = endGetTime - startGetTime;

                totalGap += gap;

                LOG.info("Get time " + times + " Gap = " + gap + "  TotalGap = " + totalGap);
            }
            //Collections.sort(resultList);
            //LOG.info("Result Size = {}; first = {}, last = {}, thread = {}", resultList.size(), resultList.get(0),
            //    resultList.get(resultList.size() - 1), Thread.currentThread().getName());
            return totalGap;
        } catch (Exception e) {
            LOG.error("Get time " + times + " " + e.toString());
        }
        return 0L;
    }

    private int compareList(List<byte[]> list1, List<byte[]> list2) {
        Iterator<byte[]> it1 = list1.listIterator();
        Iterator<byte[]> it2 = list2.listIterator();

        Comparator<byte[]> comp = new Comparator<byte[]>() {
            @Override
            public int compare(byte[] o1, byte[] o2) {
                int len1 = o1.length;
                int len2 = o2.length;
                int lim = Math.min(len1, len2);

                int k = 0;
                while (k < lim) {
                    byte c1 = o1[k];
                    byte c2 = o2[k];
                    if (c1 != c2) {
                        return c1 - c2;
                    }
                    k++;
                }
                return len1 - len2;
            }
        };

        //Collections.sort(list1, comp);
        //Collections.sort(list2, comp);

        while (it1.hasNext()) {
            byte[] b1 = it1.next();
            while (it2.hasNext()) {
                byte[] b2 = it2.next();
                //System.out.println(new String(b1, StandardCharsets.UTF_8)
                // + ":" + new String(b2, StandardCharsets.UTF_8));
                if (Arrays.equals(b1, b2)) {
                    //System.out.println("TRUE");
                    it1.remove();
                    it2.remove();
                    //System.out.println(list1.size() + " | " + list2.size());
                    break;
                }
            }
            it2 = list2.listIterator();
        }
        return list1.size();
    }
}
