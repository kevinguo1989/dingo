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

package io.dingodb.server.executor.api;

import io.dingodb.common.table.Index;
import io.dingodb.common.table.TableDefinition;

import java.util.Map;

public class TableCoordinator {

    public boolean addIndex(String IndexName, Index index) {
        return false;
    }

    public boolean deleteIndex(String indexName) {
        return false;
    }

    public TableDefinition getTableDefinition() {
        return null;
    }

    public String getIndexAddr(String indexName) {
        return "";
    }

    public Map<String, String> getAllIndexAddr() {
        return null;
    }

    public void handlePartHeartbeat() {

    }

    public void start() {

    }

    public void sendHeartbeatToCoordinator() {

    }


}
