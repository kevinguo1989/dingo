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

package io.dingodb.sdk.operation.op.impl;

import io.dingodb.common.CommonId;
import io.dingodb.sdk.operation.context.Context;
import io.dingodb.sdk.operation.op.Op;

public class MultiValueOp extends CollectionOp {

    public MultiValueOp(CommonId execId, Context context) {
        super(execId, context);
    }

    public MultiValueOp(CommonId execId, Context context, Op head) {
        super(execId, context, head);
    }
}
