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

package io.dingodb.server.coordinator.meta.adaptor.impl;

import com.google.auto.service.AutoService;
import io.dingodb.common.CommonId;
import io.dingodb.server.coordinator.meta.Constant;
import io.dingodb.server.coordinator.meta.adaptor.Adaptor;
import io.dingodb.server.protocol.meta.Schema;

import static io.dingodb.server.protocol.CommonIdConstant.ID_TYPE;
import static io.dingodb.server.protocol.CommonIdConstant.TABLE_IDENTIFIER;

@AutoService(Adaptor.class)
public class SchemaAdaptor extends BaseAdaptor<Schema> {

    public static final CommonId META_ID = CommonId.prefix(ID_TYPE.table, TABLE_IDENTIFIER.schema);

    @Override
    public Class<Schema> adaptFor() {
        return Schema.class;
    }

    @Override
    public CommonId metaId() {
        return META_ID;
    }

    @Override
    protected CommonId newId(Schema schema) {
        return new CommonId(
            META_ID.type(),
            META_ID.identifier(),
            schema.getParent().seq,
            metaStore().generateSeq(META_ID.encode(), Constant.SCHEMA_SEQ_START)
        );
    }

}
