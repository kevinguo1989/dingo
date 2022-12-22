package io.dingodb.index;

import io.dingodb.common.table.Index;
import io.dingodb.common.table.IndexStatus;
import io.dingodb.common.table.TableDefinition;

public class DingoIndexExecutor {

    public void executeAddIndex(String tableName, Index index) throws Exception {
        // 1. 获取TableDefinition
        TableDefinition tableDefinition = null;

        // 2. 创建新raftgroup

        // 3. 更新TableDefinition，将index状态改为new，并提交到raft
        index.setStatus(IndexStatus.NEW);
        tableDefinition.addIndex(index);


        // 4. 插入数据

    }
}
