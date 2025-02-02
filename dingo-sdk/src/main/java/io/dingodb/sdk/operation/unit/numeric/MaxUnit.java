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

package io.dingodb.sdk.operation.unit.numeric;

import io.dingodb.sdk.operation.number.ComputeNumber;

public class MaxUnit<N extends ComputeNumber<N>> extends NumberUnit<N, MaxUnit<N>> {

    public MaxUnit() {
        super(null, 0L);
    }

    public MaxUnit(N value) {
        super(value, 1L);
    }

    public MaxUnit(N value, long count) {
        super(value, count);
    }

    @Override
    public MaxUnit<N> merge(MaxUnit<N> that) {
        if (that == null) {
            return this;
        }
        if (getClass().equals(that.getClass())) {
            MaxUnit<N> maxUnit = that;
            count.add(maxUnit.count);
            setValue(ComputeNumber.max(value, maxUnit.value));
        }
        return this;
    }

    @Override
    public MaxUnit<N> fastClone() {
        return new MaxUnit<>(value.fastClone(), count.value());
    }
}
