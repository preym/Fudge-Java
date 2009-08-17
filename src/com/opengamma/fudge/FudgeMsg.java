/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.fudge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

/**
 * A container for {@link FudgeMsgField}s.
 *
 * @author kirk
 */
public class FudgeMsg implements Serializable {
  private final List<FudgeMsgField> _fields = new ArrayList<FudgeMsgField>();
  
  public FudgeMsg() {
  }
  
  public FudgeMsg(FudgeMsg other) {
    if(other == null) {
      throw new NullPointerException("Cannot initialize from a null other FudgeMsg");
    }
    initializeFromByteArray(other.toByteArray());
  }
  
  public FudgeMsg(byte[] byteArray) {
    initializeFromByteArray(byteArray);
  }
  
  protected void initializeFromByteArray(byte[] byteArray) {
    throw new UnsupportedOperationException("To be implemented.");
  }
  
  public void add(FudgeField field) {
    if(field == null) {
      throw new NullPointerException("Cannot add an empty field");
    }
    _fields.add(new FudgeMsgField(field));
  }
  
  public void add(Object value, String name) {
    add(value, name, null);
  }
  
  public void add(Object value, Short ordinal) {
    add(value, null, ordinal);
  }
  
  public void add(Object value, String name, Short ordinal) {
    FudgeFieldType type = determineTypeFromValue(value);
    if(type == null) {
      throw new IllegalArgumentException("Cannot determine a Fudge type for value " + value + " of type " + value.getClass());
    }
    add(type, value, name, ordinal);
  }
  
  public void add(FudgeFieldType type, Object value, String name, Short ordinal) {
    FudgeMsgField field = new FudgeMsgField(type, value, name, ordinal);
    _fields.add(field);
  }
  
  protected FudgeFieldType determineTypeFromValue(Object value) {
    if(value == null) {
      throw new NullPointerException("Cannot determine type for null value.");
    }
    FudgeFieldType type = FudgeTypeDictionary.INSTANCE.getByJavaType(value.getClass());
    return type;
  }
  
  /**
   * Return an <em>unmodifiable</em> list of all the fields in this message, in the index
   * order for those fields.
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<FudgeField> getAllFields() {
    return (List) Collections.unmodifiableList(_fields);
  }
  
  public FudgeField getByIndex(int index) {
    if(index < 0) {
      throw new ArrayIndexOutOfBoundsException("Cannot specify a negative index into a FudgeMsg.");
    }
    if(index >= _fields.size()) {
      return null;
    }
    return _fields.get(index);
  }
  
  // REVIEW kirk 2009-08-16 -- All of these getters are currently extremely unoptimized.
  // There may be an option required if we have a lot of random access to the field content
  // to speed things up by building an index.
  
  public List<FudgeField> getAllByOrdinal(short ordinal) {
    List<FudgeField> fields = new ArrayList<FudgeField>();
    for(FudgeMsgField field : _fields) {
      if((field.getOrdinal() != null) && (ordinal == field.getOrdinal())) {
        fields.add(field);
      }
    }
    return fields;
  }
  
  public FudgeField getByOrdinal(short ordinal) {
    for(FudgeMsgField field : _fields) {
      if((field.getOrdinal() != null) && (ordinal == field.getOrdinal())) {
        return field;
      }
    }
    return null;
  }
  
  public List<FudgeField> getAllByName(String name) {
    List<FudgeField> fields = new ArrayList<FudgeField>();
    for(FudgeMsgField field : _fields) {
      if(ObjectUtils.equals(name, field.getName())) {
        fields.add(field);
      }
    }
    return fields;
  }
  
  public FudgeField getByName(String name) {
    for(FudgeMsgField field : _fields) {
      if(ObjectUtils.equals(name, field.getName())) {
        return field;
      }
    }
    return null;
  }
  
  public byte[] toByteArray() {
    throw new UnsupportedOperationException("To be implemented.");
  }

}