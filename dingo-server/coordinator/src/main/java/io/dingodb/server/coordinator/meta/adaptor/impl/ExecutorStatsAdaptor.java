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

package io.dingodb.server.coordinator.meta.adaptor.impl;

import com.google.auto.service.AutoService;
import io.dingodb.common.CommonId;
import io.dingodb.server.coordinator.meta.adaptor.StatsAdaptor;
import io.dingodb.server.protocol.meta.ExecutorStats;

import static io.dingodb.server.protocol.CommonIdConstant.ID_TYPE;
import static io.dingodb.server.protocol.CommonIdConstant.STATS_IDENTIFIER;

@AutoService(StatsAdaptor.class)
public class ExecutorStatsAdaptor extends BaseStatsAdaptor<ExecutorStats> {

    public static final CommonId META_ID = CommonId.prefix(ID_TYPE.stats, STATS_IDENTIFIER.executor);

    @Override
    public Class<ExecutorStats> adaptFor() {
        return ExecutorStats.class;
    }

    @Override
    public CommonId statsId() {
        return META_ID;
    }

}
