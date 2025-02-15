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

import org.scalatest.prop.PropertyChecks
import org.scalatest.exceptions.TestFailedException
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

class ShouldEndWithSubstringSpec extends AnyFunSpec with PropertyChecks with ReturnsNormallyThrowsAssertion {

  describe("The endWith substring syntax") {

    it("should do nothing if the string ends with the specified substring") {

      "1.78" should endWith (".78")
      "21.7" should endWith ("7")
      "21.78" should endWith ("21.78")
      forAll((s: String, t: String) => s + t should endWith (t))
    }

    it("should do nothing if the string does not end with the specified substring when used with not") {

      "eight" should not { endWith ("1.7") }
      "eight" should not endWith ("1.7")
      forAll((s: String, t: String) => if (!(s + t).endsWith(s)) s + t should not (endWith (s)) else succeed)
      forAll((s: String, t: String) => if (!(s + t).endsWith(s)) s + t should not endWith (s) else succeed)
    }

    it("should do nothing if the string does not end with the specified substring when used in a logical-and expression") {

      "1.7b" should ((endWith ("1.7b")) and (endWith ("7b")))
      "1.7b" should (endWith ("1.7b") and (endWith ("7b")))
      "1.7b" should (endWith ("1.7b") and endWith ("7b"))

      forAll((s: String, t: String) => s + t should (endWith (t) and endWith ("")))
    }

    it("should do nothing if the string does not end with the specified substring when used in a logical-or expression") {

      "1.7b" should (endWith ("hello") or (endWith ("1.7b")))
      "1.7b" should ((endWith ("hello")) or (endWith ("1.7b")))
      "1.7b" should (endWith ("hello") or endWith ("1.7b"))

      "1.7b" should (endWith ("hello") or (endWith ("7b")))
      "1.7b" should ((endWith ("hello")) or (endWith ("7b")))
      "1.7b" should (endWith ("hello") or endWith ("7b"))

      forAll((s: String, t: String) => s + t should (endWith ("hi") or endWith (t)))
    }

    it("should do nothing if the string does not end with the specified substring when used in a logical-and expression with not") {

      "fred" should (not (endWith ("fre")) and not (endWith ("1.7")))
      "fred" should ((not endWith ("fre")) and (not endWith ("1.7")))
      "fred" should (not endWith ("fre") and not endWith ("1.7"))
      forAll((s: String) => if (!(s endsWith "bob") && !(s endsWith "1.7")) s should (not endWith ("bob") and not endWith ("1.7")) else succeed)
    }

    it("should do nothing if the string does not end with the specified substring when used in a logical-or expression with not") {
      "fred" should (not (endWith ("fred")) or not (endWith ("1.7")))
      "fred" should ((not endWith ("fred")) or (not endWith ("1.7")))
      "fred" should (not endWith ("fred") or not endWith ("1.7"))
      forAll((s: String) => if (s.indexOf("a") != 0 || s.indexOf("b") != 0) s should (not endWith ("a") or not endWith ("b")) else succeed)
    }

    it("should throw TestFailedException if the string does not match the specified substring") {

      val caught1 = intercept[TestFailedException] {
        "1.7" should endWith ("1.78")
      }
      assert(caught1.getMessage === "\"1.7\" did not end with substring \"1.78\"")

      val caught2 = intercept[TestFailedException] {
        "1.7" should endWith ("21.7")
      }
      assert(caught2.getMessage === "\"1.7\" did not end with substring \"21.7\"")

      val caught3 = intercept[TestFailedException] {
        "1.78" should endWith ("1.7")
      }
      assert(caught3.getMessage === "\"1.78\" did not end with substring \"1.7\"")

      val caught6 = intercept[TestFailedException] {
        "eight" should endWith ("1.7")
      }
      assert(caught6.getMessage === "\"eight\" did not end with substring \"1.7\"")

      val caught7 = intercept[TestFailedException] {
        "one.eight" should endWith ("1.7")
      }
      assert(caught7.getMessage === "\"one.eight\" did not end with substring \"1.7\"")

      val caught8 = intercept[TestFailedException] {
        "onedoteight" should endWith ("1.7")
      }
      assert(caught8.getMessage === "\"onedoteight\" did not end with substring \"1.7\"")

      val caught9 = intercept[TestFailedException] {
        "***" should endWith ("1.7")
      }
      assert(caught9.getMessage === "\"***\" did not end with substring \"1.7\"")

      forAll((s: String) => if (!(s endsWith "1.7")) assertThrows[TestFailedException](s should endWith ("1.7")) else succeed)
    }

    it("should throw TestFailedException if the string does matches the specified substring when used with not") {

      val caught1 = intercept[TestFailedException] {
        "1.7" should not { endWith ("1.7") }
      }
      assert(caught1.getMessage === "\"1.7\" ended with substring \"1.7\"")

      val caught2 = intercept[TestFailedException] {
        "1.7" should not { endWith ("7") }
      }
      assert(caught2.getMessage === "\"1.7\" ended with substring \"7\"")

      val caught3 = intercept[TestFailedException] {
        "-1.8" should not { endWith (".8") }
      }
      assert(caught3.getMessage === "\"-1.8\" ended with substring \".8\"")

      val caught4 = intercept[TestFailedException] {
        "8b" should not { endWith ("b") }
      }
      assert(caught4.getMessage === "\"8b\" ended with substring \"b\"")

      val caught5 = intercept[TestFailedException] {
        "1." should not { endWith ("1.") }
      }
      assert(caught5.getMessage === "\"1.\" ended with substring \"1.\"")

      val caught11 = intercept[TestFailedException] {
        "1.7" should not endWith (".7")
      }
      assert(caught11.getMessage === "\"1.7\" ended with substring \".7\"")

      val caught13 = intercept[TestFailedException] {
        "-1.8" should not endWith ("8")
      }
      assert(caught13.getMessage === "\"-1.8\" ended with substring \"8\"")

      val caught14 = intercept[TestFailedException] {
        "8" should not endWith ("")
      }
      assert(caught14.getMessage === "\"8\" ended with substring \"\"")

      val caught15 = intercept[TestFailedException] {
        "1." should not endWith ("1.")
      }
      assert(caught15.getMessage === "\"1.\" ended with substring \"1.\"")

      val caught21 = intercept[TestFailedException] {
        "1.7a" should not { endWith ("7a") }
      }
      assert(caught21.getMessage === "\"1.7a\" ended with substring \"7a\"")

      val caught22 = intercept[TestFailedException] {
        "b1.7" should not { endWith ("1.7") }
      }
      assert(caught22.getMessage === "\"b1.7\" ended with substring \"1.7\"")

      val caught23 = intercept[TestFailedException] {
        "ba-1.8" should not { endWith ("a-1.8") }
      }
      assert(caught23.getMessage === "\"ba-1.8\" ended with substring \"a-1.8\"")

      forAll((s: String) => if (s.length != 0) assertThrows[TestFailedException](s should not endWith (s.substring(s.length - 1, s.length))) else succeed)
    }

    it("should throw TestFailedException if the string ends with the specified substring when used in a logical-and expression") {

      val caught1 = intercept[TestFailedException] {
        "1.7" should (endWith ("1.7") and (endWith ("1.8")))
      }
      assert(caught1.getMessage === "\"1.7\" ended with substring \"1.7\", but \"1.7\" did not end with substring \"1.8\"")

      val caught2 = intercept[TestFailedException] {
        "1.7" should ((endWith ("7")) and (endWith ("1.8")))
      }
      assert(caught2.getMessage === "\"1.7\" ended with substring \"7\", but \"1.7\" did not end with substring \"1.8\"")

      val caught3 = intercept[TestFailedException] {
        "1.7" should (endWith (".7") and endWith ("1.8"))
      }
      assert(caught3.getMessage === "\"1.7\" ended with substring \".7\", but \"1.7\" did not end with substring \"1.8\"")

      // Check to make sure the error message "short circuits" (i.e., just reports the left side's failure)
      val caught4 = intercept[TestFailedException] {
        "one.eight" should (endWith ("1.7") and (endWith ("1.8")))
      }
      assert(caught4.getMessage === "\"one.eight\" did not end with substring \"1.7\"")

      val caught5 = intercept[TestFailedException] {
        "one.eight" should ((endWith ("1.7")) and (endWith ("1.8")))
      }
      assert(caught5.getMessage === "\"one.eight\" did not end with substring \"1.7\"")

      val caught6 = intercept[TestFailedException] {
        "one.eight" should (endWith ("1.7") and endWith ("1.8"))
      }
      assert(caught6.getMessage === "\"one.eight\" did not end with substring \"1.7\"")

      forAll((s: String, t: String, u: String) => if (!((s + u) endsWith t)) assertThrows[TestFailedException](s + u should (endWith (u) and endWith (t))) else succeed)
    }

    it("should throw TestFailedException if the string ends with the specified substring when used in a logical-or expression") {

      val caught1 = intercept[TestFailedException] {
        "one.seven" should (endWith ("1.7") or (endWith ("1.8")))
      }
      assert(caught1.getMessage === "\"one.seven\" did not end with substring \"1.7\", and \"one.seven\" did not end with substring \"1.8\"")

      val caught2 = intercept[TestFailedException] {
        "one.seven" should ((endWith ("1.7")) or (endWith ("1.8")))
      }
      assert(caught2.getMessage === "\"one.seven\" did not end with substring \"1.7\", and \"one.seven\" did not end with substring \"1.8\"")

      val caught3 = intercept[TestFailedException] {
        "one.seven" should (endWith ("1.7") or endWith ("1.8"))
      }
      assert(caught3.getMessage === "\"one.seven\" did not end with substring \"1.7\", and \"one.seven\" did not end with substring \"1.8\"")

      forAll(
        (s: String, t: String, u: String, v: String) => {
          if (t.length != 0 && v.length != 0 && !(s + u).endsWith(t) && !(s + u).endsWith(v))
            assertThrows[TestFailedException](s + u should (endWith (t) or endWith (v)))
          else
            succeed
        }
      )
    }

    it("should throw TestFailedException if the string ends with the specified substring when used in a logical-and expression used with not") {

      val caught1 = intercept[TestFailedException] {
        "1.7" should (not endWith ("1.8") and (not endWith ("1.7")))
      }
      assert(caught1.getMessage === "\"1.7\" did not end with substring \"1.8\", but \"1.7\" ended with substring \"1.7\"")

      val caught2 = intercept[TestFailedException] {
        "1.7" should ((not endWith ("1.8")) and (not endWith ("1.7")))
      }
      assert(caught2.getMessage === "\"1.7\" did not end with substring \"1.8\", but \"1.7\" ended with substring \"1.7\"")

      val caught3 = intercept[TestFailedException] {
        "1.7" should (not endWith ("1.8") and not endWith ("1.7"))
      }
      assert(caught3.getMessage === "\"1.7\" did not end with substring \"1.8\", but \"1.7\" ended with substring \"1.7\"")

      val caught4 = intercept[TestFailedException] {
        "a1.7" should (not endWith ("1.8") and (not endWith ("a1.7")))
      }
      assert(caught4.getMessage === "\"a1.7\" did not end with substring \"1.8\", but \"a1.7\" ended with substring \"a1.7\"")

      val caught5 = intercept[TestFailedException] {
        "b1.7" should ((not endWith ("1.8")) and (not endWith ("1.7")))
      }
      assert(caught5.getMessage === "\"b1.7\" did not end with substring \"1.8\", but \"b1.7\" ended with substring \"1.7\"")

      val caught6 = intercept[TestFailedException] {
        "a1.7b" should (not endWith ("1.8") and not endWith ("1.7b"))
      }
      assert(caught6.getMessage === "\"a1.7b\" did not end with substring \"1.8\", but \"a1.7b\" ended with substring \"1.7b\"")

      forAll(
        (s: String, t: String, u: String) =>
          if ((s + t + u).indexOf("hi") != 0)
            assertThrows[TestFailedException](s + t + u should (not endWith ("hi") and not endWith (u)))
          else
            succeed
      )
    }

    it("should throw TestFailedException if the string ends with the specified substring when used in a logical-or expression used with not") {

      val caught1 = intercept[TestFailedException] {
        "1.7" should (not endWith ("1.7") or (not endWith ("1.7")))
      }
      assert(caught1.getMessage === "\"1.7\" ended with substring \"1.7\", and \"1.7\" ended with substring \"1.7\"")

      val caught2 = intercept[TestFailedException] {
        "1.7" should ((not endWith ("1.7")) or (not endWith ("1.7")))
      }
      assert(caught2.getMessage === "\"1.7\" ended with substring \"1.7\", and \"1.7\" ended with substring \"1.7\"")

      val caught3 = intercept[TestFailedException] {
        "1.7" should (not endWith ("1.7") or not endWith ("1.7"))
      }
      assert(caught3.getMessage === "\"1.7\" ended with substring \"1.7\", and \"1.7\" ended with substring \"1.7\"")

      val caught4 = intercept[TestFailedException] {
        "1.7" should (not (endWith ("1.7")) or not (endWith ("1.7")))
      }
      assert(caught4.getMessage === "\"1.7\" ended with substring \"1.7\", and \"1.7\" ended with substring \"1.7\"")

      val caught5 = intercept[TestFailedException] {
        "a1.7" should (not endWith (".7") or (not endWith ("a1.7")))
      }
      assert(caught5.getMessage === "\"a1.7\" ended with substring \".7\", and \"a1.7\" ended with substring \"a1.7\"")

      val caught6 = intercept[TestFailedException] {
        "b1.7" should ((not endWith ("1.7")) or (not endWith ("1.7")))
      }
      assert(caught6.getMessage === "\"b1.7\" ended with substring \"1.7\", and \"b1.7\" ended with substring \"1.7\"")

      val caught7 = intercept[TestFailedException] {
        "a1.7b" should (not endWith ("1.7b") or not endWith ("7b"))
      }
      assert(caught7.getMessage === "\"a1.7b\" ended with substring \"1.7b\", and \"a1.7b\" ended with substring \"7b\"")

      val caught8 = intercept[TestFailedException] {
        "a1.7b" should (not (endWith ("1.7b")) or not (endWith ("7b")))
      }
      assert(caught8.getMessage === "\"a1.7b\" ended with substring \"1.7b\", and \"a1.7b\" ended with substring \"7b\"")

      forAll(
        (s: String, t: String) =>
          assertThrows[TestFailedException](s + t should (not endWith (t) or not endWith ("")))
      )
    }
  }
}

