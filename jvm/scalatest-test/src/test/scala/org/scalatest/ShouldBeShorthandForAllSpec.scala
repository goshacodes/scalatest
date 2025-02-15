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

import matchers.{BeMatcher, MatchResult, BePropertyMatcher, BePropertyMatchResult}
import SharedHelpers._
import FailureMessages.decorateToStringValue
import exceptions.TestFailedException
import org.scalactic.Prettifier
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

class ShouldBeShorthandForAllSpec extends AnyFunSpec with EmptyMocks with BookPropertyMatchers {

  private val prettifier = Prettifier.default
  
  def errorMessage(index: Int, message: String, lineNumber: Int, left: Any): String = 
    "'all' inspection failed, because: \n" +
    "  at index " + index + ", " + message + " (ShouldBeShorthandForAllSpec.scala:" + lineNumber + ") \n" +
    "in " + decorateToStringValue(prettifier, left)

  describe("The shouldBe syntax") {

    it("should work with theSameInstanceAs") {

      val string = "Hi"
      val obj: AnyRef = string
      val otherString = new String("Hi")

      all(List(string, obj)) shouldBe theSameInstanceAs (string)
      all(List(string, obj)) shouldBe theSameInstanceAs (obj)

      // This won't work in js, string and otherString will be same instance in js.
      // SKIP-SCALATESTJS,NATIVE-START
      val list = List(otherString, string)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe theSameInstanceAs (otherString)
      }
      assert(caught1.message === (Some(errorMessage(1, "\"Hi\" was not the same instance as \"Hi\"", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      // SKIP-SCALATESTJS,NATIVE-END
    }

    it("should work with any") {
      
      all(List(8, 8, 8)) shouldBe 8
      val list1 = List(1, 2)
      val caught1 = intercept[TestFailedException] {
        all(list1) shouldBe 1
      }
      assert(caught1.message === (Some(errorMessage(1, "2 was not 1", thisLineNumber - 2, list1))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      val s = null
      all(List[String](s)) shouldBe null
      val list2 = List(null, "hi")
      val caught2 = intercept[TestFailedException] {
        all(list2) shouldBe null
      }
      assert(caught2.message === (Some(errorMessage(1, "\"hi\" was not null", thisLineNumber - 2, list2))))
      assert(caught2.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught2.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      all(List(8, 9, 10)) shouldBe > (7)
      val list3 = List(7, 8, 9)
      val caught3 = intercept[TestFailedException] {
        all(list3) shouldBe > (7)
      }
      assert(caught3.message === Some(errorMessage(0, "7 was not greater than 7", thisLineNumber - 2, list3)))
      assert(caught3.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught3.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      all(List(4, 5, 6)) shouldBe < (7) 
      val list4 = List(5, 6, 7)
      val caught4 = intercept[TestFailedException] {
        all(list4) shouldBe < (7)
      }
      assert(caught4.message === Some(errorMessage(2, "7 was not less than 7", thisLineNumber - 2, list4)))
      assert(caught4.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught4.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      all(List(7, 8, 9)) shouldBe >= (7)
      val list5 = List(6, 7, 8)
      val caught5 = intercept[TestFailedException] {
        all(list5) shouldBe >= (7)
      }
      assert(caught5.message === Some(errorMessage(0, "6 was not greater than or equal to 7", thisLineNumber - 2, list5)))
      assert(caught5.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught5.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      all(List(1, 2, 3)) shouldBe <= (7)
      val list6 = List(1, 2, 8)
      val caught6 = intercept[TestFailedException] {
        all(list6) shouldBe <= (7)
      }
      assert(caught6.message === Some(errorMessage(2, "8 was not less than or equal to 7", thisLineNumber - 2, list6)))
      assert(caught6.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught6.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      all(List(true, true, true)) shouldBe true
      val list7 = List(true, false, true)
      val caught7 = intercept[TestFailedException] {
        all(list7) shouldBe true
      }
      assert(caught7.message === Some(errorMessage(1, "false was not true", thisLineNumber - 2, list7)))
      assert(caught7.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught7.failedCodeLineNumber === Some(thisLineNumber - 4))
    }
    
    it("should work with BeMatcher") {
      
      class OddMatcher extends BeMatcher[Int] {
        def apply(left: Int): MatchResult = {
          MatchResult(
            left % 2 == 1,
            left.toString + " was even",
            left.toString + " was odd"
          )
        }
      }
      val odd = new OddMatcher
      val even = not (odd)
      
      all(List(1, 3, 5)) shouldBe odd
      all(List(2, 4, 6)) shouldBe even
      
      val list1 = List(1, 2, 3)
      val caught1 = intercept[TestFailedException] {
        all(list1) shouldBe (odd)
      }
      assert(caught1.message === Some(errorMessage(1, "2 was even", thisLineNumber - 2, list1)))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))

      val list2 = List(6, 7, 8)
      val caught2 = intercept[TestFailedException] {
        all(list2) shouldBe (even)
      }
      assert(caught2.message === Some(errorMessage(1, "7 was odd", thisLineNumber - 2, list2)))
      assert(caught2.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught2.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    // SKIP-SCALATESTJS,NATIVE-START
    it("should work with symbol") {
      
      emptyMock shouldBe Symbol("empty")
      isEmptyMock shouldBe Symbol("empty")
      all(List(emptyMock, isEmptyMock)) shouldBe Symbol("empty")
      
      val list1 = List(noPredicateMock)
      val ex1 = intercept[TestFailedException] {
        all(list1) shouldBe Symbol("empty")
      }
      assert(ex1.message === Some(errorMessage(0, "NoPredicateMock has neither an empty nor an isEmpty method", thisLineNumber - 2, list1)))
      assert(ex1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(ex1.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      val list2 = List(noPredicateMock)
      val ex2 = intercept[TestFailedException] {
        all(list2) shouldBe Symbol("full")
      }
      assert(ex2.message === Some(errorMessage(0, "NoPredicateMock has neither a full nor an isFull method", thisLineNumber - 2, list2)))
      assert(ex2.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(ex2.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      all(List(emptyMock, isEmptyMock)) shouldBe a (Symbol("empty"))
      
      val list3 = List(noPredicateMock)
      val ex3 = intercept[TestFailedException] {
        all(list3) shouldBe a (Symbol("empty"))
      }
      assert(ex3.message === Some(errorMessage(0, "NoPredicateMock has neither an empty nor an isEmpty method", thisLineNumber - 2, list3)))
      assert(ex3.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(ex3.failedCodeLineNumber === Some(thisLineNumber - 4))

      all(List(emptyMock)) shouldBe an (Symbol("empty"))
      
      val list4 = List(noPredicateMock)
      val ex4 = intercept[TestFailedException] {
        all(list4) shouldBe an (Symbol("empty"))
      }
      assert(ex4.message === Some(errorMessage(0, "NoPredicateMock has neither an empty nor an isEmpty method", thisLineNumber - 2, list4)))
      assert(ex4.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(ex4.failedCodeLineNumber === Some(thisLineNumber - 4))
    }
    // SKIP-SCALATESTJS,NATIVE-END
    
    it("should work with BePropertyMatcher") {
      case class MyFile(
        val name: String,
        val file: Boolean,
        val isDirectory: Boolean
      )

      class FileBePropertyMatcher extends BePropertyMatcher[MyFile] {
        def apply(file: MyFile) = {
          new BePropertyMatchResult(file.file, "file")
        }
      }

      class DirectoryBePropertyMatcher extends BePropertyMatcher[MyFile] {
        def apply(file: MyFile) = {
          new BePropertyMatchResult(file.isDirectory, "directory")
        }
      }

      def file = new FileBePropertyMatcher
      def directory = new DirectoryBePropertyMatcher

      val myFile = new MyFile("temp.txt", true, false)

      val book = new Book("A Tale of Two Cities", "Dickens", 1859, 45, true)
      val badBook = new Book("A Tale of Two Cities", "Dickens", 1859, 45, false)
      val badBookPrettified = "Book(\"A Tale of Two Cities\", \"Dickens\", 1859, 45, false)"

      all(List(book)) shouldBe goodRead
      
      val list1 = List(badBook)
      val caught1 = intercept[TestFailedException] {
        all(list1) shouldBe goodRead
      }
      assert(caught1.message === Some(errorMessage(0, s"$badBookPrettified was not goodRead", thisLineNumber - 2, list1)))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      all(List(book)) shouldBe a (goodRead)
      
      val list2 = List(badBook)
      val caught2 = intercept[TestFailedException] {
        all(list2) shouldBe a (goodRead)
      }
      assert(caught2.message === Some(errorMessage(0, s"$badBookPrettified was not a goodRead", thisLineNumber - 2, list2)))
      assert(caught2.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught2.failedCodeLineNumber === Some(thisLineNumber - 4))
      
      all(List(book)) shouldBe an (goodRead)
      
      val list3 = List(badBook)
      val caught3 = intercept[TestFailedException] {
        all(list3) shouldBe an (goodRead)
      }
      assert(caught3.message === Some(errorMessage(0, s"$badBookPrettified was not an goodRead", thisLineNumber - 2, list3)))
      assert(caught3.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught3.failedCodeLineNumber === Some(thisLineNumber - 4))
    }
    
    it("should with +-") {
      
      val sevenDotOh = 7.0
      val minusSevenDotOh = -7.0
      val sevenDotOhFloat = 7.0f
      val minusSevenDotOhFloat = -7.0f
      val sevenLong = 7L
      val minusSevenLong = -7L
      val sevenInt = 7
      val minusSevenInt = -7
      val sevenShort: Short = 7
      val minusSevenShort: Short = -7
      val sevenByte: Byte = 7
      val minusSevenByte: Byte = -7
      
      all(List(sevenDotOh)) shouldBe (7.1 +- 0.2)
      all(List(sevenDotOh)) shouldBe (6.9 +- 0.2)
      all(List(sevenDotOh)) shouldBe (7.0 +- 0.2)
      all(List(sevenDotOh)) shouldBe (7.2 +- 0.2)
      all(List(sevenDotOh)) shouldBe (6.8 +- 0.2)
      all(List(minusSevenDotOh)) shouldBe (-7.1 +- 0.2)
      all(List(minusSevenDotOh)) shouldBe (-6.9 +- 0.2)
      all(List(minusSevenDotOh)) shouldBe (-7.0 +- 0.2)
      all(List(minusSevenDotOh)) shouldBe (-7.2 +- 0.2)
      all(List(minusSevenDotOh)) shouldBe (-6.8 +- 0.2)

      // Double +- Float
      all(List(sevenDotOh)) shouldBe (7.1 +- 0.2f)
      all(List(sevenDotOh)) shouldBe (6.9 +- 0.2f)
      all(List(sevenDotOh)) shouldBe (7.0 +- 0.2f)
      all(List(sevenDotOh)) shouldBe (7.2 +- 0.2f)
      all(List(sevenDotOh)) shouldBe (6.8 +- 0.2f)
      all(List(minusSevenDotOh)) shouldBe (-7.1 +- 0.2f)
      all(List(minusSevenDotOh)) shouldBe (-6.9 +- 0.2f)
      all(List(minusSevenDotOh)) shouldBe (-7.0 +- 0.2f)
      all(List(minusSevenDotOh)) shouldBe (-7.2 +- 0.2f)
      all(List(minusSevenDotOh)) shouldBe (-6.8 +- 0.2f)

      // Double +- Long
      all(List(sevenDotOh)) shouldBe (7.1 +- 2L)
      all(List(sevenDotOh)) shouldBe (6.9 +- 2L)
      all(List(sevenDotOh)) shouldBe (7.0 +- 2L)
      all(List(sevenDotOh)) shouldBe (7.2 +- 2L)
      all(List(sevenDotOh)) shouldBe (6.8 +- 2L)
      all(List(minusSevenDotOh)) shouldBe (-7.1 +- 2L)
      all(List(minusSevenDotOh)) shouldBe (-6.9 +- 2L)
      all(List(minusSevenDotOh)) shouldBe (-7.0 +- 2L)
      all(List(minusSevenDotOh)) shouldBe (-7.2 +- 2L)
      all(List(minusSevenDotOh)) shouldBe (-6.8 +- 2L)

      // Double +- Int
      all(List(sevenDotOh)) shouldBe (7.1 +- 2)
      all(List(sevenDotOh)) shouldBe (6.9 +- 2)
      all(List(sevenDotOh)) shouldBe (7.0 +- 2)
      all(List(sevenDotOh)) shouldBe (7.2 +- 2)
      all(List(sevenDotOh)) shouldBe (6.8 +- 2)
      all(List(minusSevenDotOh)) shouldBe (-7.1 +- 2)
      all(List(minusSevenDotOh)) shouldBe (-6.9 +- 2)
      all(List(minusSevenDotOh)) shouldBe (-7.0 +- 2)
      all(List(minusSevenDotOh)) shouldBe (-7.2 +- 2)
      all(List(minusSevenDotOh)) shouldBe (-6.8 +- 2)

      // Double +- Short
      all(List(sevenDotOh)) shouldBe (7.1 +- 2.toShort)
      all(List(sevenDotOh)) shouldBe (6.9 +- 2.toShort)
      all(List(sevenDotOh)) shouldBe (7.0 +- 2.toShort)
      all(List(sevenDotOh)) shouldBe (7.2 +- 2.toShort)
      all(List(sevenDotOh)) shouldBe (6.8 +- 2.toShort)
      all(List(minusSevenDotOh)) shouldBe (-7.1 +- 2.toShort)
      all(List(minusSevenDotOh)) shouldBe (-6.9 +- 2.toShort)
      all(List(minusSevenDotOh)) shouldBe (-7.0 +- 2.toShort)
      all(List(minusSevenDotOh)) shouldBe (-7.2 +- 2.toShort)
      all(List(minusSevenDotOh)) shouldBe (-6.8 +- 2.toShort)

      // Double +- Byte
      all(List(sevenDotOh)) shouldBe (7.1 +- 2.toByte)
      all(List(sevenDotOh)) shouldBe (6.9 +- 2.toByte)
      all(List(sevenDotOh)) shouldBe (7.0 +- 2.toByte)
      all(List(sevenDotOh)) shouldBe (7.2 +- 2.toByte)
      all(List(sevenDotOh)) shouldBe (6.8 +- 2.toByte)
      all(List(minusSevenDotOh)) shouldBe (-7.1 +- 2.toByte)
      all(List(minusSevenDotOh)) shouldBe (-6.9 +- 2.toByte)
      all(List(minusSevenDotOh)) shouldBe (-7.0 +- 2.toByte)
      all(List(minusSevenDotOh)) shouldBe (-7.2 +- 2.toByte)
      all(List(minusSevenDotOh)) shouldBe (-6.8 +- 2.toByte)

      // Float +- Float
      all(List(sevenDotOhFloat)) shouldBe (7.1f +- 0.2f)
      all(List(sevenDotOhFloat)) shouldBe (6.9f +- 0.2f)
      all(List(sevenDotOhFloat)) shouldBe (7.0f +- 0.2f)
      all(List(sevenDotOhFloat)) shouldBe (7.2f +- 0.2f)
      all(List(sevenDotOhFloat)) shouldBe (6.8f +- 0.2f)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.1f +- 0.2f)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.9f +- 0.2f)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.0f +- 0.2f)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.2f +- 0.2f)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.8f +- 0.2f)

      // Float +- Long
      all(List(sevenDotOhFloat)) shouldBe (7.1f +- 2L)
      all(List(sevenDotOhFloat)) shouldBe (6.9f +- 2L)
      all(List(sevenDotOhFloat)) shouldBe (7.0f +- 2L)
      all(List(sevenDotOhFloat)) shouldBe (7.2f +- 2L)
      all(List(sevenDotOhFloat)) shouldBe (6.8f +- 2L)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.1f +- 2L)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.9f +- 2L)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.0f +- 2L)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.2f +- 2L)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.8f +- 2L)

      // Float +- Int
      all(List(sevenDotOhFloat)) shouldBe (7.1f +- 2)
      all(List(sevenDotOhFloat)) shouldBe (6.9f +- 2)
      all(List(sevenDotOhFloat)) shouldBe (7.0f +- 2)
      all(List(sevenDotOhFloat)) shouldBe (7.2f +- 2)
      all(List(sevenDotOhFloat)) shouldBe (6.8f +- 2)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.1f +- 2)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.9f +- 2)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.0f +- 2)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.2f +- 2)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.8f +- 2)

      // Float +- Short
      all(List(sevenDotOhFloat)) shouldBe (7.1f +- 2.toShort)
      all(List(sevenDotOhFloat)) shouldBe (6.9f +- 2.toShort)
      all(List(sevenDotOhFloat)) shouldBe (7.0f +- 2.toShort)
      all(List(sevenDotOhFloat)) shouldBe (7.2f +- 2.toShort)
      all(List(sevenDotOhFloat)) shouldBe (6.8f +- 2.toShort)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.1f +- 2.toShort)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.9f +- 2.toShort)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.0f +- 2.toShort)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.2f +- 2.toShort)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.8f +- 2.toShort)

      // Float +- Byte
      all(List(sevenDotOhFloat)) shouldBe (7.1f +- 2.toByte)
      all(List(sevenDotOhFloat)) shouldBe (6.9f +- 2.toByte)
      all(List(sevenDotOhFloat)) shouldBe (7.0f +- 2.toByte)
      all(List(sevenDotOhFloat)) shouldBe (7.2f +- 2.toByte)
      all(List(sevenDotOhFloat)) shouldBe (6.8f +- 2.toByte)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.1f +- 2.toByte)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.9f +- 2.toByte)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.0f +- 2.toByte)
      all(List(minusSevenDotOhFloat)) shouldBe (-7.2f +- 2.toByte)
      all(List(minusSevenDotOhFloat)) shouldBe (-6.8f +- 2.toByte)

      // Long +- Long
      all(List(sevenLong)) shouldBe (9L +- 2L)
      all(List(sevenLong)) shouldBe (8L +- 2L)
      all(List(sevenLong)) shouldBe (7L +- 2L)
      all(List(sevenLong)) shouldBe (6L +- 2L)
      all(List(sevenLong)) shouldBe (5L +- 2L)
      all(List(minusSevenLong)) shouldBe (-9L +- 2L)
      all(List(minusSevenLong)) shouldBe (-8L +- 2L)
      all(List(minusSevenLong)) shouldBe (-7L +- 2L)
      all(List(minusSevenLong)) shouldBe (-6L +- 2L)
      all(List(minusSevenLong)) shouldBe (-5L +- 2L)

      // Long +- Int
      all(List(sevenLong)) shouldBe (9L +- 2)
      all(List(sevenLong)) shouldBe (8L +- 2)
      all(List(sevenLong)) shouldBe (7L +- 2)
      all(List(sevenLong)) shouldBe (6L +- 2)
      all(List(sevenLong)) shouldBe (5L +- 2)
      all(List(minusSevenLong)) shouldBe (-9L +- 2)
      all(List(minusSevenLong)) shouldBe (-8L +- 2)
      all(List(minusSevenLong)) shouldBe (-7L +- 2)
      all(List(minusSevenLong)) shouldBe (-6L +- 2)
      all(List(minusSevenLong)) shouldBe (-5L +- 2)

      // Long +- Short
      all(List(sevenLong)) shouldBe (9L +- 2.toShort)
      all(List(sevenLong)) shouldBe (8L +- 2.toShort)
      all(List(sevenLong)) shouldBe (7L +- 2.toShort)
      all(List(sevenLong)) shouldBe (6L +- 2.toShort)
      all(List(sevenLong)) shouldBe (5L +- 2.toShort)
      all(List(minusSevenLong)) shouldBe (-9L +- 2.toShort)
      all(List(minusSevenLong)) shouldBe (-8L +- 2.toShort)
      all(List(minusSevenLong)) shouldBe (-7L +- 2.toShort)
      all(List(minusSevenLong)) shouldBe (-6L +- 2.toShort)
      all(List(minusSevenLong)) shouldBe (-5L +- 2.toShort)

      // Long +- Byte
      all(List(sevenLong)) shouldBe (9L +- 2.toByte)
      all(List(sevenLong)) shouldBe (8L +- 2.toByte)
      all(List(sevenLong)) shouldBe (7L +- 2.toByte)
      all(List(sevenLong)) shouldBe (6L +- 2.toByte)
      all(List(sevenLong)) shouldBe (5L +- 2.toByte)
      all(List(minusSevenLong)) shouldBe (-9L +- 2.toByte)
      all(List(minusSevenLong)) shouldBe (-8L +- 2.toByte)
      all(List(minusSevenLong)) shouldBe (-7L +- 2.toByte)
      all(List(minusSevenLong)) shouldBe (-6L +- 2.toByte)
      all(List(minusSevenLong)) shouldBe (-5L +- 2.toByte)

      // Int +- Int
      all(List(sevenInt)) shouldBe (9 +- 2)
      all(List(sevenInt)) shouldBe (8 +- 2)
      all(List(sevenInt)) shouldBe (7 +- 2)
      all(List(sevenInt)) shouldBe (6 +- 2)
      all(List(sevenInt)) shouldBe (5 +- 2)
      all(List(minusSevenInt)) shouldBe (-9 +- 2)
      all(List(minusSevenInt)) shouldBe (-8 +- 2)
      all(List(minusSevenInt)) shouldBe (-7 +- 2)
      all(List(minusSevenInt)) shouldBe (-6 +- 2)
      all(List(minusSevenInt)) shouldBe (-5 +- 2)

      // Int +- Short
      all(List(sevenInt)) shouldBe (9 +- 2.toShort)
      all(List(sevenInt)) shouldBe (8 +- 2.toShort)
      all(List(sevenInt)) shouldBe (7 +- 2.toShort)
      all(List(sevenInt)) shouldBe (6 +- 2.toShort)
      all(List(sevenInt)) shouldBe (5 +- 2.toShort)
      all(List(minusSevenInt)) shouldBe (-9 +- 2.toShort)
      all(List(minusSevenInt)) shouldBe (-8 +- 2.toShort)
      all(List(minusSevenInt)) shouldBe (-7 +- 2.toShort)
      all(List(minusSevenInt)) shouldBe (-6 +- 2.toShort)
      all(List(minusSevenInt)) shouldBe (-5 +- 2.toShort)

      // Int +- Byte
      all(List(sevenInt)) shouldBe (9 +- 2.toByte)
      all(List(sevenInt)) shouldBe (8 +- 2.toByte)
      all(List(sevenInt)) shouldBe (7 +- 2.toByte)
      all(List(sevenInt)) shouldBe (6 +- 2.toByte)
      all(List(sevenInt)) shouldBe (5 +- 2.toByte)
      all(List(minusSevenInt)) shouldBe (-9 +- 2.toByte)
      all(List(minusSevenInt)) shouldBe (-8 +- 2.toByte)
      all(List(minusSevenInt)) shouldBe (-7 +- 2.toByte)
      all(List(minusSevenInt)) shouldBe (-6 +- 2.toByte)
      all(List(minusSevenInt)) shouldBe (-5 +- 2.toByte)

      // Short +- Short
      all(List(sevenShort)) shouldBe (9.toShort +- 2.toShort)
      all(List(sevenShort)) shouldBe (8.toShort +- 2.toShort)
      all(List(sevenShort)) shouldBe (7.toShort +- 2.toShort)
      all(List(sevenShort)) shouldBe (6.toShort +- 2.toShort)
      all(List(sevenShort)) shouldBe (5.toShort +- 2.toShort)
      all(List(minusSevenShort)) shouldBe ((-9).toShort +- 2.toShort)
      all(List(minusSevenShort)) shouldBe ((-8).toShort +- 2.toShort)
      all(List(minusSevenShort)) shouldBe ((-7).toShort +- 2.toShort)
      all(List(minusSevenShort)) shouldBe ((-6).toShort +- 2.toShort)
      all(List(minusSevenShort)) shouldBe ((-5).toShort +- 2.toShort)

      // Short +- Byte
      all(List(sevenShort)) shouldBe (9.toShort +- 2.toByte)
      all(List(sevenShort)) shouldBe (8.toShort +- 2.toByte)
      all(List(sevenShort)) shouldBe (7.toShort +- 2.toByte)
      all(List(sevenShort)) shouldBe (6.toShort +- 2.toByte)
      all(List(sevenShort)) shouldBe (5.toShort +- 2.toByte)
      all(List(minusSevenShort)) shouldBe ((-9).toShort +- 2.toByte)
      all(List(minusSevenShort)) shouldBe ((-8).toShort +- 2.toByte)
      all(List(minusSevenShort)) shouldBe ((-7).toShort +- 2.toByte)
      all(List(minusSevenShort)) shouldBe ((-6).toShort +- 2.toByte)
      all(List(minusSevenShort)) shouldBe ((-5).toShort +- 2.toByte)

      // Byte +- Byte
      all(List(sevenByte)) shouldBe (9.toByte +- 2.toByte)
      all(List(sevenByte)) shouldBe (8.toByte +- 2.toByte)
      all(List(sevenByte)) shouldBe (7.toByte +- 2.toByte)
      all(List(sevenByte)) shouldBe (6.toByte +- 2.toByte)
      all(List(sevenByte)) shouldBe (5.toByte +- 2.toByte)
      all(List(minusSevenByte)) shouldBe ((-9).toByte +- 2.toByte)
      all(List(minusSevenByte)) shouldBe ((-8).toByte +- 2.toByte)
      all(List(minusSevenByte)) shouldBe ((-7).toByte +- 2.toByte)
      all(List(minusSevenByte)) shouldBe ((-6).toByte +- 2.toByte)
      all(List(minusSevenByte)) shouldBe ((-5).toByte +- 2.toByte)
      
      val list1 = List(sevenDotOh)
      val caught1 = intercept[TestFailedException] {
        all(list1) shouldBe (17.1 +- 0.2)
      }
      assert(caught1.message === Some(errorMessage(0, sevenDotOh+ " was not 17.1 plus or minus 0.2", thisLineNumber - 2, list1)))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Double +- Float
      val list2 = List(sevenDotOh)
      val caught2 = intercept[TestFailedException] {
        all(list2) shouldBe (17.1 +- 0.2f)
      }
      assert(caught2.message === Some(errorMessage(0, sevenDotOh + " was not 17.1 plus or minus 0.20000000298023224", thisLineNumber - 2, list2)))
      assert(caught2.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught2.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Double +- Long
      val list3 = List(sevenDotOh)
      val caught3 = intercept[TestFailedException] {
        all(list3) shouldBe (17.1 +- 2L)
      }
      assert(caught3.message === Some(errorMessage(0, sevenDotOh + " was not 17.1 plus or minus " + 2.0, thisLineNumber - 2, list3)))
      assert(caught3.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught3.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Double +- Int
      val list4 = List(sevenDotOh)
      val caught4 = intercept[TestFailedException] {
        all(list4) shouldBe (17.1 +- 2)
      }
      assert(caught4.message === Some(errorMessage(0, sevenDotOh + " was not 17.1 plus or minus " + 2.0, thisLineNumber - 2, list4)))
      assert(caught4.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught4.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Double +- Short
      val list5 = List(sevenDotOh)
      val caught5 = intercept[TestFailedException] {
        all(list5) shouldBe (17.1 +- 2.toShort)
      }
      assert(caught5.message === Some(errorMessage(0, sevenDotOh + " was not 17.1 plus or minus " + 2.0, thisLineNumber - 2, list5)))
      assert(caught5.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught5.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Double +- Byte
      val list6 = List(sevenDotOh)
      val caught6 = intercept[TestFailedException] {
        all(list6) shouldBe (17.1 +- 2.toByte)
      }
      assert(caught6.message === Some(errorMessage(0, sevenDotOh + " was not 17.1 plus or minus " + 2.0, thisLineNumber - 2, list6)))
      assert(caught6.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught6.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Float +- Float
      val list7 = List(sevenDotOhFloat)
      val caught7 = intercept[TestFailedException] {
        all(list7) shouldBe (17.1f +- 0.2f)
      }
      assert(caught7.message === Some(errorMessage(0, sevenDotOh + " was not " + 17.1f + " plus or minus " + 0.2f, thisLineNumber - 2, list7)))
      assert(caught7.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught7.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Float +- Long
      val list8 = List(sevenDotOhFloat)
      val caught8 = intercept[TestFailedException] {
        all(list8) shouldBe (17.1f +- 2L)
      }
      assert(caught8.message === Some(errorMessage(0, sevenDotOh + " was not " + 17.1f + " plus or minus " + 2.0, thisLineNumber - 2, list8)))
      assert(caught8.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught8.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Float +- Int
      val list9 = List(sevenDotOhFloat)
      val caught9 = intercept[TestFailedException] {
        all(list9) shouldBe (17.1f +- 2)
      }
      assert(caught9.message === Some(errorMessage(0, sevenDotOh + " was not " + 17.1f + " plus or minus " + 2.0, thisLineNumber - 2, list9)))
      assert(caught9.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught9.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Float +- Short
      val list10 = List(sevenDotOhFloat)
      val caught10 = intercept[TestFailedException] {
        all(list10) shouldBe (17.1f +- 2.toShort)
      }
      assert(caught10.message === Some(errorMessage(0, sevenDotOh + " was not " + 17.1f + " plus or minus " + 2.0, thisLineNumber - 2, list10)))
      assert(caught10.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught10.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Float +- Byte
      val list11 = List(sevenDotOhFloat)
      val caught11 = intercept[TestFailedException] {
        all(list11) shouldBe (17.1f +- 2.toByte)
      }
      assert(caught11.message === Some(errorMessage(0, sevenDotOh + " was not " + 17.1f + " plus or minus " + 2.0, thisLineNumber - 2, list11)))
      assert(caught11.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught11.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Long +- Long
      val list12 = List(sevenLong)
      val caught12 = intercept[TestFailedException] {
        all(list12) shouldBe (19L +- 2L)
      }
      assert(caught12.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list12)))
      assert(caught12.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught12.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Long +- Int
      val list13 = List(sevenLong)
      val caught13 = intercept[TestFailedException] {
        all(list13) shouldBe (19L +- 2)
      }
      assert(caught13.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list13)))
      assert(caught13.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught13.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Long +- Short
      val list14 = List(sevenLong)
      val caught14 = intercept[TestFailedException] {
        all(list14) shouldBe (19L +- 2.toShort)
      }
      assert(caught14.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list14)))
      assert(caught14.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught14.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Long +- Byte
      val list15 = List(sevenLong)
      val caught15 = intercept[TestFailedException] {
        all(list15) shouldBe (19L +- 2.toByte)
      }
      assert(caught15.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list15)))
      assert(caught15.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught15.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Int +- Int
      val list16 = List(sevenInt)
      val caught16 = intercept[TestFailedException] {
        all(list16) shouldBe (19 +- 2)
      }
      assert(caught16.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list16)))
      assert(caught16.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught16.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Int +- Short
      val list17 = List(sevenInt)
      val caught17 = intercept[TestFailedException] {
        all(list17) shouldBe (19 +- 2.toShort)
      }
      assert(caught17.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list17)))
      assert(caught17.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught17.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Int +- Byte
      val list18 = List(sevenInt)
      val caught18 = intercept[TestFailedException] {
        all(list18) shouldBe (19 +- 2.toByte)
      }
      assert(caught18.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list18)))
      assert(caught18.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught18.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Short +- Short
      val list19 = List(sevenShort)
      val caught19 = intercept[TestFailedException] {
        all(list19) shouldBe (19.toShort +- 2.toShort)
      }
      assert(caught19.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list19)))
      assert(caught19.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught19.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Short +- Byte
      val list20 = List(sevenShort)
      val caught20 = intercept[TestFailedException] {
        all(list20) shouldBe (19.toShort +- 2.toByte)
      }
      assert(caught20.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list20)))
      assert(caught20.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught20.failedCodeLineNumber === Some(thisLineNumber - 4))

      // Byte +- Byte
      val list21 = List(sevenByte)
      val caught21 = intercept[TestFailedException] {
        all(list21) shouldBe (19.toByte +- 2.toByte)
      }
      assert(caught21.message === Some(errorMessage(0, "7 was not 19 plus or minus 2", thisLineNumber - 2, list21)))
      assert(caught21.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught21.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with a [AnyRef]") {

      val string1 = "Hi"
      val string2: Any = "Hello"
      val int = 8

      all(List(string1, string2)) shouldBe a [String]
      all(List(string1, int)) shouldBe a [Any]

      val list = List(string1, int, string2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [String]
      }
      val offendingLine = thisLineNumber - 2
      // SKIP-SCALATESTJS,NATIVE-START
      assert(caught1.message === (Some(errorMessage(1, "8 was not an instance of java.lang.String, but an instance of java.lang.Integer", offendingLine, list))))
      // SKIP-SCALATESTJS,NATIVE-END
      //SCALATESTJS,NATIVE-ONLY assert(caught1.message === (Some(errorMessage(1, "8 was not an instance of java.lang.String, but an instance of java.lang.Byte", offendingLine, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(offendingLine))
    }

    it("should work with a [java.lang.Integer] but not [Int]") {

      val int1 = 700000
      val int2: Any = 800000
      val str = "Hi"

      all(List(int1, int2)) shouldBe a [java.lang.Integer]
      all(List(int1, str)) shouldBe a [Any]

      val list = List(int1, int2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [Int]
      }
      val offendingLine = thisLineNumber - 2
      assert(caught1.message === (Some(errorMessage(0, int1 + " was not an instance of int, but an instance of java.lang.Integer", offendingLine, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(offendingLine))
    }

    it("should work with a [java.lang.Long], but not [Long]") {

      val long1 = 7L
      val long2: Any = 8L
      val str = "Hi"

      all(List(long1, long2)) shouldBe a [java.lang.Long]
      all(List(long1, str)) shouldBe a [Any]

      val list = List(long1, long2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [Long]
      }
      assert(caught1.message === (Some(errorMessage(0, long1 + " was not an instance of long, but an instance of java.lang.Long", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with a [java.lang.Short], but not [Short]") {

      val short1: Short = 300
      val short2: Any = 400.toShort
      val str = "Hi"

      all(List(short1, short2)) shouldBe a [java.lang.Short]
      all(List(short1, str)) shouldBe a [Any]

      val list = List(short1, short2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [Short]
      }
      val offendingLine = thisLineNumber - 2
      assert(caught1.message === (Some(errorMessage(0, short1 + " was not an instance of short, but an instance of java.lang.Short", offendingLine, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(offendingLine))
    }

    it("should work with a [java.lang.Byte], but not [Byte]") {

      val byte1: Byte = 7.toByte
      val byte2: Any = 8.toByte
      val str = "Hi"

      all(List(byte1, byte2)) shouldBe a [java.lang.Byte]
      all(List(byte1, str)) shouldBe a [Any]

      val list = List(byte1, byte2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [Byte]
      }
      assert(caught1.message === (Some(errorMessage(0, byte1 + " was not an instance of byte, but an instance of java.lang.Byte", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with a [java.lang.Double], but not [Double]") {

      val double1: Double = 7.77
      val double2: Double = 8.88
      val str = "Hi"

      all(List[Double](double1, double2: Double)) shouldBe a [java.lang.Double]
      all(List(double1, str)) shouldBe a [Any]

      val list = List(double1, double2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [Double]
      }
      val offendingLine = thisLineNumber - 2
      assert(caught1.message === (Some(errorMessage(0, double1 + " was not an instance of double, but an instance of java.lang.Double", offendingLine, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(offendingLine))
    }

    it("should work with a [java.lang.Float], but not [Float]") {

      val float1: Float = 7.77f
      val float2: Any = 8.88f
      val str = "Hi"

      all(List(float1, float2)) shouldBe a [java.lang.Float]
      all(List(float1, str)) shouldBe a [Any]

      val list = List(float1, float2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [Float]
      }
      assert(caught1.message === (Some(errorMessage(0, float1 + " was not an instance of float, but an instance of java.lang.Float", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with a [java.lang.Boolean], but not [Boolean]") {

      val bool1 = false
      val bool2: Any = true
      val str = "Hi"

      all(List(bool1, bool2)) shouldBe a [java.lang.Boolean]
      all(List(bool1, str)) shouldBe a [Any]

      val list = List(bool1, bool2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [Boolean]
      }
      assert(caught1.message === (Some(errorMessage(0, s"${bool1.toString()} was not an instance of boolean, but an instance of java.lang.Boolean", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with a [java.lang.Character], but not [Char]") {

      val c1 = '7'
      val c2: Any = '8'
      val str = "Hi"

      all(List(c1, c2)) shouldBe a [java.lang.Character]
      all(List(c1, str)) shouldBe a [Any]

      val list = List(c1, c2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe a [Char]
      }
      assert(caught1.message === (Some(errorMessage(0, "'7' was not an instance of char, but an instance of java.lang.Character", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with an [AnyRef]") {

      val string1 = "Hi"
      val string2: Any = "Hello"
      val int = 8

      all(List(string1, string2)) shouldBe an [String]
      all(List(string1, int)) shouldBe an [Any]

      val list = List(string1, int, string2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [String]
      }
      val offendingLine = thisLineNumber - 2
      // SKIP-SCALATESTJS,NATIVE-START
      assert(caught1.message === (Some(errorMessage(1, "8 was not an instance of java.lang.String, but an instance of java.lang.Integer", offendingLine, list))))
      // SKIP-SCALATESTJS,NATIVE-END
      //SCALATESTJS,NATIVE-ONLY assert(caught1.message === (Some(errorMessage(1, "8 was not an instance of java.lang.String, but an instance of java.lang.Byte", offendingLine, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(offendingLine))
    }

    it("should work with an [java.lang.Intger], but not [Int]") {

      val int1 = 70000
      val int2: Any = 80000
      val str = "Hi"

      all(List(int1, int2)) shouldBe an [java.lang.Integer]
      all(List(int1, str)) shouldBe an [Any]

      val list = List(int1, int2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [Int]
      }
      val offendingLine = thisLineNumber - 2
      assert(caught1.message === (Some(errorMessage(0, int1 + " was not an instance of int, but an instance of java.lang.Integer", offendingLine, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(offendingLine))
    }

    it("should work with an [java.lang.Long], but not [Long]") {

      val long1 = 7L
      val long2: Any = 8L
      val str = "Hi"

      all(List(long1, long2)) shouldBe an [java.lang.Long]
      all(List(long1, str)) shouldBe an [Any]

      val list = List(long1, long2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [Long]
      }
      assert(caught1.message === (Some(errorMessage(0, long1 + " was not an instance of long, but an instance of java.lang.Long", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with an [java.lang.Short], but not [Short]") {

      val short1: Short = 7000
      val short2: Any = 8000.toShort
      val str = "Hi"

      all(List(short1, short2)) shouldBe an [java.lang.Short]
      all(List(short1, str)) shouldBe an [Any]

      val list = List(short1, short2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [Short]
      }
      val offendingLine = thisLineNumber - 2
      assert(caught1.message === (Some(errorMessage(0, short1 + " was not an instance of short, but an instance of java.lang.Short", offendingLine, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(offendingLine))
    }

    it("should work with an [java.lang.Byte], but not [Byte]") {

      val byte1: Byte = 7.toByte
      val byte2: Any = 8.toByte
      val str = "Hi"

      all(List(byte1, byte2)) shouldBe an [java.lang.Byte]
      all(List(byte1, str)) shouldBe an [Any]

      val list = List(byte1, byte2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [Byte]
      }
      assert(caught1.message === (Some(errorMessage(0, byte1 + " was not an instance of byte, but an instance of java.lang.Byte", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with an [java.lang.Double], but not [Double]") {

      val double1: Double = 7.77
      val double2: Any = 8.88
      val str = "Hi"

      all(List(double1, double2)) shouldBe an [java.lang.Double]
      all(List(double1, str)) shouldBe an [Any]

      val list = List(double1, double2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [Double]
      }
      val offendingLine = thisLineNumber - 2
      assert(caught1.message === (Some(errorMessage(0, double1 + " was not an instance of double, but an instance of java.lang.Double", offendingLine, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(offendingLine))
    }

    it("should work with an [java.lang.Float], but not [Float]") {

      val float1: Float = 7.77f
      val float2: Any = 8.88f
      val str = "Hi"

      all(List(float1, float2)) shouldBe an [java.lang.Float]
      all(List(float1, str)) shouldBe an [Any]

      val list = List(float1, float2, str)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [Float]
      }
      assert(caught1.message === (Some(errorMessage(0, float1 + " was not an instance of float, but an instance of java.lang.Float", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with an [java.lang.Boolean], but not [Boolean]") {

      val bool1 = false
      val bool2: Any = true
      val str = "Hi"

      all(List(bool1, bool2)) shouldBe an [java.lang.Boolean]
      all(List(bool1, str)) shouldBe an [Any]

      val list = List(bool1, bool2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [Boolean]
      }
      assert(caught1.message === (Some(errorMessage(0, s"${bool1.toString()} was not an instance of boolean, but an instance of java.lang.Boolean", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }

    it("should work with an [java.lang.Character], but not [Char]") {

      val c1 = '7'
      val c2: Any = '8'
      val str = "Hi"

      all(List(c1, c2)) shouldBe an [java.lang.Character]
      all(List(c1, str)) shouldBe an [Any]

      val list = List(c1, c2)
      val caught1 = intercept[TestFailedException] {
        all(list) shouldBe an [Char]
      }
      assert(caught1.message === (Some(errorMessage(0, "'7' was not an instance of char, but an instance of java.lang.Character", thisLineNumber - 2, list))))
      assert(caught1.failedCodeFileName === Some("ShouldBeShorthandForAllSpec.scala"))
      assert(caught1.failedCodeLineNumber === Some(thisLineNumber - 4))
    }
    
  }

}
