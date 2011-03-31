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

package org.fudgemsg.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify that this is a Fudge builder.
 * <p>
 * This annotation is used at runtime to specify that a particular class is a
 * {@link FudgeMessageBuilder} or {@link FudgeObjectBuilder} for a particular
 * data type. This is similar to {@link HasFudgeBuilder}, but allows the data
 * object to specify what its builder(s) are, in a case where a builder has been
 * written external to a source data type.
 * <p>
 * The Fudge system can, if desired, locate this annotation and automatically
 * configure using {@link FudgeObjectDictionary#addAllAnnotatedBuilders()}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FudgeBuilderFor {

  /**
   * The class for which the annotated type is a builder.
   */
  Class<?> value();

}
