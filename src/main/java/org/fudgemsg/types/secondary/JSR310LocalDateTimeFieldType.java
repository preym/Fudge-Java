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
package org.fudgemsg.types.secondary;

import javax.time.calendar.DateTimeProvider;
import javax.time.calendar.LocalDateTime;

import org.fudgemsg.types.FudgeDateTime;
import org.fudgemsg.types.SecondaryFieldTypeBase;
import org.fudgemsg.wire.types.FudgeWireType;

/**
 * Secondary type for JSR-310 object conversion.
 */
public class JSR310LocalDateTimeFieldType extends SecondaryFieldTypeBase<LocalDateTime, DateTimeProvider, FudgeDateTime> {

  /**
   * Singleton instance of the type.
   */
  public static final JSR310LocalDateTimeFieldType INSTANCE = new JSR310LocalDateTimeFieldType();

  /**
   * Restricted constructor.
   */
  private JSR310LocalDateTimeFieldType() {
    super(FudgeWireType.DATETIME, LocalDateTime.class);
  }

  //-------------------------------------------------------------------------
  @Override
  public FudgeDateTime secondaryToPrimary(final LocalDateTime object) {
    return new FudgeDateTime(object);
  }

  @Override
  public LocalDateTime primaryToSecondary(final DateTimeProvider object) {
    return object.toLocalDateTime();
  }

  @Override
  public boolean canConvertPrimary(final Class<? extends DateTimeProvider> clazz) {
    return DateTimeProvider.class.isAssignableFrom(clazz);
  }

}
