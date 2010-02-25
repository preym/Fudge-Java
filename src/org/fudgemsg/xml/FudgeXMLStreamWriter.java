/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc. and other contributors.
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

package org.fudgemsg.xml;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeField;
import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.FudgeFieldType;
import org.fudgemsg.FudgeRuntimeException;
import org.fudgemsg.FudgeSize;
import org.fudgemsg.FudgeStreamWriter;
import org.fudgemsg.FudgeTypeDictionary;
import org.fudgemsg.taxon.FudgeTaxonomy;

/**
 * <p>Implementation of a {@link FudgeStreamWriter} that writes XML to a text stream. This can be
 * used for XML output, or can be used to assist in developing/debugging a streaming serializer
 * without having to inspect the binary output from a FudgeDataOutputStreamWriter.</p>
 * 
 * <p>This code should adhere to the <a href="http://wiki.fudgemsg.org/display/FDG/XML+Fudge+Messages">XML Fudge Message specification</a>.</p>
 * 
 * @author Andrew Griffin
 */
public class FudgeXMLStreamWriter extends FudgeXMLSettings implements FudgeStreamWriter {
  
  private final FudgeContext _fudgeContext;
  private final XMLStreamWriter _writer;
  private FudgeTaxonomy _taxonomy = null;
  private int _taxonomyId = 0;
  private int _messageSizeToWrite = 0;
  
  /**
   * Creates a new {@link FudgeXMLStreamWriter} for writing to the target XML device.
   * 
   * @param fudgeContext the {@link FudgeContext}
   * @param writer the underlying {@link Writer}
   * @throws XMLStreamException if the XML subsystem can't create a stream wrapper for {@code Writer}
   */
  public FudgeXMLStreamWriter (final FudgeContext fudgeContext, final Writer writer) throws XMLStreamException {
    this (fudgeContext, XMLOutputFactory.newInstance ().createXMLStreamWriter (writer));
  }
  
  public FudgeXMLStreamWriter (final FudgeXMLSettings settings, final FudgeContext fudgeContext, final Writer writer) throws XMLStreamException {
    this (settings, fudgeContext, XMLOutputFactory.newInstance ().createXMLStreamWriter (writer));
  }
  
  /**
   * Creates a new {@link FudgeXMLStreamWriter} for writing a Fudge stream to an {@link XMLStreamWriter}.
   * 
   * @param fudgeContext the {@link FudgeContext}
   * @param writer the underlying {@link Writer}
   */
  public FudgeXMLStreamWriter (final FudgeContext fudgeContext, final XMLStreamWriter writer) {
    _fudgeContext = fudgeContext;
    _writer = writer;
  }
  
  public FudgeXMLStreamWriter (final FudgeXMLSettings settings, final FudgeContext fudgeContext, final XMLStreamWriter writer) {
    super (settings);
    _fudgeContext = fudgeContext;
    _writer = writer;
  }
  
  /**
   * Returns the underlying {@link XMLStreamWriter}.
   * 
   * @return the {@code XMLStreamWriter}
   */
  protected XMLStreamWriter getWriter () {
    return _writer;
  }
  
