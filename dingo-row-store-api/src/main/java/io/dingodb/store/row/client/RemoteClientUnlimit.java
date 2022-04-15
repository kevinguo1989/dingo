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

import io.dingodb.raft.util.Endpoint;
import io.dingodb.store.row.client.pd.RemotePlacementDriverClient;
import io.dingodb.store.row.metadata.Region;
import io.dingodb.store.row.options.PlacementDriverOptions;
import io.dingodb.store.row.options.RpcOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class RemoteClientUnlimit {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteClientUnlimit.class);

    public static void main(String[] args) throws Exception {

        PlacementDriverOptions pdOpts = new PlacementDriverOptions();
        pdOpts.setInitialPdServerList("172.20.61.4:9181,172.20.61.5:9181,172.20.61.6:9181");
        pdOpts.setPdGroupId("COORDINATOR_RAFT");

        RemotePlacementDriverClient pdClient = new RemotePlacementDriverClient(0, "dingo");

        pdClient.init(pdOpts);

        Endpoint pdLeader = pdClient.getPdLeader(true, 1000);

        System.out.println(pdLeader.getIp() + ":" + pdLeader.getPort());

        Map<String, Region> regionMap = pdClient.getRegionRouteTable().getRegionTable();
        System.out.println("regionMap Size = " + regionMap.size());
        List<Region> regions = new ArrayList<Region>();
        for (Map.Entry<String, Region> entry : regionMap.entrySet()) {
            Region region = entry.getValue();
            region.getRegionEpoch().setVersion(-1);
            System.out.println(region.toString());
            regions.add(entry.getValue());
        }

        DingoRowStoreRpcService dingoRowStoreRpcService = new DefaultDingoRowStoreRpcService(pdClient, null) {
            @Override
            public Endpoint getLeader(final String regionId, final boolean forceRefresh, final long timeoutMillis) {
                return super.getLeader(regionId, forceRefresh, timeoutMillis);
            }
        };

        dingoRowStoreRpcService.init(new RpcOptions());

        int times = 500;
        int batchSize = 10000;

        List<TestResult> results = new ArrayList<TestResult>();

        if (false) {
            //Put no get  SingleThread
            if (false) {
                RemotePutNoGetThread putNoGet = new RemotePutNoGetThread(dingoRowStoreRpcService,
                    Collections.singletonList(regions.get(0)), times, batchSize);

                TestResult result1 = new TestResult();
                result1.setTestType("SinglePut");
                result1.setThreadId("SinglePut");
                result1.setThreadSize(1);
                result1.setTimes(times);
                result1.setBatchSize(batchSize);
                result1.setTotalGap(putNoGet.call());
                results.add(result1);
            }

            if (true) {
                //Put future get  SingleThread
                RemotePutGetThread putGet = new RemotePutGetThread(dingoRowStoreRpcService,
                    Collections.singletonList(regions.get(0)), times, batchSize);

                TestResult result2 = new TestResult();
                result2.setTestType("SinglePutGet");
                result2.setThreadId("SinglePutGet");
                result2.setThreadSize(1);
                result2.setTimes(times);
                result2.setBatchSize(batchSize);
                result2.setTotalGap(putGet.call());
                results.add(result2);
            }

            if (true) {
                //GET  SingleThread
                RemoteGetThread get = new RemoteGetThread(dingoRowStoreRpcService,
                    Collections.singletonList(regions.get(0)), times, batchSize);

                TestResult result3 = new TestResult();
                result3.setTestType("SingleGet");
                result3.setThreadId("SingleGet");
                result3.setThreadSize(1);
                result3.setTimes(times);
                result3.setBatchSize(batchSize);
                result3.setTotalGap(get.call());
                results.add(result3);
            }
        }

        if (true) {
            //Put no get  MulitThread
            List<FutureTask<Long>> futures = new ArrayList<FutureTask<Long>>();

            if (false) {
                for (Region region : regions) {
                    RemotePutNoGetThread remoteThread = new RemotePutNoGetThread(dingoRowStoreRpcService,
                        Collections.singletonList(region), times, batchSize);
                    FutureTask<Long> future = new FutureTask<Long>(remoteThread);
                    futures.add(future);
                    new Thread(future).start();
                }

                futures.forEach(future -> {
                    try {
                        TestResult result = new TestResult();
                        result.setTestType("Put");
                        result.setThreadId(future.toString());
                        result.setThreadSize(regions.size());
                        result.setTimes(times);
                        result.setBatchSize(batchSize);
                        result.setTotalGap(future.get());
                        results.add(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            }


            if (false) {
                //Put future get  MulitThread
                futures = new ArrayList<FutureTask<Long>>();

                for (Region region : regions) {
                    RemotePutGetThread remoteThread
                        = new RemotePutGetThread(dingoRowStoreRpcService,
                        Collections.singletonList(region), times, batchSize);
                    FutureTask<Long> future = new FutureTask<Long>(remoteThread);
                    futures.add(future);
                    new Thread(future).start();
                }

                futures.forEach(future -> {
                    try {
                        TestResult result = new TestResult();
                        result.setTestType("PutGet");
                        result.setThreadId(future.toString());
                        result.setThreadSize(regions.size());
                        result.setTimes(times);
                        result.setBatchSize(batchSize);
                        result.setTotalGap(future.get());
                        results.add(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            }


            if (true) {
                //GET  MulitThread
                futures = new ArrayList<FutureTask<Long>>();

                for (Region region : regions) {
                    RemoteGetThread remoteThread
                        = new RemoteGetThread(dingoRowStoreRpcService,
                        Collections.singletonList(region), times, batchSize);
                    FutureTask<Long> future = new FutureTask<Long>(remoteThread);
                    futures.add(future);
                    new Thread(future).start();
                }

                futures.forEach(future -> {
                    try {
                        TestResult result = new TestResult();
                        result.setTestType("GET");
                        result.setThreadId(future.toString());
                        result.setThreadSize(regions.size());
                        result.setTimes(times);
                        result.setBatchSize(batchSize);
                        result.setTotalGap(future.get());
                        results.add(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        pdClient.shutdown();


        for (TestResult result : results) {
            System.out.println(result);
            LOG.info(result.toString());
        }
    }
}
