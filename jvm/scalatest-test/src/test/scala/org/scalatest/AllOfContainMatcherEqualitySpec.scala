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

import org.scalactic.Equality
import org.scalactic.Prettifier
import org.scalactic.ColCompatHelper.Iterable
import SharedHelpers._
import matchers.should.Matchers._

import org.scalactic.ArrayHelper.deep

class AllOfContainMatcherEqualitySpec extends funspec.AnyFunSpec {

  private val prettifier = Prettifier.default

  class TrimEquality extends Equality[String] {
    def areEqual(left: String, right: Any) = 
      left.trim == (right match {
        case s: String => s.trim
        case other => other
      })
  }
  
  class MapTrimEquality extends Equality[(Int, String)] {
    def areEqual(left: (Int, String), right: Any) = 
      right match {
        case t2: Tuple2[_, _] =>  
          left._1 == t2._1 && 
          left._2.trim == (t2._2 match {
            case s: String => s.trim
            case other => other
          })
        case right => left == right
      }
  }
  
  class JavaMapTrimEquality extends Equality[java.util.Map.Entry[Int, String]] {
    def areEqual(left: java.util.Map.Entry[Int, String], right: Any) = 
      right match {
        case rightEntry: java.util.Map.Entry[_, _] =>  
          left.getKey == rightEntry.getKey && 
          left.getValue.trim == (rightEntry.getValue match {
            case s: String => s.trim
            case other => other
          })
        case right => left == right
      }
  }
  
  class FalseEquality extends Equality[Int] {
    def areEqual(left: Int, right: Any): Boolean = false
  }
  
  class MapFalseEquality extends Equality[(Int, String)] {
    def areEqual(left: (Int, String), right: Any): Boolean = false
  }
  
  class JavaMapFalseEquality extends Equality[java.util.Map.Entry[Int, String]] {
    def areEqual(left: java.util.Map.Entry[Int, String], right: Any): Boolean = false
  }
  
