package io.dingodb.index;

import io.dingodb.common.codec.DingoIndexKeyValueCodec;
import io.dingodb.common.codec.DingoKeyValueCodec;
import io.dingodb.common.codec.KeyValueCodec;
import io.dingodb.common.store.KeyValue;
import io.dingodb.common.table.TableDefinition;
import io.dingodb.index.api.ExecutorServerApi;
import io.dingodb.index.api.TableCoordinatorServerApi;

import java.util.HashMap;
import java.util.Map;

public class DingoIndexDataExecutor {

    public void executeInsert(String tableName, Object[] record) throws Exception {
        // 1. 获取该Table对应的tableCoordinator服务地址
        // 2. 使用tableCoordinatorAddr初始化tableCoordinatorServerApi，优先使用缓存
        // 3. 获取TableDefinition
        TableDefinition tableDefinition = null;
        // 4. 序列化主键索引及其他索引数据

        KeyValueCodec codec = new DingoKeyValueCodec(tableDefinition.getDingoType(), tableDefinition.getKeyMapping());
        KeyValue keyValue = codec.encode(record);
        Map<String, DingoIndexKeyValueCodec> indexCodecs = getDingoIndexCodec(tableDefinition);
        Map<String, KeyValue> indexKeyValues = new HashMap<>();
        for (String indexName : indexCodecs.keySet()) {
            DingoIndexKeyValueCodec indexCodec = indexCodecs.get(indexName);
            KeyValue IndexKeyValue = indexCodec.encode(record);
            indexKeyValues.put(indexName, IndexKeyValue);
        }

        // 5. 获取所有索引包含主键的executor地址及ExecutorServerApi
        String executorAddr = tableCoordinatorServerApi.getExecutorAddr(tableName);
        Map<String, String> allIndexExecutorAddr = tableCoordinatorServerApi.getAllIndexExecutorAddr(tableName);

        ExecutorServerApi executorServerApi = null;
        Map<String, ExecutorServerApi> allIndexExecutorServerApi = new HashMap<>();

        // 6. 插入未完成主键数据
        executorServerApi.insertUnfinishedRecord(keyValue);

        // 7. 插入所有索引数据
        for (String indexName : allIndexExecutorServerApi.keySet()) {
            allIndexExecutorServerApi.get(indexName).insertIndex(allIndexKeyValue.get(indexName));
        }

        // 8. 插入完成主键数据
        executorServerApi.insertFinishedRecord(keyValue.getKey(), tableDefinition.getVersion());

        // 9. 删除未完成主键数据
        executorServerApi.deleteUnfinishedRecord(keyValue.getKey());
    }


    private Map<String, DingoIndexKeyValueCodec> getDingoIndexCodec(TableDefinition tableDefinition) {

        Map<String, DingoIndexKeyValueCodec> indicsCodec = new HashMap<>();

        tableDefinition.getIndexesMapping().forEach((k, v) -> {
            DingoIndexKeyValueCodec indexCodec = new DingoIndexKeyValueCodec(tableDefinition.getDingoType(),
                tableDefinition.getKeyMapping(), v, tableDefinition.getIndexes().get(k).isUnique());
            indicsCodec.put(k, indexCodec);
        });

        return indicsCodec;
    }
}
