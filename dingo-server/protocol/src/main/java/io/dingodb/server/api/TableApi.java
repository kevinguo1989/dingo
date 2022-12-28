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

package io.dingodb.server.api;

import io.dingodb.common.CommonId;
import io.dingodb.common.Location;
import io.dingodb.common.annotation.ApiDeclaration;
import io.dingodb.common.table.Index;
import io.dingodb.common.table.TableDefinition;
import io.dingodb.common.util.ByteArrayUtils;
import io.dingodb.meta.Part;
import io.dingodb.server.protocol.meta.TablePart;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.CompletableFuture;

public interface TableApi {

    @ApiDeclaration
    CompletableFuture<Boolean> createTable(
        CommonId id, TableDefinition tableDefinition, Map<CommonId, Location> mirrors
    );

    @ApiDeclaration
    CommonId getIndexId(CommonId tableId, String indexName);

    @ApiDeclaration
    CompletableFuture<Void> deleteTable(CommonId id);

    @ApiDeclaration
    TableDefinition getDefinition(CommonId tableId);

    @ApiDeclaration
    List<TablePart> partitions(CommonId tableId);

    @ApiDeclaration
    NavigableMap<ByteArrayUtils.ComparableByteArray, Part> getParts();

    @ApiDeclaration
    CommonId createIndex(CommonId id, Index index);

    @ApiDeclaration
    boolean updateTableDefinition(CommonId id, TableDefinition tableDefinition);

}
