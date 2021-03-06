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
package org.fudgemsg.types;

/**
 * An object that represents the presence of the message-level indicator flag.
 * <p>
 * The indicator can be used as a flag in the message, for example to clear a
 * previously set value.
 * <p>
 * This class is a singleton, as only one value is needed of the indicator type.
 */
public enum IndicatorType {

  /**
   * The only instance of this type.
   */
  INSTANCE

}
