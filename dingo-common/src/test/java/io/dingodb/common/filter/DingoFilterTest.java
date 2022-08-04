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

import io.dingodb.common.codec.DingoCodec;
import io.dingodb.serial.schema.DingoSchema;
import io.dingodb.serial.schema.IntegerSchema;
import io.dingodb.serial.schema.StringSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DingoFilterTest {


    @Test
    public void testFilter() throws IOException {

        FilterContext context = new FilterContext() {
            DingoCodec codec = null;
            @Override
            public DingoCodec getCodec() {
                if (codec == null) {
                    List<DingoSchema> dingoSchemas = new ArrayList<>();
                    dingoSchemas.add(new IntegerSchema(0));
                    dingoSchemas.add(new StringSchema(1, 0));
                    dingoSchemas.add(new StringSchema(2, 0));
                    dingoSchemas.add(new IntegerSchema(3));
                    codec = new DingoCodec(dingoSchemas);
                }
                return codec;
            }
        };


        DingoFilter root = new DingoFilterImpl();
        DingoFilter equalsFilter = new DingoValueEqualsFilter(new int[]{3}, new Object[]{1});
        root.addAndFilter(equalsFilter);

        Object[] record = new Object[] {1, "a1", "a2", 1};
        byte[] recordb = context.getCodec().encode(record);
        Assertions.assertEquals(equalsFilter.filter(context, recordb), true);

        record = new Object[] {1, "a1", "a2", 2};
        recordb = context.getCodec().encode(record);
        Assertions.assertEquals(equalsFilter.filter(context, recordb), false);

        DingoFilter equalsFilter2 = new DingoValueEqualsFilter(new int[]{1}, new Object[]{"a1"});
        root.addAndFilter(equalsFilter2);

        record = new Object[] {1, "a1", "a2", 1};
        recordb = context.getCodec().encode(record);
        Assertions.assertEquals(root.filter(context, recordb), true);

        record = new Object[] {1, "a2", "a2", 1};
        recordb = context.getCodec().encode(record);
        Assertions.assertEquals(root.filter(context, recordb), false);

        root = new DingoFilterImpl();
        root.addOrFilter(equalsFilter);
        root.addOrFilter(equalsFilter2);

        Assertions.assertEquals(root.filter(context, recordb), true);

        record = new Object[] {1, "a2", "a2", 2};
        recordb = context.getCodec().encode(record);
        Assertions.assertEquals(root.filter(context, recordb), false);
    }
}
