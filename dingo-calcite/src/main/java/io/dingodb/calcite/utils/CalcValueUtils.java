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

package io.dingodb.calcite.utils;

import io.dingodb.calcite.visitor.RexConverter;
import io.dingodb.common.type.DingoType;
import io.dingodb.common.type.converter.ExprConverter;
import io.dingodb.exec.expr.SqlExprCompileContext;
import io.dingodb.exec.expr.SqlExprEvalContext;
import io.dingodb.expr.parser.Expr;
import io.dingodb.expr.parser.exception.DingoExprCompileException;
import io.dingodb.expr.runtime.EvalEnv;
import io.dingodb.expr.runtime.exception.FailGetEvaluator;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rex.RexNode;

import java.util.List;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CalcValueUtils {
    private CalcValueUtils() {
    }

    @Nullable
    public static Object calcValue(
        RexNode rexNode,
        @Nonnull DingoType targetType,
        Object[] tuple,
        DingoType tupleType,
        @Nullable EvalEnv env
    ) throws DingoExprCompileException, FailGetEvaluator {
        Expr expr = RexConverter.convert(rexNode);
        SqlExprEvalContext etx = new SqlExprEvalContext(env);
        etx.setTuple(tuple);
        return targetType.convertFrom(
            expr.compileIn(new SqlExprCompileContext(tupleType, null, env)).eval(etx),
            ExprConverter.INSTANCE
        );
    }

    @Nonnull
    public static Object[] calcValues(
        @Nonnull List<RexNode> rexNodeList,
        @Nonnull DingoType targetType,
        Object[] tuple,
        DingoType tupleType,
        @Nullable EvalEnv env
    ) throws DingoExprCompileException, FailGetEvaluator {
        int size = rexNodeList.size();
        Object[] result = new Object[size];
        for (int i = 0; i < size; ++i) {
            result[i] = calcValue(rexNodeList.get(i), targetType.getChild(i), tuple, tupleType, env);
        }
        return result;
    }

    @Nonnull
    public static EvalEnv getEnv(@Nonnull RelOptRuleCall call) {
        EvalEnv env = new EvalEnv();
        env.setTimeZone(call.getPlanner().getContext().unwrap(TimeZone.class));
        return env;
    }
}
