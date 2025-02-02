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

package io.dingodb.sdk.operation.filter.impl;

import io.dingodb.sdk.operation.filter.AbstractDingoFilter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class DingoGtFilter extends AbstractDingoFilter {

    private int index;
    private BigDecimal value;

    public DingoGtFilter(Number value) {
        this.value = new BigDecimal(value.toString());
    }

    public DingoGtFilter(int index, Number value) {
        this.index = index;
        this.value = new BigDecimal(value.toString());
    }

    @Override
    public boolean filter(Object[] record) {
        return isGreaterThan(record[index]);
    }

    @Override
    public boolean filter(Object record) {
        return isGreaterThan(record);
    }

    private boolean isGreaterThan(Object record) {
        if (record == null) {
            log.warn("Current input value is null.");
            return false;
        }
        BigDecimal currentValue = new BigDecimal(record.toString());

        return currentValue.compareTo(value) > 0;
    }
}
