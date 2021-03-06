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
package org.fudgemsg;

/**
 * A mutable Fudge message formed from a collection of fields.
 * <p>
 * The Fudge specification is built around messages containing a list of fields.
 * This interface is the high-level representation of the list of fields,
 * with convenient accessors and mutators.
 * <p>
 * Each field may be referenced by a name or by an ordinal.
 * All four combinations are possible - from both present to both absent.
 * Methods provide the ability to lookup a field by both name or ordinal.
 * <p>
 * Applications working with messages should use this interface or
 * {@link FudgeMsg} rather than a concrete class like
 * {@link StandardFudgeMsg} for flexibility.
 * <p>
 * This interface intends implementations to be mutable and not thread-safe.
 */
public interface MutableFudgeMsg extends FudgeMsg {

  /**
   * Adds a field to this container.
   * 
   * @param field  the field to add, not null
   */
  public void add(FudgeField field);

  /**
   * Adds a field to this container with a name, no ordinal, and type determined by the context's type dictionary.
   * 
   * @param name  the name of the field, null for none
   * @param value  the field value, not null
   */
  public void add(String name, Object value);

  /**
   * Adds a field to this container with an ordinal, no name, and type determined by the context's type dictionary.
   * 
   * @param ordinal  the ordinal for the field, null for none
   * @param value  the field value, not null
   */
  public void add(Integer ordinal, Object value);

  /**
   * Adds a field to this container with the given name, ordinal and type determined by the context's type dictionary.
   * 
   * @param name  the name of the field, null for none
   * @param ordinal  the ordinal index for the field, null for none
   * @param value  the field value, not null
   */
  public void add(String name, Integer ordinal, Object value);

  /**
   * Adds a field to this container with the given name, ordinal, and type.
   * 
   * @param name  the name of the field, null for none
   * @param ordinal  the ordinal for the field, null for none
   * @param type  the field type, not null
   * @param value  the field value, not null
   */
  public void add(String name, Integer ordinal, FudgeFieldType type, Object value);

  //-------------------------------------------------------------------------
  /**
   * Adds a sub-message with the given name and ordinal.
   * 
   * @param name  the name of the field, null for none
   * @param ordinal  the ordinal index for the field, null for none
   * @return the created sub-message, not null
   */
  public MutableFudgeMsg addSubMessage(String name, Integer ordinal);

  /**
   * Ensures that a sub-message exists with the given name and ordinal.
   * <p>
   * This creates a sub-message if one does not exist already.
   * 
   * @param name  the name of the field, null for none
   * @param ordinal  the ordinal index for the field, null for none
   * @return the sub-message, either existing or created, not null
   */
  public MutableFudgeMsg ensureSubMessage(String name, Integer ordinal);

  //-------------------------------------------------------------------------
  /**
   * Removes all fields with the given name.
   * 
   * @param name  name of the fields, null matches fields without a name
   */
  public void remove(String name);

  /**
   * Removes all fields with the given ordinal.
   * 
   * @param ordinal ordinal index of fields, null matches fields without an ordinal
   */
  public void remove(Integer ordinal);

  /**
   * Removes all fields matching both the name and ordinal supplied.
   * 
   * @param name  the name of the fields to remove
   * @param ordinal  the ordinal of the fields to remove
   */
  public void remove(String name, Integer ordinal);

  /**
   * Removes all fields from the message.
   */
  public void clear();

}
