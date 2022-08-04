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

package io.dingodb.common.filter;

import java.util.Comparator;

public class DingoValueCompareFilter implements DingoFilter {

    int columnIndex;
    Object value;
    Comparator<Object> comparator;

    public DingoValueCompareFilter(int columnIndex, Object value, Comparator<Object> comparator) {
        this.columnIndex = columnIndex;
        this.value = value;
        this.comparator = comparator;
    }

    @Override
    public boolean filter(FilterContext context, byte[] record) {
        return false;
    }

    @Override
    public void addOrFilter(DingoFilter filter) {

    }

    @Override
    public void addAndFilter(DingoFilter filter) {

    }
}
