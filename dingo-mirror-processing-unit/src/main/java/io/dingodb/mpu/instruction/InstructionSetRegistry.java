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

package io.dingodb.mpu.instruction;

import io.dingodb.common.util.Parameters;
import io.dingodb.mpu.core.InternalInstructions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class InstructionSetRegistry {

    private InstructionSetRegistry() {
    }

    private static final Instructions[] instructionSets = new Instructions[Byte.MAX_VALUE];

    public static synchronized void register(int id, Instructions instructions) {
        Parameters.check(id, __ -> id > 32, "The instructions id must great than 32.");
        Parameters.mustNull(instructionSets[id], id + "registered.");
        log.info("Register instructions, id: {}, {}.", id, instructions.getClass().getName());
        instructionSets[id] = instructions;
    }

    public static Instructions instructions(int id) {
        Instructions instructions = instructionSets[id];
        Parameters.nonNull(instructions, "Not found" + id);
        return instructions;
    }

    static {
        instructionSets[EmptyInstructions.id] = EmptyInstructions.INSTRUCTIONS;
        instructionSets[KVInstructions.id] = KVInstructions.INSTRUCTIONS;
        instructionSets[SeqInstructions.id] = SeqInstructions.INSTRUCTIONS;
        instructionSets[InternalInstructions.id] = InternalInstructions.INSTANCE;
    }

}