  describe("allOf ") {
    
    def checkShouldContainStackDepth(e: exceptions.StackDepthException, left: Any, right: Iterable[Any], lineNumber: Int): Unit = {
      val leftText = FailureMessages.decorateToStringValue(prettifier, left)
      e.message should be (Some(leftText + " did not contain all of (" + right.map(r => FailureMessages.decorateToStringValue(prettifier, r)).mkString(", ") + ")"))
      e.failedCodeFileName should be (Some("AllOfContainMatcherEqualitySpec.scala"))
      e.failedCodeLineNumber should be (Some(lineNumber))
    }
      
    def checkShouldNotContainStackDepth(e: exceptions.StackDepthException, left: Any, right: Iterable[Any], lineNumber: Int): Unit = {
      val leftText = FailureMessages.decorateToStringValue(prettifier, left)
      e.message should be (Some(leftText + " contained all of (" + right.map(r => FailureMessages.decorateToStringValue(prettifier, r)).mkString(", ") + ")"))
      e.failedCodeFileName should be (Some("AllOfContainMatcherEqualitySpec.scala"))
      e.failedCodeLineNumber should be (Some(lineNumber))
    }
    
    it("should take custom implicit equality in scope when 'should contain' is used") {
      
      implicit val trimEquality = new TrimEquality
      implicit val mapTrimEquality = new MapTrimEquality
      implicit val javaMapTrimEquality = new JavaMapTrimEquality
      
      List("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3")
      Set("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3")
      Array("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3")
      Map(1 -> "one ", 2 -> "two", 3 -> "three ") should contain allOf (1 -> "one", 2 -> "two ", 3 -> "three")

      // SKIP-SCALATESTJS,NATIVE-START
      javaList("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3")
      javaSet("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3")
      javaMap(Entry(1, "one "), Entry(2, "two"), Entry(3, "three ")) should contain allOf (Entry(1, "one"), Entry(2, "two "), Entry(3, "three"))
      // SKIP-SCALATESTJS,NATIVE-END
    }
    
    it("should take custom implicit equality in scope when 'should not contain' is used") {
      
      implicit val trimEquality = new TrimEquality
      implicit val mapTrimEquality = new MapTrimEquality
      implicit val javaMapTrimEquality = new JavaMapTrimEquality
      
      List("A ", "B", "C ") should not contain allOf ("a ", "b", "c ")
      Set("A ", "B", "C ") should not contain allOf ("a ", "b", "c ")
      Array("A ", "B", "C ") should not contain allOf ("a ", "b", "c ")
      Map(1 -> "A ", 2 -> "B", 3 -> "C ") should not contain allOf (1 -> "a ", 2 -> "b", 3 -> "c ")

      // SKIP-SCALATESTJS,NATIVE-START
      javaList("A ", "B", "C ") should not contain allOf ("a ", "b", "c ")
      javaSet("A ", "B", "C ") should not contain allOf ("a ", "b", "c ")
      javaMap(Entry(1, "A "), Entry(2, "B"), Entry(3, "C ")) should not contain allOf (Entry(1, "a "), Entry(2, "b"), Entry(3, "c "))
      // SKIP-SCALATESTJS,NATIVE-END
    }
    
    it("should throw TestFailedException with correct stack depth and message when 'should contain custom matcher' failed with custom implicit equality in scope") {
      
      implicit val falseEquality = new FalseEquality
      implicit val mapFalseEquality = new MapFalseEquality
      implicit val javaMapFalseEquality = new JavaMapFalseEquality
      
      val left1 = List(1, 2, 3)
      val e1 = intercept[exceptions.TestFailedException] {
        left1 should contain allOf (1, 2, 3)
      }
      checkShouldContainStackDepth(e1, left1, deep(Array(1, 2, 3)), thisLineNumber - 2)
        
      val left2 = Set(1, 2, 3)
      val e2 = intercept[exceptions.TestFailedException] {
        left2 should contain allOf (1, 2, 3)
      }
      checkShouldContainStackDepth(e2, left2, deep(Array(1, 2, 3)), thisLineNumber - 2)
        
      val left3 = Array(1, 2, 3)
      val e3 = intercept[exceptions.TestFailedException] {
        left3 should contain allOf (1, 2, 3)
      }
        checkShouldContainStackDepth(e3, left3, deep(Array(1, 2, 3)), thisLineNumber - 2)
        
      val left4 = Map(1 -> "one", 2 -> "two", 3 -> "three")
      val e4 = intercept[exceptions.TestFailedException] {
        left4 should contain allOf (1 -> "one", 2 -> "two", 3 -> "three")
      }
      checkShouldContainStackDepth(e4, left4, deep(Array(1 -> "one", 2 -> "two", 3 -> "three")), thisLineNumber - 2)

      // SKIP-SCALATESTJS,NATIVE-START
      val left5 = javaList(1, 2, 3)
      val e5 = intercept[exceptions.TestFailedException] {
        left5 should contain allOf (1, 2, 3)
      }
      checkShouldContainStackDepth(e5, left5, deep(Array(1, 2, 3)), thisLineNumber - 2)

      val left6 = javaMap(Entry(1, "one"), Entry(2, "two"), Entry(3, "three"))
      val e6 = intercept[exceptions.TestFailedException] {
        left6 should contain allOf (Entry(1, "one"), Entry(2, "two"), Entry(3, "three"))
      }
      checkShouldContainStackDepth(e6, left6, deep(Array(Entry(1, "one"), Entry(2, "two"), Entry(3, "three"))), thisLineNumber - 2)
      // SKIP-SCALATESTJS,NATIVE-END
    }
    
    it("should throw TestFailedException with correct stack depth and message when 'should not contain custom matcher' failed with custom implicit equality in scope") {
      
      implicit val trimEquality = new TrimEquality
      implicit val mapTrimEquality = new MapTrimEquality
      implicit val javaMapTrimEquality = new JavaMapTrimEquality
      
      val left1 = List("1 ", "2", "3 ")
      val e1 = intercept[exceptions.TestFailedException] {
        left1 should not contain allOf ("1", "2 ", "3")
      }
      checkShouldNotContainStackDepth(e1, left1, deep(Array("1", "2 ", "3")), thisLineNumber - 2)
        
      val left2 = Set("1 ", "2", "3 ")
      val e2 = intercept[exceptions.TestFailedException] {
        left2 should not contain allOf ("1", "2 ", "3")
      }
      checkShouldNotContainStackDepth(e2, left2, deep(Array("1", "2 ", "3")), thisLineNumber - 2)
        
      val left3 = Array("1 ", "2", "3 ")
      val e3 = intercept[exceptions.TestFailedException] {
        left3 should not contain allOf ("1", "2 ", "3")
      }
      checkShouldNotContainStackDepth(e3, left3, deep(Array("1", "2 ", "3")), thisLineNumber - 2)
        
      val left4 = Map(1 -> "one ", 2 -> "two", 3 -> "three ")
      val e4 = intercept[exceptions.TestFailedException] {
        left4 should not contain allOf (1 -> "one", 2 -> "two ", 3 -> "three")
      }
      checkShouldNotContainStackDepth(e4, left4, deep(Array(1 -> "one", 2 -> "two ", 3 -> "three")), thisLineNumber - 2)

      // SKIP-SCALATESTJS,NATIVE-START
      val left5 = javaList("1 ", "2", "3 ")
      val e5 = intercept[exceptions.TestFailedException] {
        left5 should not contain allOf ("1", "2 ", "3")
      }
      checkShouldNotContainStackDepth(e5, left5, deep(Array("1", "2 ", "3")), thisLineNumber - 2)

      val left6 = javaMap(Entry(1, "one "), Entry(2, "two"), Entry(3, "three "))
      val e6 = intercept[exceptions.TestFailedException] {
        left6 should not contain allOf (Entry(1, "one"), Entry(2, "two "), Entry(3, "three"))
      }
      checkShouldNotContainStackDepth(e6, left6, deep(Array(Entry(1, "one"), Entry(2, "two "), Entry(3, "three"))), thisLineNumber - 2)
      // SKIP-SCALATESTJS,NATIVE-END
    }
    
    it("should take passed in custom explicit equality when 'should contain' is used") {
      val trimEquality = new TrimEquality
      val mapTrimEquality = new MapTrimEquality
      val javaMapTrimEquality = new JavaMapTrimEquality
      
      (List("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3 ")) (trimEquality)
      (Set("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3 ")) (trimEquality)
      (Array("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3 ")) (trimEquality)
      (Map(1 -> "one ", 2 -> "two", 3 -> "three ") should contain allOf (1 -> "one", 2 -> "two ", 3 -> "three")) (mapTrimEquality)

      // SKIP-SCALATESTJS,NATIVE-START
      (javaList("1 ", "2", "3 ") should contain allOf ("1", "2 ", "3 ")) (trimEquality)
      (javaMap(Entry(1, "one "), Entry(2, "two"), Entry(3, "three ")) should contain allOf (Entry(1, "one"), Entry(2, "two "), Entry(3, "three"))) (javaMapTrimEquality)
      // SKIP-SCALATESTJS,NATIVE-END
    }
    
    it("should take passed in custom explicit equality when 'should not contain' is used") {
      val equality = new FalseEquality
      (List(1, 2, 3) should not contain allOf (1, 2, 3)) (equality)
      (Set(1, 2, 3) should not contain allOf (1, 2, 3)) (equality)
      (Array(1, 2, 3) should not contain allOf (1, 2, 3)) (equality)
      val mapEquality = new MapFalseEquality
      (Map(1 -> "one", 2 -> "two", 3 -> "three") should not contain allOf (1 -> "one", 2 -> "two", 3 -> "three")) (mapEquality)

      // SKIP-SCALATESTJS,NATIVE-START
      (javaList(1, 2, 3) should not contain allOf (1, 2, 3)) (equality)
      val javaMapEquality = new JavaMapFalseEquality
      (javaMap(Entry(1, "one"), Entry(2, "two"), Entry(3, "three")) should not contain allOf (Entry(1, "one"), Entry(2, "two"), Entry(3, "three"))) (javaMapEquality)
      // SKIP-SCALATESTJS,NATIVE-END
    }
    
    it("should throw TestFailedException with correct stack depth and message when 'should contain custom matcher' failed with custom explicit equality") {
      val equality = new FalseEquality
        
      val left1 = List(1, 2, 3)
      val e1 = intercept[exceptions.TestFailedException] {
        (left1 should contain allOf (1, 2, 3)) (equality)
      }
      checkShouldContainStackDepth(e1, left1, deep(Array(1, 2, 3)), thisLineNumber - 2)
        
      val left2 = Set(1, 2, 3)
      val e2 = intercept[exceptions.TestFailedException] {
        (left2 should contain allOf (1, 2, 3)) (equality)
      }
      checkShouldContainStackDepth(e2, left2, deep(Array(1, 2, 3)), thisLineNumber - 2)
        
      val left3 = Array(1, 2, 3)
      val e3 = intercept[exceptions.TestFailedException] {
        (left3 should contain allOf (1, 2, 3)) (equality)
      }
      checkShouldContainStackDepth(e3, left3, deep(Array(1, 2, 3)), thisLineNumber - 2)
        
      val mapEquality = new MapFalseEquality
        
      val left4 = Map(1 -> "one", 2 -> "two", 3 -> "three")
      val e4 = intercept[exceptions.TestFailedException] {
        (left4 should contain allOf (1 -> "one", 2 -> "two", 3 -> "three")) (mapEquality)
      }
      checkShouldContainStackDepth(e4, left4, deep(Array(1 -> "one", 2 -> "two", 3 -> "three")), thisLineNumber - 2)

      // SKIP-SCALATESTJS,NATIVE-START
      val left5 = javaList(1, 2, 3)
      val e5 = intercept[exceptions.TestFailedException] {
        (left5 should contain allOf (1, 2, 3)) (equality)
      }
      checkShouldContainStackDepth(e5, left5, deep(Array(1, 2, 3)), thisLineNumber - 2)

      val javaMapEquality = new JavaMapFalseEquality
      
      val left6 = javaMap(Entry(1, "one"), Entry(2, "two"), Entry(3, "three"))
      val e6 = intercept[exceptions.TestFailedException] {
        (left6 should contain allOf (Entry(1, "one"), Entry(2, "two"), Entry(3, "three"))) (javaMapEquality)
      }
      checkShouldContainStackDepth(e6, left6, deep(Array(Entry(1, "one"), Entry(2, "two"), Entry(3, "three"))), thisLineNumber - 2)
      // SKIP-SCALATESTJS,NATIVE-END
    }
    
    it("should throw TestFailedException with correct stack depth and message when 'should not contain custom matcher' failed with custom explicit equality") {
      val trimEquality = new TrimEquality
        
      val left1 = List("1 ", "2", "3 ")
      val e1 = intercept[exceptions.TestFailedException] {
        (left1 should not contain allOf ("1", "2 ", "3")) (trimEquality)
      }
      checkShouldNotContainStackDepth(e1, left1, deep(Array("1", "2 ", "3")), thisLineNumber - 2)
        
      val left2 = Set("1 ", "2", "3 ")
      val e2 = intercept[exceptions.TestFailedException] {
        (left2 should not contain allOf ("1", "2 ", "3")) (trimEquality)
      }
      checkShouldNotContainStackDepth(e2, left2, deep(Array("1", "2 ", "3")), thisLineNumber - 2)
        
      val left3 = Array("1 ", "2", "3 ")
      val e3 = intercept[exceptions.TestFailedException] {
        (left3 should not contain allOf ("1", "2 ", "3")) (trimEquality)
      }
      checkShouldNotContainStackDepth(e3, left3, deep(Array("1", "2 ", "3")), thisLineNumber - 2)
        
      val mapTrimEquality = new MapTrimEquality
       
      val left4 = Map(1 -> "one ", 2 -> "two", 3 -> "three ")
      val e4 = intercept[exceptions.TestFailedException] {
        (left4 should not contain allOf (1 -> "one", 2 -> "two ", 3 -> "three")) (mapTrimEquality)
      }
      checkShouldNotContainStackDepth(e4, left4, deep(Array(1 -> "one", 2 -> "two ", 3 -> "three")), thisLineNumber - 2)

      // SKIP-SCALATESTJS,NATIVE-START
      val left5 = javaList("1 ", "2", "3 ")
      val e5 = intercept[exceptions.TestFailedException] {
        (left5 should not contain allOf ("1", "2 ", "3")) (trimEquality)
      }
      checkShouldNotContainStackDepth(e5, left5, deep(Array("1", "2 ", "3")), thisLineNumber - 2)

      val javaMapTrimEquality = new JavaMapTrimEquality
      
      val left6 = javaMap(Entry(1, "one "), Entry(2, "two"), Entry(3, "three "))
      val e6 = intercept[exceptions.TestFailedException] {
        (left6 should not contain allOf (Entry(1, "one"), Entry(2, "two "), Entry(3, "three"))) (javaMapTrimEquality)
      }
      checkShouldNotContainStackDepth(e6, left6, deep(Array(Entry(1, "one"), Entry(2, "two "), Entry(3, "three"))), thisLineNumber - 2)
      // SKIP-SCALATESTJS,NATIVE-END
    }
  }
  
}