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

package io.dingodb.server.executor.store;

import com.google.auto.service.AutoService;

@AutoService(io.dingodb.store.api.StoreServiceProvider.class)
public class StoreServiceProvider implements io.dingodb.store.api.StoreServiceProvider {
    @Override
    public StoreService get() {
        return StoreService.INSTANCE;
    }
}
