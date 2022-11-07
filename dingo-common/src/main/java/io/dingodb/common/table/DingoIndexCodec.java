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

package io.dingodb.common.table;

import io.dingodb.common.codec.Codec;
import io.dingodb.common.codec.DingoCodec;
import io.dingodb.common.codec.KeyValueCodec;
import io.dingodb.common.store.KeyValue;
import io.dingodb.common.type.DingoType;
import io.dingodb.common.type.TupleMapping;
import io.dingodb.common.type.converter.DingoConverter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;

@Slf4j
public class DingoIndexCodec implements KeyValueCodec {

    private final DingoType schema;
    private final DingoType keySchema;
    private final DingoType indexSchema;
    private final TupleMapping keyMapping;
    private final TupleMapping indexMapping;
    private final Codec keyCodec;
    private final Codec indexCodec;
    private final boolean isUnique;

    public DingoIndexCodec(@NonNull DingoType schema, TupleMapping keyMapping, TupleMapping indexMapping, boolean isUnique) {
        this.schema = schema;
        this.keySchema = schema.select(keyMapping);
        this.keyMapping = keyMapping;
        this.indexSchema = schema.select(indexMapping);
        this.indexMapping = indexMapping;
        keyCodec = new DingoCodec(schema.select(keyMapping).toDingoSchemas(), keyMapping, true);
        indexCodec = new DingoCodec(schema.select(indexMapping).toDingoSchemas(), indexMapping, false);
        this.isUnique = isUnique;
    }

    @Override
    public Object[] decode(@NonNull KeyValue keyValue) throws IOException {
        Object[] record = new Object[keyMapping.size() + valueMapping.size()];
        Object[] key = keyCodec.decodeKey(keyValue.getKey());
        Object[] value = valueCodec.decode(keyValue.getValue());
        for (int i = 0; i < key.length; i++) {
            record[keyMapping.get(i)] = key[i];
        }
        for (int i = 0; i < value.length; i++) {
            record[valueMapping.get(i)] = value[i];
        }
        return (Object[]) schema.convertFrom(record, DingoConverter.INSTANCE);
    }

    @Override
    public Object[] decodeKey(byte @NonNull [] bytes) throws IOException {
        return keyCodec.decodeKey(bytes);
    }

    @Override
    public KeyValue encode(Object @NonNull [] tuple) throws IOException {
        Object[] converted = (Object[]) schema.convertTo(tuple, DingoConverter.INSTANCE);
        Object[] key = new Object[keyMapping.size()];
        Object[] value = new Object[valueMapping.size()];
        for (int i = 0; i < keyMapping.size(); i++) {
            key[i] = converted[keyMapping.get(i)];
        }
        for (int i = 0; i < valueMapping.size(); i++) {
            value[i] = converted[valueMapping.get(i)];
        }
        byte[] keyByte = keyCodec.encodeKey(key);
        byte[] valueByte = valueCodec.encode(value);
        return new KeyValue(keyByte, valueByte);
    }

    @Override
    public byte[] encodeKey(Object[] keys) throws IOException {
        Object[] key = (Object[]) keySchema.convertTo(keys, DingoConverter.INSTANCE);
        return keyCodec.encodeKey(key);
    }

    @Override
    public Object[] mapKeyAndDecodeValue(Object @NonNull [] keys, byte[] bytes) throws IOException {
        Object[] value = valueCodec.decode(bytes);
        Object[] record = new Object[keyMapping.size() + valueMapping.size()];
        for (int i = 0; i < keys.length; i++) {
            record[keyMapping.get(i)] = keys[i];
        }
        for (int i = 0; i < value.length; i++) {
            record[valueMapping.get(i)] = value[i];
        }
        return (Object[]) schema.convertFrom(record, DingoConverter.INSTANCE);
    }
}
