/*
 * Copyright 2001-2024 Artima, Inc.
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
package org.scalatest.examples.refspec.oneinstancepertest

import org.scalatest._
import refspec.RefSpec
import collection.mutable.ListBuffer

class ExampleSpec extends RefSpec with OneInstancePerTest {

  val builder = new StringBuilder("ScalaTest is ")
  val buffer = new ListBuffer[String]

  object `Testing ` {
    def `should be easy` {
      builder.append("easy!")
      assert(builder.toString === "ScalaTest is easy!")
      assert(buffer.isEmpty)
      buffer += "sweet"
    }

    def `should be fun` {
      builder.append("fun!")
      assert(builder.toString === "ScalaTest is fun!")
      assert(buffer.isEmpty)
    } 
  }
}
