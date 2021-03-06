/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and other contributors.
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
package org.fudgemsg.wire.types;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The wire type definition for a double.
 */
final class DoubleWireType extends FudgeWireType {

  /**
   * Standard Fudge field type: double.
   * See {@link FudgeWireType#DOUBLE_TYPE_ID}.
   */
  public static final DoubleWireType INSTANCE = new DoubleWireType();

  /**
   * Restricted constructor.
   */
  private DoubleWireType() {
    super(FudgeWireType.DOUBLE_TYPE_ID, Double.TYPE, 8);
  }

  //-------------------------------------------------------------------------
  @Override
  public Double readValue(DataInput input, int dataSize) throws IOException {
    return input.readDouble();
  }

  @Override
  public void writeValue(DataOutput output, Object value) throws IOException {
    output.writeDouble((Double) value);
  }

}
