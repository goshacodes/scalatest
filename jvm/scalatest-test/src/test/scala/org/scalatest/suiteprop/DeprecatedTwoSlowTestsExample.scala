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
package org.scalatest.suiteprop

import org.scalatest._
// SKIP-SCALATESTJS,NATIVE-START
import refspec.RefSpec
// SKIP-SCALATESTJS,NATIVE-END
import org.scalatest.{ freespec, funspec }
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.propspec.AnyPropSpec
import org.scalatest.wordspec.AnyWordSpec

class DeprecatedTwoSlowTestsExample extends org.scalatest.suiteprop.SuiteExamples {

  trait Services {
    val theTestNames = Vector("first test", "second test")
  }

  trait NestedTestNames extends Services {
    override val theTestNames = Vector("A subject should first test", "A subject should second test")
  }

  trait DeeplyNestedTestNames extends Services {
    override val theTestNames = Vector("A subject when created should first test", "A subject when created should second test")
  }

  trait NestedTestNamesWithMust extends Services {
    override val theTestNames = Vector("A subject must first test", "A subject must second test")
  }

  trait DeeplyNestedTestNamesWithMust extends Services {
    override val theTestNames = Vector("A subject when created must first test", "A subject when created must second test")
  }

  trait NestedTestNamesWithCan extends Services {
    override val theTestNames = Vector("A subject can first test", "A subject can second test")
  }

  trait DeeplyNestedTestNamesWithCan extends Services {
    override val theTestNames = Vector("A subject when created can first test", "A subject when created can second test")
  }

  type FixtureServices = Services

  // SKIP-SCALATESTJS,NATIVE-START
  class SpecExample extends RefSpec with Services {
    @SlowAsMolasses def `test first`: Unit = {}
    @Ignore @SlowAsMolasses def `test second`: Unit = {}
    override val theTestNames = Vector("test first", "test second")
  }
  // SKIP-SCALATESTJS,NATIVE-END

  class FunSuiteExample extends AnyFunSuite with Services {
    test("first test", mytags.SlowAsMolasses) {}
    ignore("second test", mytags.SlowAsMolasses) {}
  }

  class FixtureFunSuiteExample extends StringFixtureFunSuite with Services {
    test("first test", mytags.SlowAsMolasses) { s => }
    ignore("second test", mytags.SlowAsMolasses) { s => }
  }

  class FunSpecExample extends AnyFunSpec with Services {
    it("first test", mytags.SlowAsMolasses) {}
    ignore("second test", mytags.SlowAsMolasses) {}
  }

  class NestedFunSpecExample extends AnyFunSpec with NestedTestNames {
    describe("A subject") {
      it("should first test", mytags.SlowAsMolasses) {}
      ignore("should second test", mytags.SlowAsMolasses) {}
    }
  }

