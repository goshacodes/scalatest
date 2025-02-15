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
package org.scalatest

import SharedHelpers.thisLineNumber
import exceptions.TestFailedException
import org.scalactic.Prettifier
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

class ShouldBeEmptyStructuralSpec extends AnyFunSpec {

  private val prettifier = Prettifier.default
  
  val fileName: String = "ShouldBeEmptyStructuralSpec.scala"
    
  def wasNotEmpty(left: Any): String = 
    FailureMessages.wasNotEmpty(prettifier, left)
    
  def wasEmpty(left: Any): String = 
    FailureMessages.wasEmpty(prettifier, left)
  
  describe("empty matcher") {
    
    describe("when work with arbitrary object with isEmpty() method") {
      
      class MyEmptiness(value: Boolean) {
        def isEmpty(): Boolean = value
        override def toString = "emptiness"
      }
      val objTrue = new MyEmptiness(true)
      val objFalse = new MyEmptiness(false)
      
      it("should do nothing for 'objTrue should be (empty)'") {
        objTrue should be (empty)
      }
      
      it("should throw TFE with correct stack depth for 'objFalse should be (empty)'") {
        val caught1 = intercept[TestFailedException] {
          objFalse should be (empty)
        }
        assert(caught1.message === Some(wasNotEmpty(objFalse)))
        assert(caught1.failedCodeFileName === Some(fileName))
        assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      }
      
      it("should do nothing if for 'objFalse should not be empty'") {
        objFalse should not be empty
      }
      
      it("should throw TFE with correct stack depth for 'objTrue should not be empty'") {
        val caught1 = intercept[TestFailedException] {
          objTrue should not be empty
        }
        assert(caught1.message === Some(wasEmpty(objTrue)))
        assert(caught1.failedCodeFileName === Some(fileName))
        assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      }
    }
    
    describe("when work with arbitrary object with isEmpty method") {
      
      class MyEmptiness(value: Boolean) {
        def isEmpty: Boolean = value
        override def toString = "emptiness"
      }
      val objTrue = new MyEmptiness(true)
      val objFalse = new MyEmptiness(false)
      
      it("should do nothing for 'objTrue should be (empty)'") {
        objTrue should be (empty)
      }
      
      it("should throw TFE with correct stack depth for 'objFalse should be (empty)'") {
        val caught1 = intercept[TestFailedException] {
          objFalse should be (empty)
        }
        assert(caught1.message === Some(wasNotEmpty(objFalse)))
        assert(caught1.failedCodeFileName === Some(fileName))
        assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      }
      
      it("should do nothing if for 'objFalse should not be empty'") {
        objFalse should not be empty
      }
      
      it("should throw TFE with correct stack depth for 'objTrue should not be empty'") {
        val caught1 = intercept[TestFailedException] {
          objTrue should not be empty
        }
        assert(caught1.message === Some(wasEmpty(objTrue)))
        assert(caught1.failedCodeFileName === Some(fileName))
        assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      }
    }
    
    describe("when work with arbitrary object with isEmpty val") {
      
      class MyEmptiness(value: Boolean) {
        val isEmpty: Boolean = value
        override def toString = "emptiness"
      }
      val objTrue = new MyEmptiness(true)
      val objFalse = new MyEmptiness(false)
      
      it("should do nothing for 'objTrue should be (empty)'") {
        objTrue should be (empty)
      }
      
      it("should throw TFE with correct stack depth for 'objFalse should be (empty)'") {
        val caught1 = intercept[TestFailedException] {
          objFalse should be (empty)
        }
        assert(caught1.message === Some(wasNotEmpty(objFalse)))
        assert(caught1.failedCodeFileName === Some(fileName))
        assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      }
      
      it("should do nothing if for 'objFalse should not be empty'") {
        objFalse should not be empty
      }
      
      it("should throw TFE with correct stack depth for 'objTrue should not be empty'") {
        val caught1 = intercept[TestFailedException] {
          objTrue should not be empty
        }
        assert(caught1.message === Some(wasEmpty(objTrue)))
        assert(caught1.failedCodeFileName === Some(fileName))
        assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      }
    }
    
  }
    
}
