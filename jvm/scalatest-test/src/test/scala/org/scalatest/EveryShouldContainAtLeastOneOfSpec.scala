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

import org.scalactic.{Equality, Every, One, Many, Prettifier}
import org.scalactic.Uniformity
import org.scalactic.StringNormalizations._
import SharedHelpers._
import FailureMessages.decorateToStringValue
import exceptions.TestFailedException
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

class EveryShouldContainAtLeastOneOfSpec extends AnyFunSpec {

  private val prettifier = Prettifier.default

  val upperCaseStringEquality =
    new Equality[String] {
      def areEqual(a: String, b: Any): Boolean = a.toUpperCase == b
    }

  //ADDITIONAL//

  describe("an Every") {

    val fumList: Every[String] = Every("fum")
    val toList: Every[String] = Every("to")
    val ecList: Every[String] = Every("\u0000fum")

    describe("when used with contain atLeastOneOf (...) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        fumList should contain atLeastOneOf ("fee", "fie", "foe", "fum")
        val e1 = intercept[TestFailedException] {
          fumList should contain atLeastOneOf ("happy", "birthday", "to", "you")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources.didNotContainAtLeastOneOf(decorateToStringValue(prettifier, fumList), "\"happy\", \"birthday\", \"to\", \"you\""))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should contain atLeastOneOf ("FEE", "FIE", "FOE", "FUM")
        intercept[TestFailedException] {
          fumList should contain atLeastOneOf ("fum", "foe")
        }
      }
      it("should use an explicitly provided Equality") {
        (fumList should contain atLeastOneOf ("FEE", "FIE", "FOE", "FUM")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (fumList should contain atLeastOneOf ("fum", "foe")) (decided by upperCaseStringEquality)
        }
        intercept[TestFailedException] {
          fumList should contain atLeastOneOf (" FEE ", " FIE ", " FOE ", " FUM ")
        }
        (fumList should contain atLeastOneOf (" FEE ", " FIE ", " FOE ", " FUM ")) (after being lowerCased and trimmed)
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          fumList should contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
      it("should throw TestFailedException with analysis showing escaped string") {
        val e1 = intercept[exceptions.TestFailedException] {
          ecList should contain atLeastOneOf ("fum", "foe")
        }
        e1.analysis should be (Vector("LHS contains at least one string with characters that might cause problem, the escaped string: \"\\u0000fum\""))
      }
    }

    describe("when used with (contain atLeastOneOf (...)) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {

        fumList should (contain atLeastOneOf ("fee", "fie", "foe", "fum"))
        val e1 = intercept[TestFailedException] {
          fumList should (contain atLeastOneOf ("happy", "birthday", "to", "you"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources.didNotContainAtLeastOneOf(decorateToStringValue(prettifier, fumList), "\"happy\", \"birthday\", \"to\", \"you\""))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should (contain atLeastOneOf ("FEE", "FIE", "FOE", "FUM"))
        intercept[TestFailedException] {
          fumList should (contain atLeastOneOf ("fum", "foe"))
        }
      }
      it("should use an explicitly provided Equality") {
        (fumList should (contain atLeastOneOf ("FEE", "FIE", "FOE", "FUM"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (fumList should (contain atLeastOneOf ("fum", "foe"))) (decided by upperCaseStringEquality)
        }
        intercept[TestFailedException] {
          fumList should (contain atLeastOneOf (" FEE ", " FIE ", " FOE ", " FUM "))
        }
        (fumList should (contain atLeastOneOf (" FEE ", " FIE ", " FOE ", " FUM "))) (after being lowerCased and trimmed)
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          fumList should (contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
      it("should throw TestFailedException with analysis showing escaped string") {
        val e1 = intercept[exceptions.TestFailedException] {
          ecList should (contain atLeastOneOf ("fum", "foe"))
        }
        e1.analysis should be (Vector("LHS contains at least one string with characters that might cause problem, the escaped string: \"\\u0000fum\""))
      }
    }

    /*
     I purposely don't want to support this syntax:

            fumList should contain (atLeastOneOf ("fee", "fie", "foe", "fum"))
            fumList should (contain (atLeastOneOf ("fee", "fie", "foe", "fum")))

     Reason is that I don't want people putting parentheses between contain and atLeastOneOf, etc. This will not compile.
    */
    describe("when used with not contain atLeastOneOf (...) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        toList should not contain atLeastOneOf ("fee", "fie", "foe", "fum")
        val e1 = intercept[TestFailedException] {
          toList should not contain atLeastOneOf ("happy", "birthday", "to", "you")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources.containedAtLeastOneOf(decorateToStringValue(prettifier, toList), "\"happy\", \"birthday\", \"to\", \"you\""))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        toList should not contain atLeastOneOf ("to", "you")
        intercept[TestFailedException] {
          toList should not contain atLeastOneOf ("TO", "YOU")
        }
      }
      it("should use an explicitly provided Equality") {
        (toList should not contain atLeastOneOf ("to", "you")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (toList should not contain atLeastOneOf ("TO", "YOU")) (decided by upperCaseStringEquality)
        }
        toList should not contain atLeastOneOf (" TO ", " YOU ")
        intercept[TestFailedException] {
          (toList should not contain atLeastOneOf (" TO ", " YOU ")) (after being lowerCased and trimmed)
        }
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          toList should not contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }

    /*
    Interesting, of these three, the top one does happen to compile and run:

            toList should not contain (atLeastOneOf ("fee", "fie", "foe", "fum"))
            // toList should not (contain (atLeastOneOf ("fee", "fie", "foe", "fum")))
            // toList should (not (contain (atLeastOneOf ("fee", "fie", "foe", "fum"))))

    The bottom two don't, but still I don't want to support that in general.
    */
    describe("when used with (not contain atLeastOneOf (...)) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        toList should (not contain atLeastOneOf ("fee", "fie", "foe", "fum"))
        val e1 = intercept[TestFailedException] {
          toList should (not contain atLeastOneOf ("happy", "birthday", "to", "you"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources.containedAtLeastOneOf(decorateToStringValue(prettifier, toList), "\"happy\", \"birthday\", \"to\", \"you\""))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        toList should (not contain atLeastOneOf ("to", "you"))
        intercept[TestFailedException] {
          toList should (not contain atLeastOneOf ("TO", "YOU"))
        }
      }
      it("should use an explicitly provided Equality") {
        (toList should (not contain atLeastOneOf ("to", "you"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (toList should (not contain atLeastOneOf ("TO", "YOU"))) (decided by upperCaseStringEquality)
        }
        toList should (not contain atLeastOneOf (" TO ", " YOU "))
        intercept[TestFailedException] {
          (toList should (not contain atLeastOneOf (" TO ", " YOU "))) (after being lowerCased and trimmed)
        }
      }

      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          toList should (not contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }

    describe("when used with shouldNot contain atLeastOneOf (...) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        toList shouldNot contain atLeastOneOf ("fee", "fie", "foe", "fum")
        val e1 = intercept[TestFailedException] {
          toList shouldNot contain atLeastOneOf ("happy", "birthday", "to", "you")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources.containedAtLeastOneOf(decorateToStringValue(prettifier, toList), "\"happy\", \"birthday\", \"to\", \"you\""))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        toList shouldNot contain atLeastOneOf ("to", "you")
        intercept[TestFailedException] {
          toList shouldNot contain atLeastOneOf ("TO", "YOU")
        }
      }
      it("should use an explicitly provided Equality") {
        (toList shouldNot contain atLeastOneOf ("to", "you")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (toList shouldNot contain atLeastOneOf ("TO", "YOU")) (decided by upperCaseStringEquality)
        }
        toList shouldNot contain atLeastOneOf (" TO ", " YOU ")
        intercept[TestFailedException] {
          (toList shouldNot contain atLeastOneOf (" TO ", " YOU ")) (after being lowerCased and trimmed)
        }
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          toList shouldNot contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }

    describe("when used with shouldNot (contain atLeastOneOf (...)) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        toList shouldNot (contain atLeastOneOf ("fee", "fie", "foe", "fum"))
        val e1 = intercept[TestFailedException] {
          toList shouldNot (contain atLeastOneOf ("happy", "birthday", "to", "you"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources.containedAtLeastOneOf(decorateToStringValue(prettifier, toList), "\"happy\", \"birthday\", \"to\", \"you\""))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        toList shouldNot (contain atLeastOneOf ("to", "you"))
        intercept[TestFailedException] {
          toList shouldNot (contain atLeastOneOf ("TO", "YOU"))
        }
      }
      it("should use an explicitly provided Equality") {
        (toList shouldNot (contain atLeastOneOf ("to", "you"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (toList shouldNot (contain atLeastOneOf ("TO", "YOU"))) (decided by upperCaseStringEquality)
        }
        toList shouldNot (contain atLeastOneOf (" TO ", " YOU "))
        intercept[TestFailedException] {
          (toList shouldNot (contain atLeastOneOf (" TO ", " YOU "))) (after being lowerCased and trimmed)
        }
      }

      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          toList shouldNot (contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }
  }

  describe("an every of Everys") {

    val list1s: Every[Every[Int]] = Every(Every(1), Every(1), Every(1))
    val lists: Every[Every[Int]] = Every(Every(1), Every(1), Every(2))
    val hiLists: Every[Every[String]] = Every(Every("hi"), Every("hi"), Every("hi"))
    val toLists: Every[Every[String]] = Every(Every("to"), Every("to"), Every("to"))

    describe("when used with contain atLeastOneOf (...) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (list1s) should contain atLeastOneOf (1, 3, 4)
        atLeast (2, lists) should contain atLeastOneOf (1, 3, 4)
        atMost (2, lists) should contain atLeastOneOf (2, 3, 4)
        no (lists) should contain atLeastOneOf (3, 4, 5)

        val e1 = intercept[TestFailedException] {
          all (lists) should contain atLeastOneOf (1, 3, 4)
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        val offendingLine = thisLineNumber - 3
        e1.failedCodeLineNumber.get should be (offendingLine)
        e1.message should be (Some("'all' inspection failed, because: \n" +
          "  at index 2, " + decorateToStringValue(prettifier, One(2)) + " did not contain at least one of (1, 3, 4) (EveryShouldContainAtLeastOneOfSpec.scala:" + offendingLine + ") \n" +
          "in " + decorateToStringValue(prettifier, lists)))
      }

      it("should use the implicit Equality in scope") {
        all (hiLists) should contain atLeastOneOf ("hi", "he")
        intercept[TestFailedException] {
          all (hiLists) should contain atLeastOneOf ("ho", "he")
        }

        {
          implicit val ise = upperCaseStringEquality
          all (hiLists) should contain atLeastOneOf ("HI", "HE")
          intercept[TestFailedException] {
            all (hiLists) should contain atLeastOneOf ("hi", "he")
          }
        }
      }
      it("should use an explicitly provided Equality") {
        (all (hiLists) should contain atLeastOneOf ("HI", "HE")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (hiLists) should contain atLeastOneOf ("hi", "he")) (decided by upperCaseStringEquality)
        }
        implicit val ise = upperCaseStringEquality
        (all (hiLists) should contain atLeastOneOf ("hi", "he")) (decided by defaultEquality[String])
        intercept[TestFailedException] {
          (all (hiLists) should contain atLeastOneOf ("HI", "HE")) (decided by defaultEquality[String])
        }
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          all (list1s) should contain atLeastOneOf (1, 3, 3, 4)
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }

    describe("when used with (contain atLeastOneOf (...)) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (list1s) should (contain atLeastOneOf (1, 3, 4))
        atLeast (2, lists) should (contain atLeastOneOf (1, 3, 4))
        atMost (2, lists) should (contain atLeastOneOf (2, 3, 4))
        no (lists) should (contain atLeastOneOf (3, 4, 5))

        val e1 = intercept[TestFailedException] {
          all (lists) should (contain atLeastOneOf (1, 3, 4))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        val offendingLine = thisLineNumber - 3
        e1.failedCodeLineNumber.get should be (offendingLine)
        e1.message should be (Some("'all' inspection failed, because: \n" +
          "  at index 2, " + decorateToStringValue(prettifier, One(2)) + " did not contain at least one of (1, 3, 4) (EveryShouldContainAtLeastOneOfSpec.scala:" + offendingLine + ") \n" +
          "in " + decorateToStringValue(prettifier, lists)))
      }

      it("should use the implicit Equality in scope") {
        all (hiLists) should (contain atLeastOneOf ("hi", "he"))
        intercept[TestFailedException] {
          all (hiLists) should (contain atLeastOneOf ("HI", "HE"))
        }

        {
          implicit val ise = upperCaseStringEquality
          all (hiLists) should (contain atLeastOneOf ("HI", "HE"))
          intercept[TestFailedException] {
            all (hiLists) should (contain atLeastOneOf ("hi", "he"))
          }
        }
      }
      it("should use an explicitly provided Equality") {
        (all (hiLists) should (contain atLeastOneOf ("HI", "HE"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (hiLists) should (contain atLeastOneOf ("hi", "he"))) (decided by upperCaseStringEquality)
        }
        implicit val ise = upperCaseStringEquality
        (all (hiLists) should (contain atLeastOneOf ("hi", "he"))) (decided by defaultEquality[String])
        intercept[TestFailedException] {
          (all (hiLists) should (contain atLeastOneOf ("HI", "HE"))) (decided by defaultEquality[String])
        }
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          all (list1s) should (contain atLeastOneOf (1, 3, 3, 4))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }

    /*
     I purposely don't want to support this syntax:

    scala> all (list1s) should contain (atLeastOneOf (1, 3, 4))
    <console>:15: error: org.scalatest.words.NewContainWord does not take parameters
                  all (list1s) should contain (atLeastOneOf (1, 3, 4))
                                                 ^

    scala> all (list1s) should (contain (atLeastOneOf (1, 3, 4)))
    <console>:15: error: org.scalatest.words.NewContainWord does not take parameters
                  all (list1s) should (contain (atLeastOneOf (1, 3, 4)))
                                                  ^

     Reason is that I don't want people putting parentheses between contain and atLeastOneOf, etc. This will not compile.
    */
    describe("when used with not contain atLeastOneOf (...) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (toLists) should not contain atLeastOneOf ("fee", "fie", "foe", "fum")
        val e1 = intercept[TestFailedException] {
          all (toLists) should not contain atLeastOneOf ("happy", "birthday", "to", "you")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        val offendingLine = thisLineNumber - 3
        e1.failedCodeLineNumber.get should be (offendingLine)
        e1.message should be (Some("'all' inspection failed, because: \n" +
          "  at index 0, " + decorateToStringValue(prettifier, One("to")) + " contained at least one of (\"happy\", \"birthday\", \"to\", \"you\") (EveryShouldContainAtLeastOneOfSpec.scala:" + offendingLine + ") \n" +
          "in " + decorateToStringValue(prettifier, toLists)))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        all (toLists) should not contain atLeastOneOf ("to", "you")
        intercept[TestFailedException] {
          all (toLists) should not contain atLeastOneOf ("TO", "YOU")
        }
      }
      it("should use an explicitly provided Equality") {
        (all (toLists) should not contain atLeastOneOf ("to", "you")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (toLists) should not contain atLeastOneOf ("TO", "YOU")) (decided by upperCaseStringEquality)
        }
        all (toLists) should not contain atLeastOneOf (" TO ", " YOU ")
        intercept[TestFailedException] {
          (all (toLists) should not contain atLeastOneOf (" TO ", " YOU ")) (after being lowerCased and trimmed)
        }
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          all (toLists) should not contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }

    /*
    Interesting, of these three, the last one does happen to compile and run:

    scala> all (toLists) should (not (contain (atLeastOneOf ("fee", "fie", "foe", "fum"))))
    <console>:15: error: org.scalatest.words.NewContainWord does not take parameters
                  all (toLists) should (not (contain (atLeastOneOf ("fee", "fie", "foe", "fum"))))
                                                        ^

    scala> all (toLists) should not (contain (atLeastOneOf ("fee", "fie", "foe", "fum")))
    <console>:15: error: org.scalatest.words.NewContainWord does not take parameters
                  all (toLists) should not (contain (atLeastOneOf ("fee", "fie", "foe", "fum")))
                                                       ^

    scala> all (toLists) should not contain (atLeastOneOf ("fee", "fie", "foe", "fum"))

    The top two don't, but still I don't want to support that in general.
    */
    describe("when used with (not contain atLeastOneOf (...)) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (toLists) should (not contain atLeastOneOf ("fee", "fie", "foe", "fum"))
        val e1 = intercept[TestFailedException] {
          all (toLists) should (not contain atLeastOneOf ("happy", "birthday", "to", "you"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        val offendingLine = thisLineNumber - 3
        e1.failedCodeLineNumber.get should be (offendingLine)
        e1.message should be (Some("'all' inspection failed, because: \n" +
          "  at index 0, " + decorateToStringValue(prettifier, One("to")) + " contained at least one of (\"happy\", \"birthday\", \"to\", \"you\") (EveryShouldContainAtLeastOneOfSpec.scala:" + offendingLine + ") \n" +
          "in " + decorateToStringValue(prettifier, toLists)))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        all (toLists) should (not contain atLeastOneOf ("to", "you"))
        intercept[TestFailedException] {
          all (toLists) should (not contain atLeastOneOf ("TO", "YOU"))
        }
      }
      it("should use an explicitly provided Equality") {
        (all (toLists) should (not contain atLeastOneOf ("to", "you"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (toLists) should (not contain atLeastOneOf ("TO", "YOU"))) (decided by upperCaseStringEquality)
        }
        all (toLists) should (not contain atLeastOneOf (" TO ", " YOU "))
        intercept[TestFailedException] {
          (all (toLists) should (not contain atLeastOneOf (" TO ", " YOU "))) (after being lowerCased and trimmed)
        }
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          all (toLists) should (not contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }

    describe("when used with shouldNot contain atLeastOneOf (...) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (toLists) shouldNot contain atLeastOneOf ("fee", "fie", "foe", "fum")
        val e1 = intercept[TestFailedException] {
          all (toLists) shouldNot contain atLeastOneOf ("happy", "birthday", "to", "you")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        val offendingLine = thisLineNumber - 3
        e1.failedCodeLineNumber.get should be (offendingLine)
        e1.message should be (Some("'all' inspection failed, because: \n" +
          "  at index 0, " + decorateToStringValue(prettifier, Every("to")) + " contained at least one of (\"happy\", \"birthday\", \"to\", \"you\") (EveryShouldContainAtLeastOneOfSpec.scala:" + offendingLine + ") \n" +
          "in " + decorateToStringValue(prettifier, toLists)))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        all (toLists) shouldNot contain atLeastOneOf ("to", "you")
        intercept[TestFailedException] {
          all (toLists) shouldNot contain atLeastOneOf ("TO", "YOU")
        }
      }
      it("should use an explicitly provided Equality") {
        (all (toLists) shouldNot contain atLeastOneOf ("to", "you")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (toLists) shouldNot contain atLeastOneOf ("TO", "YOU")) (decided by upperCaseStringEquality)
        }
        all (toLists) shouldNot contain atLeastOneOf (" TO ", " YOU ")
        intercept[TestFailedException] {
          (all (toLists) shouldNot contain atLeastOneOf (" TO ", " YOU ")) (after being lowerCased and trimmed)
        }
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          all (toLists) shouldNot contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum")
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }

    describe("when used with shouldNot (contain atLeastOneOf (...)) syntax") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (toLists) shouldNot (contain atLeastOneOf ("fee", "fie", "foe", "fum"))
        val e1 = intercept[TestFailedException] {
          all (toLists) shouldNot (contain atLeastOneOf ("happy", "birthday", "to", "you"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        val offendingLine = thisLineNumber - 3
        e1.failedCodeLineNumber.get should be (offendingLine)
        e1.message should be (Some("'all' inspection failed, because: \n" +
          "  at index 0, " + decorateToStringValue(prettifier, One("to")) + " contained at least one of (\"happy\", \"birthday\", \"to\", \"you\") (EveryShouldContainAtLeastOneOfSpec.scala:" + offendingLine + ") \n" +
          "in " + decorateToStringValue(prettifier, toLists)))
      }
      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        all (toLists) shouldNot (contain atLeastOneOf ("to", "you"))
        intercept[TestFailedException] {
          all (toLists) shouldNot (contain atLeastOneOf ("TO", "YOU"))
        }
      }
      it("should use an explicitly provided Equality") {
        (all (toLists) shouldNot (contain atLeastOneOf ("to", "you"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (toLists) shouldNot (contain atLeastOneOf ("TO", "YOU"))) (decided by upperCaseStringEquality)
        }
        all (toLists) shouldNot (contain atLeastOneOf (" TO ", " YOU "))
        intercept[TestFailedException] {
          (all (toLists) shouldNot (contain atLeastOneOf (" TO ", " YOU "))) (after being lowerCased and trimmed)
        }
      }
      it("should throw NotAllowedException with correct stack depth and message when RHS contain duplicated value") {
        val e1 = intercept[exceptions.NotAllowedException] {
          all (toLists) shouldNot (contain atLeastOneOf ("fee", "fie", "foe", "fie", "fum"))
        }
        e1.failedCodeFileName.get should be ("EveryShouldContainAtLeastOneOfSpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some(Resources.atLeastOneOfDuplicate))
      }
    }
  }
}
