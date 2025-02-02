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

package io.dingodb.strorage.column;

import java.util.Objects;

/**
 * Represents the status returned by a function call in RocksDB.
 *
 * Currently only used with {@link ColumnStorageException} when the
 * status is not {@link Code#Ok}
 */
public class Status {
  private final Code code;
  /* @Nullable */ private final SubCode subCode;
  /* @Nullable */ private final String state;

  public Status(final Code code, final SubCode subCode, final String state) {
    this.code = code;
    this.subCode = subCode;
    this.state = state;
  }

  /**
   * Intentionally private as this will be called from JNI
   */
  private Status(final byte code, final byte subCode, final String state) {
    this.code = Code.getCode(code);
    this.subCode = SubCode.getSubCode(subCode);
    this.state = state;
  }

  public Code getCode() {
    return code;
  }

  public SubCode getSubCode() {
    return subCode;
  }

  public String getState() {
    return state;
  }

  public String getCodeString() {
    final StringBuilder builder = new StringBuilder()
        .append(code.name());
    if(subCode != null && subCode != SubCode.None) {
      builder.append("(")
          .append(subCode.name())
          .append(")");
    }
    return builder.toString();
  }

  // should stay in sync with /include/rocksdb/status.h:Code and /java/rocksjni/portal.h:toJavaStatusCode
  public enum Code {
    Ok(                 (byte)0x0),
    NotFound(           (byte)0x1),
    Corruption(         (byte)0x2),
    NotSupported(       (byte)0x3),
    InvalidArgument(    (byte)0x4),
    IOError(            (byte)0x5),
    MergeInProgress(    (byte)0x6),
    Incomplete(         (byte)0x7),
    ShutdownInProgress( (byte)0x8),
    TimedOut(           (byte)0x9),
    Aborted(            (byte)0xA),
    Busy(               (byte)0xB),
    Expired(            (byte)0xC),
    TryAgain(           (byte)0xD),
    Undefined(          (byte)0x7F);

    private final byte value;

    Code(final byte value) {
      this.value = value;
    }

    public static Code getCode(final byte value) {
      for (final Code code : Code.values()) {
        if (code.value == value){
          return code;
        }
      }
      throw new IllegalArgumentException(
          "Illegal value provided for Code (" + value + ").");
    }

    /**
     * Returns the byte value of the enumerations value.
     *
     * @return byte representation
     */
    public byte getValue() {
      return value;
    }
  }

  // should stay in sync with /include/rocksdb/status.h:SubCode and /java/rocksjni/portal.h:toJavaStatusSubCode
  public enum SubCode {
    None(         (byte)0x0),
    MutexTimeout( (byte)0x1),
    LockTimeout(  (byte)0x2),
    LockLimit(    (byte)0x3),
    NoSpace(      (byte)0x4),
    Deadlock(     (byte)0x5),
    StaleFile(    (byte)0x6),
    MemoryLimit(  (byte)0x7),
    Undefined(    (byte)0x7F);

    private final byte value;

    SubCode(final byte value) {
      this.value = value;
    }

    public static SubCode getSubCode(final byte value) {
      for (final SubCode subCode : SubCode.values()) {
        if (subCode.value == value){
          return subCode;
        }
      }
      throw new IllegalArgumentException(
          "Illegal value provided for SubCode (" + value + ").");
    }

    /**
     * Returns the byte value of the enumerations value.
     *
     * @return byte representation
     */
    public byte getValue() {
      return value;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Status status = (Status) o;
    return code == status.code && subCode == status.subCode && Objects.equals(state, status.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, subCode, state);
  }
}