  /**
   * @param operation the operation being attempted when the exception was caught
   * @param e the exception caught
   * @throws IOException if the triggered {@link XMLStreamException} was caused by an {@link IOException}
   */
  protected void wrapException (final String operation, XMLStreamException e) throws IOException {
    if (e.getCause () instanceof IOException) {
      throw (IOException)e.getCause ();
    } else {
      throw new FudgeRuntimeException ("Couldn't " + operation + " XML stream", e);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void close () throws IOException {
    try {
      getWriter ().close ();
    } catch (XMLStreamException e) {
      wrapException ("close", e);
    }
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public void flush () throws IOException {
    try {
      getWriter ().flush ();
    } catch (XMLStreamException e) {
      wrapException ("flush", e);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public FudgeContext getFudgeContext() {
    return _fudgeContext;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public FudgeTaxonomy getCurrentTaxonomy() {
    return _taxonomy;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCurrentTaxonomyId (final int taxonomyId) {
    _taxonomyId = taxonomyId;
    if(getFudgeContext().getTaxonomyResolver() != null) {
      FudgeTaxonomy taxonomy = getFudgeContext().getTaxonomyResolver().resolveTaxonomy((short)taxonomyId);
      _taxonomy = taxonomy;
    } else {
      _taxonomy = null;
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int getCurrentTaxonomyId () {
    return _taxonomyId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int writeEnvelopeHeader(
      int processingDirectives,
      int schemaVersion,
      int messageSize) throws IOException {
    try {
      _messageSizeToWrite = messageSize - 8; // the size passed in includes the 8 byte Fudge envelope header
      getWriter ().writeStartDocument ();
      if (getEnvelopeElementName () != null) {
        getWriter ().writeStartElement (getEnvelopeElementName ());
        if ((processingDirectives != 0) && (getEnvelopeAttributeProcessingDirectives () != null)) {
          getWriter ().writeAttribute (getEnvelopeAttributeProcessingDirectives (), Integer.toString (processingDirectives));
        }
        if ((schemaVersion != 0) && (getEnvelopeAttributeSchemaVersion () != null)) {
          getWriter ().writeAttribute (getEnvelopeAttributeSchemaVersion (), Integer.toString (schemaVersion));
        }
      }
    } catch (XMLStreamException e) {
      wrapException ("write message envelope header to", e);
    }
    return 8;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int writeFields(FudgeFieldContainer msg) throws IOException {
    int nWritten = 0;
    for(FudgeField field : msg.getAllFields()) {
      nWritten += writeField(field);
    }
    return nWritten;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int writeField (FudgeField field) throws IOException {
    if (field == null) {
      throw new NullPointerException ("Cannot write a null field to a Fudge stream");
    }
    return writeField (field.getOrdinal (), field.getName (), field.getType (), field.getValue ());
  }
  
  /**
   * Remove any invalid characters to leave an XML element name.
   */
  private String convertFieldName (String str) {
    /*
     * nameStartChar :=  ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF]
     *                | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF]
     *                | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
     * nameChar := nameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
     */
    if (str == null) return null;
    final StringBuilder sb = new StringBuilder ();
    boolean firstChar = true;
    for (int i = 0; i < str.length (); i++) {
      final char c = str.charAt (i);
      if ((c == ':')
       || ((c >= 'A') && (c <= 'Z'))
       || (c == '_')
       || ((c >= 'a') && (c <= 'z'))
       || ((c >= 0xC0) && (c <= 0xD6))
       || ((c >= 0xD8) && (c <= 0xF6))
       || ((c >= 0xF8) && (c <= 0x2FF))
       || ((c >= 0x370) && (c <= 0x37D))
       || ((c >= 0x37F) && (c <= 0x1FFF))
       || ((c >= 0x200C) && (c <= 0x200D))
       || ((c >= 0x2070) && (c <= 0x2FEF))
       || ((c >= 0x3001) && (c <= 0xD7FF))
       || ((c >= 0xF900) && (c <= 0xFDCF))
       || ((c >= 0xFDF0) && (c <= 0xFFFD))
       || ((c >= 0x10000) && (c <= 0xEFFFF))) {
        firstChar = false;
        sb.append (c);
      } else if (!firstChar) {
        if ((c == '-')
         || (c == '.')
         || ((c >= '0') && (c <= '9'))
         || (c == 0xB7)
         || ((c >= 0x300) && (c <= 0x36F))
         || ((c >= 0x203F) && (c <= 0x2040))) {
          sb.append (c);
        }
      }
    }
    return (sb.length () > 0) ? sb.toString () : null;
  }
  
  private void writeArray (final byte[] array) throws XMLStreamException {
    boolean first = true;
    for (byte value : array) {
      if (first) first = false; else getWriter ().writeCharacters (",");
      getWriter ().writeCharacters (Byte.toString (value));
    }
  }
  
  private void writeArray (final short[] array) throws XMLStreamException {
    boolean first = true;
    for (short value : array) {
      if (first) first = false; else getWriter ().writeCharacters (",");
      getWriter ().writeCharacters (Short.toString (value));
    }
  }
  
  private void writeArray (final int[] array) throws XMLStreamException {
    boolean first = true;
    for (int value : array) {
      if (first) first = false; else getWriter ().writeCharacters (",");
      getWriter ().writeCharacters (Integer.toString (value));
    }
  }
  
  private void writeArray (final long[] array) throws XMLStreamException {
    boolean first = true;
    for (long value : array) {
      if (first) first = false; else getWriter ().writeCharacters (",");
      getWriter ().writeCharacters (Long.toString (value));
    }
  }
  
  private void writeArray (final float[] array) throws XMLStreamException {
    boolean first = true;
    for (float value : array) {
      if (first) first = false; else getWriter ().writeCharacters (",");
      getWriter ().writeCharacters (Float.toString (value));
    }
  }
  
  private void writeArray (final double[] array) throws XMLStreamException {
    boolean first = true;
    for (double value : array) {
      if (first) first = false; else getWriter ().writeCharacters (",");
      getWriter ().writeCharacters (Double.toString (value));
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public int writeField(
      Short ordinal,
      String name,
      FudgeFieldType type,
      Object fieldValue) throws IOException {
    if (_messageSizeToWrite <= 0) throw new FudgeRuntimeException ("too many fields already written");
    final int fudgeMessageBytes = FudgeSize.calculateFieldSize (getCurrentTaxonomy (), ordinal, name, type, fieldValue);
    try {
      String ename = null;
      if (getPreserveFieldNames ()) {
        ename = convertFieldName (name);
      }
      if (ename == null) {
        if (ordinal != null) {
          if (getCurrentTaxonomy () != null) {
            ename = convertFieldName (getCurrentTaxonomy ().getFieldName (ordinal));
          }
        }
        if (ename == null) {
          ename = getFieldElementName ();
          if ((ename != null) && (ordinal != null) && getAppendFieldOrdinal ()) {
            ename = ename + ordinal;
          }
        }
      }
      if (ename != null) {
        getWriter ().writeStartElement (ename);
        if ((ordinal != null) && (getFieldAttributeOrdinal () != null)) {
          getWriter ().writeAttribute (getFieldAttributeOrdinal (), ordinal.toString ());
        }
        if ((name != null) && !name.equals (ename) && (getFieldAttributeName () != null)) {
          getWriter ().writeAttribute (getFieldAttributeName (), name);
        }
        if (getFieldAttributeType () != null) {
          final String typeString = fudgeTypeIdToString (type.getTypeId ());
          if (typeString != null) {
            getWriter ().writeAttribute (getFieldAttributeType (), typeString);
          }
        }
        switch (type.getTypeId ()) {
        case FudgeTypeDictionary.INDICATOR_TYPE_ID :
          // no content
          break;
        case FudgeTypeDictionary.FUDGE_MSG_TYPE_ID :
          // the value returned from writeFields will have already been deducted from messageSizeToWrite so add it back on
          final int n = writeFields ((FudgeFieldContainer)fieldValue);
          _messageSizeToWrite += n;
          break;
        case FudgeTypeDictionary.BOOLEAN_TYPE_ID:
          getWriter ().writeCharacters ((Boolean)fieldValue ? getBooleanTrue () : getBooleanFalse ());
          break;
        case FudgeTypeDictionary.BYTE_TYPE_ID:
        case FudgeTypeDictionary.SHORT_TYPE_ID:
        case FudgeTypeDictionary.INT_TYPE_ID:
        case FudgeTypeDictionary.LONG_TYPE_ID:
        case FudgeTypeDictionary.FLOAT_TYPE_ID:
        case FudgeTypeDictionary.DOUBLE_TYPE_ID:
        case FudgeTypeDictionary.STRING_TYPE_ID:
        case FudgeTypeDictionary.DATE_TYPE_ID:
        case FudgeTypeDictionary.TIME_TYPE_ID:
        case FudgeTypeDictionary.DATETIME_TYPE_ID:
          getWriter ().writeCharacters (fieldValue.toString ());
          break;
        case FudgeTypeDictionary.BYTE_ARRAY_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_4_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_8_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_16_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_20_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_32_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_64_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_128_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_256_TYPE_ID:
        case FudgeTypeDictionary.BYTE_ARR_512_TYPE_ID:
          writeArray ((byte[])fieldValue);
          break;
        case FudgeTypeDictionary.SHORT_ARRAY_TYPE_ID:
          writeArray ((short[])fieldValue);
          break;
        case FudgeTypeDictionary.INT_ARRAY_TYPE_ID:
          writeArray ((int[])fieldValue);
          break;
        case FudgeTypeDictionary.LONG_ARRAY_TYPE_ID:
          writeArray ((long[])fieldValue);
          break;
        case FudgeTypeDictionary.FLOAT_ARRAY_TYPE_ID:
          writeArray ((float[])fieldValue);
          break;
        case FudgeTypeDictionary.DOUBLE_ARRAY_TYPE_ID:
          writeArray ((double[])fieldValue);
          break;
        default :
          if (getBase64UnknownTypes ()) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream (fudgeMessageBytes);
            final DataOutputStream dos = new DataOutputStream (new Base64OutputStream (baos));
            type.writeValue (dos, fieldValue);
            dos.close ();
            if (getFieldAttributeEncoding () != null) {
              getWriter ().writeAttribute (getFieldAttributeEncoding (), "base64");
            }
            getWriter ().writeCharacters (new String (baos.toByteArray ()));
          } else {
            getWriter ().writeCharacters (fieldValue.toString ());
          }
          break;
        }
        getWriter ().writeEndElement ();
      }
      _messageSizeToWrite -= fudgeMessageBytes;
      if (_messageSizeToWrite < 0) throw new FudgeRuntimeException ("field data overflow");
      if (_messageSizeToWrite == 0) {
        if (getEnvelopeElementName () != null) {
          getWriter ().writeEndElement (); // envelope
        }
        getWriter ().writeEndDocument ();
      }
    } catch (XMLStreamException e) {
      wrapException ("write field to", e);
    }
    return fudgeMessageBytes;
  }
      
}