  class DeeplyNestedFunSpecExample extends AnyFunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        it("should first test", mytags.SlowAsMolasses) {}
        ignore("should second test", mytags.SlowAsMolasses) {}
      }
    }
  }

  class FixtureFunSpecExample extends StringFixtureFunSpec with Services {
      it("first test", mytags.SlowAsMolasses) { s => }
      ignore("second test", mytags.SlowAsMolasses) { s => }
  }
  
  class NestedFixtureFunSpecExample extends StringFixtureFunSpec with NestedTestNames {
    describe("A subject") {
      it("should first test", mytags.SlowAsMolasses) { s => }
      ignore("should second test", mytags.SlowAsMolasses) { s => }
    }
  }

  class DeeplyNestedFixtureFunSpecExample extends StringFixtureFunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        it("should first test", mytags.SlowAsMolasses) { s => }
        ignore("should second test", mytags.SlowAsMolasses) { s => }
      }
    }
  }

  class PathFunSpecExample extends funspec.PathAnyFunSpec with Services {
    it("first test", mytags.SlowAsMolasses) {}
    ignore("second test", mytags.SlowAsMolasses) {}
    override def newInstance = new PathFunSpecExample
  }

  class NestedPathFunSpecExample extends funspec.PathAnyFunSpec with NestedTestNames {
    describe("A subject") {
      it("should first test", mytags.SlowAsMolasses) {}
      ignore("should second test", mytags.SlowAsMolasses) {}
    }
    override def newInstance = new NestedPathFunSpecExample
  }

  class DeeplyNestedPathFunSpecExample extends funspec.PathAnyFunSpec with DeeplyNestedTestNames {
    describe("A subject") {
      describe("when created") {
        it("should first test", mytags.SlowAsMolasses) {}
        ignore("should second test", mytags.SlowAsMolasses) {}
      }
    }
    override def newInstance = new DeeplyNestedPathFunSpecExample
  }

  class WordSpecExample extends AnyWordSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses) in {}
    "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
  }

  class NestedWordSpecExample extends AnyWordSpec with NestedTestNames {
    "A subject" should {
      "first test" taggedAs (mytags.SlowAsMolasses) in {}
      "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    }
  }

  class DeeplyNestedWordSpecExample extends AnyWordSpec with DeeplyNestedTestNames {
    "A subject" when {
      "created" should {
        "first test" taggedAs (mytags.SlowAsMolasses) in {}
        "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
      }
    }
  }

  class FixtureWordSpecExample extends StringFixtureWordSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
  }

  class NestedFixtureWordSpecExample extends StringFixtureWordSpec with NestedTestNames {
    "A subject" should {
      "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
      "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
    }
  }

  class DeeplyNestedFixtureWordSpecExample extends StringFixtureWordSpec with DeeplyNestedTestNames {
    "A subject" when {
      "created" should {
        "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
        "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
      }
    }
  }

  class NestedWordSpecWithMustExample extends AnyWordSpec with NestedTestNamesWithMust {
    "A subject" must {
      "first test" taggedAs (mytags.SlowAsMolasses) in {}
      "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    }
  }

  class DeeplyNestedWordSpecWithMustExample extends AnyWordSpec with DeeplyNestedTestNamesWithMust {
    "A subject" when {
      "created" must {
        "first test" taggedAs (mytags.SlowAsMolasses) in {}
        "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
      }
    }
  }

  class NestedFixtureWordSpecWithMustExample extends StringFixtureWordSpec with NestedTestNamesWithMust {
    "A subject" must {
      "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
      "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
    }
  }

  class DeeplyNestedFixtureWordSpecWithMustExample extends StringFixtureWordSpec with DeeplyNestedTestNamesWithMust {
    "A subject" when {
      "created" must {
        "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
        "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
      }
    }
  }

  class NestedWordSpecWithCanExample extends AnyWordSpec with NestedTestNamesWithCan {
    "A subject" can {
      "first test" taggedAs (mytags.SlowAsMolasses) in {}
      "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    }
  }

  class DeeplyNestedWordSpecWithCanExample extends AnyWordSpec with DeeplyNestedTestNamesWithCan {
    "A subject" when {
      "created" can {
        "first test" taggedAs (mytags.SlowAsMolasses) in {}
        "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
      }
    }
  }

  class NestedFixtureWordSpecWithCanExample extends StringFixtureWordSpec with NestedTestNamesWithCan {
    "A subject" can {
      "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
      "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
    }
  }

  class DeeplyNestedFixtureWordSpecWithCanExample extends StringFixtureWordSpec with DeeplyNestedTestNamesWithCan {
    "A subject" when {
      "created" can {
        "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
        "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
      }
    }
  }

  class FlatSpecExample extends AnyFlatSpec with Services {
    it should "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it should "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    override val theTestNames = Vector("should first test", "should second test")
   }

  class SubjectFlatSpecExample extends AnyFlatSpec with NestedTestNames {
    behavior of "A subject"
    it should "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it should "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
   }

  class ShorthandSubjectFlatSpecExample extends AnyFlatSpec with NestedTestNames {
    "A subject" should "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it should "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
   }

  class FixtureFlatSpecExample extends StringFixtureFlatSpec with Services {
    it should "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it should "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
    override val theTestNames = Vector("should first test", "should second test")
  }

  class SubjectFixtureFlatSpecExample extends StringFixtureFlatSpec with NestedTestNames {
    behavior of "A subject"
    it should "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it should "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
   }

  class ShorthandSubjectFixtureFlatSpecExample extends StringFixtureFlatSpec with NestedTestNames {
    "A subject" should "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it should "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
   }

  class FlatSpecWithMustExample extends AnyFlatSpec with Services {
    it must "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it must "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    override val theTestNames = Vector("must first test", "must second test")
   }

  class SubjectFlatSpecWithMustExample extends AnyFlatSpec with NestedTestNamesWithMust {
    behavior of "A subject"
    it must "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it must "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
   }

  class ShorthandSubjectFlatSpecWithMustExample extends AnyFlatSpec with NestedTestNamesWithMust {
    "A subject" must "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it must "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
   }

  class FixtureFlatSpecWithMustExample extends StringFixtureFlatSpec with Services {
    it must "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it must "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
    override val theTestNames = Vector("must first test", "must second test")
  }

  class SubjectFixtureFlatSpecWithMustExample extends StringFixtureFlatSpec with NestedTestNamesWithMust {
    behavior of "A subject"
    it must "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it must "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
   }

  class ShorthandSubjectFixtureFlatSpecWithMustExample extends StringFixtureFlatSpec with NestedTestNamesWithMust {
    "A subject" must "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it must "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
   }

  class FlatSpecWithCanExample extends AnyFlatSpec with Services {
    it can "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it can "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    override val theTestNames = Vector("can first test", "can second test")
   }

  class SubjectFlatSpecWithCanExample extends AnyFlatSpec with NestedTestNamesWithCan {
    behavior of "A subject"
    it can "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it can "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
   }

  class ShorthandSubjectFlatSpecWithCanExample extends AnyFlatSpec with NestedTestNamesWithCan {
    "A subject" can "first test" taggedAs (mytags.SlowAsMolasses) in {}
    it can "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
   }

  class FixtureFlatSpecWithCanExample extends StringFixtureFlatSpec with Services {
    it can "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it can "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
    override val theTestNames = Vector("can first test", "can second test")
  }

  class SubjectFixtureFlatSpecWithCanExample extends StringFixtureFlatSpec with NestedTestNamesWithCan {
    behavior of "A subject"
    it can "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it can "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
   }

  class ShorthandSubjectFixtureFlatSpecWithCanExample extends StringFixtureFlatSpec with NestedTestNamesWithCan {
    "A subject" can "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    it can "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
   }

  class FreeSpecExample extends AnyFreeSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses) in {}
    "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
  }

  class NestedFreeSpecExample extends AnyFreeSpec with NestedTestNames {
    "A subject" - {
      "should first test" taggedAs (mytags.SlowAsMolasses) in {}
      "should second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    }
  }

  class DeeplyNestedFreeSpecExample extends AnyFreeSpec with DeeplyNestedTestNames {
    "A subject" - {
      "when created" - {
        "should first test" taggedAs (mytags.SlowAsMolasses) in {}
        "should second test" taggedAs (mytags.SlowAsMolasses) ignore {}
      }
    }
  }

  class FixtureFreeSpecExample extends StringFixtureFreeSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    "second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
  }

  class NestedFixtureFreeSpecExample extends StringFixtureFreeSpec with NestedTestNames {
    "A subject" - {
      "should first test" taggedAs (mytags.SlowAsMolasses) in { s => }
      "should second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
    }
  }

  class DeeplyNestedFixtureFreeSpecExample extends StringFixtureFreeSpec with DeeplyNestedTestNames {
    "A subject" - {
      "when created" - {
        "should first test" taggedAs (mytags.SlowAsMolasses) in { s => }
        "should second test" taggedAs (mytags.SlowAsMolasses) ignore { s => }
      }
    }
  }

  class PathFreeSpecExample extends freespec.PathAnyFreeSpec with Services {
    "first test" taggedAs (mytags.SlowAsMolasses) in {}
    "second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    override def newInstance = new PathFreeSpecExample
  }

  class NestedPathFreeSpecExample extends freespec.PathAnyFreeSpec with NestedTestNames {
    "A subject" - {
      "should first test" taggedAs (mytags.SlowAsMolasses) in {}
      "should second test" taggedAs (mytags.SlowAsMolasses) ignore {}
    }
    override def newInstance = new NestedPathFreeSpecExample
  }

  class DeeplyNestedPathFreeSpecExample extends freespec.PathAnyFreeSpec with DeeplyNestedTestNames {
    "A subject" - {
      "when created" - {
        "should first test" taggedAs (mytags.SlowAsMolasses) in {}
        "should second test" taggedAs (mytags.SlowAsMolasses) ignore {}
      }
    }
    override def newInstance = new DeeplyNestedPathFreeSpecExample
  }

  class FeatureSpecExample extends AnyFeatureSpec with Services {
    Scenario("first test", mytags.SlowAsMolasses) {}
    ignore("second test", mytags.SlowAsMolasses) {}
    override val theTestNames = Vector("Scenario: first test", "Scenario: second test")
  }

  class NestedFeatureSpecExample extends AnyFeatureSpec with Services {
    Feature("A feature") {
      Scenario("first test", mytags.SlowAsMolasses) {}
      ignore("second test", mytags.SlowAsMolasses) {}
    }
    override val theTestNames = Vector("Feature: A feature Scenario: first test", "Feature: A feature Scenario: second test")
  }

  class FixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    Scenario("first test", mytags.SlowAsMolasses) { s => }
    ignore("second test", mytags.SlowAsMolasses) { s => }
    override val theTestNames = Vector("Scenario: first test", "Scenario: second test")
  }

  class NestedFixtureFeatureSpecExample extends StringFixtureFeatureSpec with Services {
    Feature("A feature") {
      Scenario("first test", mytags.SlowAsMolasses) { s => }
      ignore("second test", mytags.SlowAsMolasses) { s => }
    }
    override val theTestNames = Vector("Feature: A feature Scenario: first test", "Feature: A feature Scenario: second test")
  }

  class PropSpecExample extends AnyPropSpec with Services {
    property("first test", mytags.SlowAsMolasses) {}
    ignore("second test", mytags.SlowAsMolasses) {}
  }

  class FixturePropSpecExample extends StringFixturePropSpec with Services {
    property("first test", mytags.SlowAsMolasses) { s => }
    ignore("second test", mytags.SlowAsMolasses) { s => }
  }

  // SKIP-SCALATESTJS,NATIVE-START
  lazy val spec = new SpecExample
  // SKIP-SCALATESTJS,NATIVE-END
  lazy val funSuite = new FunSuiteExample
  lazy val fixtureFunSuite = new FixtureFunSuiteExample
  lazy val funSpec = new FunSpecExample
  lazy val nestedFunSpec = new NestedFunSpecExample
  lazy val deeplyNestedFunSpec = new DeeplyNestedFunSpecExample
  lazy val fixtureFunSpec = new FixtureFunSpecExample
  lazy val nestedFixtureFunSpec = new NestedFixtureFunSpecExample
  lazy val deeplyNestedFixtureFunSpec = new DeeplyNestedFixtureFunSpecExample
  lazy val pathFunSpec = new PathFunSpecExample
  lazy val nestedPathFunSpec = new NestedPathFunSpecExample
  lazy val deeplyNestedPathFunSpec = new DeeplyNestedPathFunSpecExample

  lazy val wordSpec = new WordSpecExample
  lazy val nestedWordSpec = new NestedWordSpecExample
  lazy val deeplyNestedWordSpec = new DeeplyNestedWordSpecExample
  lazy val fixtureWordSpec = new FixtureWordSpecExample
  lazy val nestedFixtureWordSpec = new NestedFixtureWordSpecExample
  lazy val deeplyNestedFixtureWordSpec = new DeeplyNestedFixtureWordSpecExample

  lazy val nestedWordSpecWithMust = new NestedWordSpecWithMustExample
  lazy val deeplyNestedWordSpecWithMust = new DeeplyNestedWordSpecWithMustExample
  lazy val nestedFixtureWordSpecWithMust = new NestedFixtureWordSpecWithMustExample
  lazy val deeplyNestedFixtureWordSpecWithMust = new DeeplyNestedFixtureWordSpecWithMustExample
  
  lazy val nestedWordSpecWithCan = new NestedWordSpecWithCanExample
  lazy val deeplyNestedWordSpecWithCan = new DeeplyNestedWordSpecWithCanExample
  lazy val nestedFixtureWordSpecWithCan = new NestedFixtureWordSpecWithCanExample
  lazy val deeplyNestedFixtureWordSpecWithCan = new DeeplyNestedFixtureWordSpecWithCanExample

  lazy val flatSpec = new FlatSpecExample
  lazy val subjectFlatSpec = new SubjectFlatSpecExample
  lazy val shorthandSubjectFlatSpec = new ShorthandSubjectFlatSpecExample
  lazy val fixtureFlatSpec = new FixtureFlatSpecExample
  lazy val subjectFixtureFlatSpec = new SubjectFixtureFlatSpecExample
  lazy val shorthandSubjectFixtureFlatSpec = new ShorthandSubjectFixtureFlatSpecExample

  lazy val flatSpecWithMust = new FlatSpecWithMustExample
  lazy val subjectFlatSpecWithMust = new SubjectFlatSpecWithMustExample
  lazy val shorthandSubjectFlatSpecWithMust = new ShorthandSubjectFlatSpecWithMustExample
  lazy val fixtureFlatSpecWithMust = new FixtureFlatSpecWithMustExample
  lazy val subjectFixtureFlatSpecWithMust = new SubjectFixtureFlatSpecWithMustExample
  lazy val shorthandSubjectFixtureFlatSpecWithMust = new ShorthandSubjectFixtureFlatSpecWithMustExample

  lazy val flatSpecWithCan = new FlatSpecWithCanExample
  lazy val subjectFlatSpecWithCan = new SubjectFlatSpecWithCanExample
  lazy val shorthandSubjectFlatSpecWithCan = new ShorthandSubjectFlatSpecWithCanExample
  lazy val fixtureFlatSpecWithCan = new FixtureFlatSpecWithCanExample
  lazy val subjectFixtureFlatSpecWithCan = new SubjectFixtureFlatSpecWithCanExample
  lazy val shorthandSubjectFixtureFlatSpecWithCan = new ShorthandSubjectFixtureFlatSpecWithCanExample

  lazy val freeSpec = new FreeSpecExample
  lazy val nestedFreeSpec = new NestedFreeSpecExample
  lazy val deeplyNestedFreeSpec = new DeeplyNestedFreeSpecExample
  lazy val fixtureFreeSpec = new FixtureFreeSpecExample
  lazy val nestedFixtureFreeSpec = new NestedFixtureFreeSpecExample
  lazy val deeplyNestedFixtureFreeSpec = new DeeplyNestedFixtureFreeSpecExample
  lazy val pathFreeSpec = new PathFreeSpecExample
  lazy val nestedPathFreeSpec = new NestedPathFreeSpecExample
  lazy val deeplyNestedPathFreeSpec = new DeeplyNestedPathFreeSpecExample
  lazy val featureSpec = new FeatureSpecExample
  lazy val nestedFeatureSpec = new NestedFeatureSpecExample
  lazy val fixtureFeatureSpec = new FixtureFeatureSpecExample
  lazy val nestedFixtureFeatureSpec = new NestedFixtureFeatureSpecExample
  lazy val propSpec = new PropSpecExample
  lazy val fixturePropSpec = new FixturePropSpecExample
   
  // Two ways to ignore in a flat spec, so add two more examples
  override def examples = super.examples ++ 
    Vector(
      new FlatSpecExample2,
      new FixtureFlatSpecExample2,
      new FlatSpecWithMustExample2,
      new FixtureFlatSpecWithMustExample2,
      new FlatSpecWithCanExample2,
      new FixtureFlatSpecWithCanExample2
    )

  class FlatSpecExample2 extends AnyFlatSpec with Services {
    it should "first test" taggedAs (mytags.SlowAsMolasses) in {}
    ignore should "second test" taggedAs (mytags.SlowAsMolasses) in {}
    override val theTestNames = Vector("should first test", "should second test")
   }

  class FixtureFlatSpecExample2 extends StringFixtureFlatSpec with Services {
    it should "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    ignore should "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
    override val theTestNames = Vector("should first test", "should second test")
  }

  class FlatSpecWithMustExample2 extends AnyFlatSpec with Services {
    it must "first test" taggedAs (mytags.SlowAsMolasses) in {}
    ignore must "second test" taggedAs (mytags.SlowAsMolasses) in {}
    override val theTestNames = Vector("must first test", "must second test")
   }

  class FixtureFlatSpecWithMustExample2 extends StringFixtureFlatSpec with Services {
    it must "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    ignore must "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
    override val theTestNames = Vector("must first test", "must second test")
  }

  class FlatSpecWithCanExample2 extends AnyFlatSpec with Services {
    it can "first test" taggedAs (mytags.SlowAsMolasses) in {}
    ignore can "second test" taggedAs (mytags.SlowAsMolasses) in {}
    override val theTestNames = Vector("can first test", "can second test")
   }

  class FixtureFlatSpecWithCanExample2 extends StringFixtureFlatSpec with Services {
    it can "first test" taggedAs (mytags.SlowAsMolasses) in { s => }
    ignore can "second test" taggedAs (mytags.SlowAsMolasses) in { s => }
    override val theTestNames = Vector("can first test", "can second test")
  }
}
