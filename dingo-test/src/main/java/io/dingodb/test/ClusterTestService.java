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

package io.dingodb.test;

import com.google.common.collect.ImmutableList;
import io.dingodb.cluster.ClusterService;
import io.dingodb.common.Location;

import java.security.PrivateKey;
import java.util.List;

public class ClusterTestService implements ClusterService {
    public static final ClusterTestService INSTANCE = new ClusterTestService();

    @Override
    public List<Location> getComputingLocations() {
        PrivateKey privateKey;
        return ImmutableList.of(
            new FakeLocation(0),
            new FakeLocation(1),
            new FakeLocation(2)
        );
    }
}
