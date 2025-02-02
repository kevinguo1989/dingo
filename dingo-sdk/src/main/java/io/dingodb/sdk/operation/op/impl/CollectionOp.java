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

package io.dingodb.sdk.operation.op.impl;

import io.dingodb.common.CommonId;
import io.dingodb.common.util.Parameters;
import io.dingodb.sdk.operation.Column;
import io.dingodb.sdk.operation.context.Context;
import io.dingodb.sdk.operation.executive.collection.CollAddExec;
import io.dingodb.sdk.operation.executive.collection.DistinctListExec;
import io.dingodb.sdk.operation.executive.collection.FilterExec;
import io.dingodb.sdk.operation.executive.collection.ListExec;
import io.dingodb.sdk.operation.executive.collection.SortListExec;
import io.dingodb.sdk.operation.executive.value.AvgExec;
import io.dingodb.sdk.operation.executive.value.CountExec;
import io.dingodb.sdk.operation.executive.value.DecreaseCountExec;
import io.dingodb.sdk.operation.executive.value.IncreaseCountExec;
import io.dingodb.sdk.operation.executive.value.MaxContinuousDecreaseCountExec;
import io.dingodb.sdk.operation.executive.value.MaxContinuousIncreaseCountExec;
import io.dingodb.sdk.operation.executive.value.MaxDecreaseCountExec;
import io.dingodb.sdk.operation.executive.value.MaxExec;
import io.dingodb.sdk.operation.executive.value.MaxIncreaseCountExec;
import io.dingodb.sdk.operation.executive.value.MinExec;
import io.dingodb.sdk.operation.executive.value.SumExec;
import io.dingodb.sdk.operation.filter.DingoFilter;
import io.dingodb.sdk.operation.op.Op;

public class CollectionOp extends AbstractOp {

    public CollectionOp(CommonId execId, Context context) {
        super(execId, context);
    }

    public CollectionOp(CommonId execId, Context context, Op head) {
        super(execId, context, head);
    }

    public ValueOp sum(Column column) {
        next = new ValueOp(
            SumExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp max(Column column) {
        next = new ValueOp(
            MaxExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp min(Column column) {
        next = new ValueOp(
            MinExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp avg(Column column) {
        next = new ValueOp(
            AvgExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp count(Column column) {
        next = new ValueOp(
            CountExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp decreaseCount(Column column) {
        next = new ValueOp(
            DecreaseCountExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp increaseCount(Column column) {
        next = new ValueOp(
            IncreaseCountExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp maxDecreaseCount(Column column) {
        next = new ValueOp(
            MaxDecreaseCountExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp maxIncreaseCount(Column column) {
        next = new ValueOp(
            MaxIncreaseCountExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp maxContinuousDecreaseCount(Column column) {
        next = new ValueOp(
            MaxContinuousDecreaseCountExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public ValueOp maxContinuousIncreaseCount(Column column) {
        next = new ValueOp(
            MaxContinuousIncreaseCountExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (ValueOp) next;
    }

    public CollectionOp add(Column[] columns, boolean useDefaultWhenNotExisted) {
        next = new CollectionOp(
            CollAddExec.COMMON_ID,
            Context.builder().column(columns).useDefaultWhenNotExisted(useDefaultWhenNotExisted).build(),
            assignIdent(false));
        return (CollectionOp) next;
    }

    public CollectionOp sortList(Column column) {
        return sortList(column, true);
    }

    public CollectionOp sortList(Column column, boolean desc) {
        next = new MultiValueOp(
            SortListExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).desc(desc).build(),
            Parameters.cleanNull(head, this));
        return (MultiValueOp) next;
    }

    public CollectionOp list(Column column) {
        next = new MultiValueOp(
            ListExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (MultiValueOp) next;
    }

    public CollectionOp distinctList(Column column) {
        next = new MultiValueOp(
            DistinctListExec.COMMON_ID,
            Context.builder().column(new Column[]{column}).build(),
            Parameters.cleanNull(head, this));
        return (MultiValueOp) next;
    }

    public CollectionOp filter(DingoFilter filter) {
        next = new CollectionOp(
            FilterExec.COMMON_ID,
            Context.builder().filter(filter).build(),
            Parameters.cleanNull(head, this));
        return (CollectionOp) next;
    }
}
