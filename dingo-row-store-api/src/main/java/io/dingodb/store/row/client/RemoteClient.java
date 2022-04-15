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
import io.dingodb.store.row.client.failover.FailoverClosure;
import io.dingodb.store.row.client.failover.RetryCallable;
import io.dingodb.store.row.client.failover.RetryRunner;
import io.dingodb.store.row.client.failover.impl.BoolFailoverFuture;
import io.dingodb.store.row.client.failover.impl.FailoverClosureImpl;
import io.dingodb.store.row.client.failover.impl.MapFailoverFuture;
import io.dingodb.store.row.client.pd.RemotePlacementDriverClient;
import io.dingodb.store.row.cmd.store.BatchPutRequest;
import io.dingodb.store.row.cmd.store.MultiGetRequest;
import io.dingodb.store.row.errors.Errors;
import io.dingodb.store.row.metadata.Cluster;
import io.dingodb.store.row.metadata.Region;
import io.dingodb.store.row.metadata.Store;
import io.dingodb.store.row.options.PlacementDriverOptions;
import io.dingodb.store.row.options.RpcOptions;
import io.dingodb.store.row.storage.KVEntry;
import io.dingodb.store.row.util.ByteArray;
import io.dingodb.store.row.util.Lists;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class RemoteClient {


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
            System.out.println(entry.getKey() + " : " + entry.getValue().getId());
            Region region = entry.getValue();
            region.getRegionEpoch().setVersion(-1);
            System.out.println(region.toString());
            regions.add(entry.getValue());
        }

        //regions = Collections.singletonList(regions.get(0));

        Cluster cluster = pdClient.getMetadataRpcClient().getClusterInfo(0);
        List<Store> stores = cluster.getStores();

        for (Store store : stores) {
            System.out.println(store.getId() + " == " + store.getEndpoint().getPort() + ":"
                + store.getEndpoint().getIp());
            if (store.getRegions() != null) {
                System.out.println("Region Size : " + store.getRegions().size());

                for (Region region : store.getRegions()) {
                    System.out.println(region);
                }
            }
        }


        DingoRowStoreRpcService dingoRowStoreRpcService = new DefaultDingoRowStoreRpcService(pdClient, null) {
            @Override
            public Endpoint getLeader(final String regionId, final boolean forceRefresh, final long timeoutMillis) {
                return super.getLeader(regionId, forceRefresh, timeoutMillis);
            }
        };

        dingoRowStoreRpcService.init(new RpcOptions());

        List<KVEntry> kvEntries = new ArrayList<KVEntry>();
        List<byte[]> keys = new ArrayList<byte[]>();

        for (int i = 17310000; i < 17311000; i ++) {
            byte[] value = (i + "P").getBytes(StandardCharsets.UTF_8);
            KVEntry kvEntry = new KVEntry(value, value);
            kvEntries.add(kvEntry);
            keys.add(value);
        }



        //put-time start

        //FutureGroup<Boolean> futureGroup = internalPut(regions,
        //    kvEntries, 3, null, dingoRowStoreRpcService);

        //put-time1 end

        //for (Future future : futureGroup.futures()) {
        //    future.get();
        //    System.out.println(future.toString());
        //}

        //put-time2 end

        //System.out.println("Get Result");


        //get-time start
        FutureGroup<Map<ByteArray, byte[]>> futureGroupResult = internalMultiGet(keys, false,
            regions, 3, null, dingoRowStoreRpcService);

        int count = 0;

        //get-time1 end
        for (Future<Map<ByteArray, byte[]>> future : futureGroupResult.futures()) {
            Map<ByteArray, byte[]> results = future.get();
            System.out.println(results.size());
            count += results.size();
            for (Map.Entry<ByteArray, byte[]> result : results.entrySet()) {
                System.out.println(new String(result.getKey().getBytes(), StandardCharsets.UTF_8)
                    + ":" + new String(result.getValue(), StandardCharsets.UTF_8));
            }
        }

        //get-time2 end

        System.out.println(count);



        pdClient.shutdown();






    }

    private static FutureGroup<Boolean> internalPut(List<Region> regions, final List<KVEntry> entries,
                                                    final int retriesLeft, final Throwable lastCause,
                                                    DingoRowStoreRpcService dingoRowStoreRpcService) {
        final List<CompletableFuture<Boolean>> futures = Lists.newArrayListWithCapacity(regions.size());
        final Errors lastError = lastCause == null ? null : Errors.forException(lastCause);
        for (Region region : regions) {
            final RetryCallable<Boolean> retryCallable = retryCause -> internalPut(Collections.singletonList(region),
                entries, retriesLeft - 1, retryCause, dingoRowStoreRpcService);
            final BoolFailoverFuture future = new BoolFailoverFuture(retriesLeft, retryCallable);
            internalRegionPut(region, entries, future, retriesLeft, lastError, dingoRowStoreRpcService);
            futures.add(future);
        }
        return new FutureGroup<>(futures);
    }

    private static void internalRegionPut(final Region region, final List<KVEntry> subEntries,
                                          final CompletableFuture<Boolean> future, final int retriesLeft,
                                          final Errors lastCause, DingoRowStoreRpcService dingoRowStoreRpcService) {
        final RetryRunner retryRunner = retryCause -> internalRegionPut(region, subEntries, future,
            retriesLeft - 1, retryCause, dingoRowStoreRpcService);
        final FailoverClosure<Boolean> closure = new FailoverClosureImpl<>(future, false, retriesLeft,
            retryRunner);

        final BatchPutRequest request = new BatchPutRequest();
        request.setKvEntries(subEntries);
        request.setRegionId(region.getId());
        request.setRegionEpoch(region.getRegionEpoch());
        dingoRowStoreRpcService.callAsyncWithRpc(request, closure, lastCause);
    }


    private static FutureGroup<Map<ByteArray, byte[]>> internalMultiGet(final List<byte[]> keys,
                                                                        final boolean readOnlySafe,
                                                                        List<Region> regions,
                                                            final int retriesLeft, final Throwable lastCause,
                                                            DingoRowStoreRpcService dingoRowStoreRpcService) {
        final List<CompletableFuture<Map<ByteArray, byte[]>>> futures = Lists.newArrayListWithCapacity(regions.size());
        final Errors lastError = lastCause == null ? null : Errors.forException(lastCause);
        for (Region region : regions) {
            final RetryCallable<Map<ByteArray, byte[]>> retryCallable = retryCause -> internalMultiGet(keys,
                readOnlySafe, regions, retriesLeft - 1, retryCause, dingoRowStoreRpcService);
            final MapFailoverFuture<ByteArray, byte[]> future = new MapFailoverFuture<>(retriesLeft, retryCallable);
            internalRegionMultiGet(region, keys, readOnlySafe, future, retriesLeft,
                lastError, true, dingoRowStoreRpcService);
            futures.add(future);
        }
        return new FutureGroup<>(futures);
    }

    private static void internalRegionMultiGet(final Region region, final List<byte[]> subKeys,
                                               final boolean readOnlySafe,
                                        final CompletableFuture<Map<ByteArray, byte[]>> future, final int retriesLeft,
                                               final Errors lastCause, final boolean requireLeader,
                                               DingoRowStoreRpcService dingoRowStoreRpcService) {
        // require leader on retry
        final RetryRunner retryRunner = retryCause -> internalRegionMultiGet(region, subKeys, readOnlySafe, future,
            retriesLeft - 1, retryCause, true, dingoRowStoreRpcService);
        final FailoverClosure<Map<ByteArray, byte[]>> closure = new FailoverClosureImpl<>(future,
            false, retriesLeft, retryRunner);
        final MultiGetRequest request = new MultiGetRequest();
        request.setKeys(subKeys);
        request.setReadOnlySafe(readOnlySafe);
        request.setRegionId(region.getId());
        request.setRegionEpoch(region.getRegionEpoch());
        dingoRowStoreRpcService.callAsyncWithRpc(request, closure, lastCause, requireLeader);
    }

}
