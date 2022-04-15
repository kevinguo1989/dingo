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

import io.dingodb.store.row.client.failover.FailoverClosure;
import io.dingodb.store.row.client.failover.RetryCallable;
import io.dingodb.store.row.client.failover.RetryRunner;
import io.dingodb.store.row.client.failover.impl.BoolFailoverFuture;
import io.dingodb.store.row.client.failover.impl.FailoverClosureImpl;
import io.dingodb.store.row.client.failover.impl.MapFailoverFuture;
import io.dingodb.store.row.cmd.store.BatchPutRequest;
import io.dingodb.store.row.cmd.store.MultiGetRequest;
import io.dingodb.store.row.errors.Errors;
import io.dingodb.store.row.metadata.Region;
import io.dingodb.store.row.storage.KVEntry;
import io.dingodb.store.row.util.ByteArray;
import io.dingodb.store.row.util.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RegionOp {

    public FutureGroup<Boolean> internalPut(List<Region> regions, final List<KVEntry> entries,
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

    public void internalRegionPut(final Region region, final List<KVEntry> subEntries,
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


    public FutureGroup<Map<ByteArray, byte[]>> internalMultiGet(final List<byte[]> keys,
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

    public void internalRegionMultiGet(final Region region, final List<byte[]> subKeys,
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
