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

package io.dingodb.sdk;

import io.dingodb.sdk.client.DingoClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertDemo2 {
    public static void main(String[] args) {

        String s = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaallllllldjsjhdajhjahfsdhfkshfklshflksfhlskdf" +
            "sdfsdkjhfskhfksjhfkshfklshfoieruopiwud87982hfdsbkjsdhf9283huhsjkdbhfiuwehfkjbdjkf23jhjekfhklshdfkjdhklfhklsdf" +
            "2342sjkdhfhero2893rjlskdnajkahf8934hrjskbafjkshfjdshflksdjhfksjhfksdjfhkldjhfklajhdflksdjhfkjhfkljhfkjhfkdjhfkjdf" +
            "rewrwrwefsfwerwrweryrhgfhdfgfdkjgirjgkldfnglkdfjglkdfjglkdfjgldfkgjdklfjgkldfjglkdfjglkdfjgklfdjgkldfjglkdfjgkldfjg" +
            "gdfgdftregfdfgfdgdgdfffgfdgdfgdfgrrtertergdfgdfgfgfdgfdgdfgfEWEQWEASDewrsfdsfrsgfdgfdkjrljkljgkljgkljfdklgjdfklgjdlfkgjldfgdf" +
            "dfgdfgdfgdfgdfgbvcvbxcvxcvxcvxcvxvxcvxcvxcvsfdsfsdrwerwegsdgfdgfdgfdfsdfsewrwerwerwerwrwrwerwerewrew" +
            "dsfsdfsdgfdvxcvbgftregdfgfdhgbcvbwereklsdjfl;dskjlsogj;po[]o[p;jlkuitujghjj";



        DingoClient client = new DingoClient("172.20.3.30:19181,172.20.3.30:19182,172.20.3.30:19183");
        client.open();
        for (int i = 0; i < 10000000; i++) {
            log.info("StartInsert|{}|{}", i, System.currentTimeMillis());
            client.insert("PERSONS", new Object[]{i, s, s, 1, 1d});
            log.info("EndInsert|{}|{}", i, System.currentTimeMillis());
        }
        client.close();
    }
}
