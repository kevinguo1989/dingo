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

package io.dingodb.serial.test.io;

import io.dingodb.serial.io.RecordDecoder;
import io.dingodb.serial.io.RecordEncoder;
import io.dingodb.serial.schema.DingoSchema;
import io.dingodb.serial.schema.IntegerSchema;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntEncodeTest {

    @Test
    public void testencode() throws IOException {
        List<DingoSchema> schemas = new ArrayList<>();

        schemas.add(new IntegerSchema(0));

        RecordEncoder re = new RecordEncoder(schemas, (short) 0);
        RecordDecoder de = new RecordDecoder(schemas, (short) 0);
//
//        for (int i = 0; i < 1000; i ++) {
//            byte[] res = re.encode(new Object[]{i});
//
//            System.out.println(i+"|"+Arrays.toString(res));
//        }


        byte[] b127 = re.encode(new Object[]{127});
        byte[] b128 = re.encode(new Object[]{128});

        System.out.println(compare(b127, b128));

        byte[] res = re.encode(new Object[]{100000});
        System.out.println(100000+"|"+ Arrays.toString(res));

        res = re.encode(new Object[]{869971});
        System.out.println(869971+"|"+ Arrays.toString(res));

        byte[] a = new byte[] {1, 0, 0, 1, 0, 13, 70, 83};

        Object[] o = de.decode(a);
        System.out.println(o[0]);


        a = new byte[] {1, 0, 0, 1, 0, 12, -47, 36};

        o = de.decode(a);
        System.out.println(o[0]);
    }


    public static int compare(byte[] bytes1, byte[] bytes2) {
        if (bytes1 == bytes2) {
            return 0;
        }
        int n = Math.min(bytes1.length, bytes2.length);
        for (int i = 0; i < n; i++) {
            if (bytes1[i] == bytes2[i]) {
                continue;
            }
            return (bytes1[i] & 0xFF) - (bytes2[i] & 0xFF);
        }
        return bytes1.length - bytes2.length;
    }
}
