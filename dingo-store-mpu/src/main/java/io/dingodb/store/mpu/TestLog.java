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

package io.dingodb.store.mpu;

import io.dingodb.common.CommonId;
import io.dingodb.common.Location;
import io.dingodb.common.store.Part;
import io.dingodb.common.util.ByteArrayUtils;
import io.dingodb.common.util.FileUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestLog {

    public static void main(String[] args) {

        String path1 = "/tmp/testlog/p1";
        String path2 = "/tmp/testlog/p2";
        String path3 = "/tmp/testlog/p3";


        FileUtils.deleteIfExists(Paths.get(path1));
        FileUtils.deleteIfExists(Paths.get(path2));
        FileUtils.deleteIfExists(Paths.get(path3));
        FileUtils.createDirectories(Paths.get(path1));
        FileUtils.createDirectories(Paths.get(path2));
        FileUtils.createDirectories(Paths.get(path3));

        CommonId id1 = CommonId.prefix((byte) 'T', new byte[]{1, 1}, new byte[] {1,2,3,4});
        CommonId id2 = CommonId.prefix((byte) 'T', new byte[]{1, 2}, new byte[] {1,2,3,4});
        CommonId id3 = CommonId.prefix((byte) 'T', new byte[]{1, 3}, new byte[] {1,2,3,4});

        StoreInstance storeInstance1 = new StoreInstance(id1, Paths.get(path1));
        StoreInstance storeInstance2 = new StoreInstance(id1, Paths.get(path2));
        StoreInstance storeInstance3 = new StoreInstance(id1, Paths.get(path3));

        List<Location> locations = new ArrayList<>();
        locations.add(new Location("localhost", 12800));
        locations.add(new Location("localhost", 12801));
        locations.add(new Location("localhost", 12802));
        List<CommonId> ids = new ArrayList<>();
        ids.add(id1);
        ids.add(id2);
        ids.add(id3);

        Part part1 = Part.builder()
            .id(id1)
            .start(ByteArrayUtils.EMPTY_BYTES)
            .replicateLocations(locations)
            .replicateId(id1)
            .replicates(ids)
            .leader(id1)
            .leaderLocation(locations.get(0))
            .build();
        storeInstance1.assignPart(part1);


        Part part2 = Part.builder()
            .id(id2)
            .start(ByteArrayUtils.EMPTY_BYTES)
            .replicateLocations(locations)
            .replicateId(id2)
            .replicates(ids)
            .leader(id2)
            .leaderLocation(locations.get(1))
            .build();
        storeInstance2.assignPart(part2);


        Part part3 = Part.builder()
            .id(id3)
            .start(ByteArrayUtils.EMPTY_BYTES)
            .replicateLocations(locations)
            .replicateId(id3)
            .replicates(ids)
            .leader(id3)
            .leaderLocation(locations.get(2))
            .build();
        storeInstance3.assignPart(part3);


        storeInstance1.upsertKeyValue("1".getBytes(), "value01".getBytes());




        storeInstance1.destroy();
        storeInstance2.destroy();
        storeInstance3.destroy();
        FileUtils.deleteIfExists(Paths.get(path1));
        FileUtils.deleteIfExists(Paths.get(path2));
        FileUtils.deleteIfExists(Paths.get(path3));

    }




}
