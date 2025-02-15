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
package org.scalatest.funsuite

import org.scalatest._
import org.scalatest.SharedHelpers._
import org.scalatest.events._
import org.scalactic.exceptions.NullArgumentException
import org.scalatest.exceptions.DuplicateTestNameException
import org.scalatest.exceptions.NotAllowedException
import org.scalatest.exceptions.TestFailedException
import org.scalatest.exceptions.TestRegistrationClosedException
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.funsuite.AnyFunSuite

class AnyFunSuiteSpec extends AnyFunSpec {

  describe("A FunSuite") {

    it("should return the test names in registration order from testNames") {
      
      val a = new AnyFunSuite {
        test("test this") { /* ASSERTION_SUCCEED */ }
        test("test that") { /* ASSERTION_SUCCEED */ }
      }

      assertResult(List("test this", "test that")) {
        a.testNames.iterator.toList
      }

      val b = new AnyFunSuite {}

      assertResult(List[String]()) {
        b.testNames.iterator.toList
      }

      val c = new AnyFunSuite {
        test("test that") { /* ASSERTION_SUCCEED */ }
        test("test this") { /* ASSERTION_SUCCEED */ }
      }

      assertResult(List("test that", "test this")) {
        c.testNames.iterator.toList
      }
    }

    it("should throw NotAllowedException if a duplicate test name registration is attempted") {

      intercept[DuplicateTestNameException] {
        new AnyFunSuite {
          test("test this") { /* ASSERTION_SUCCEED */ }
          test("test this") { /* ASSERTION_SUCCEED */ }
        }
      }
      intercept[DuplicateTestNameException] {
        new AnyFunSuite {
          test("test this") { /* ASSERTION_SUCCEED */ }
          ignore("test this") { /* ASSERTION_SUCCEED */ }
        }
      }
      intercept[DuplicateTestNameException] {
        new AnyFunSuite {
          ignore("test this") { /* ASSERTION_SUCCEED */ }
          ignore("test this") { /* ASSERTION_SUCCEED */ }
        }
      }
      intercept[DuplicateTestNameException] {
        new AnyFunSuite {
          ignore("test this") { /* ASSERTION_SUCCEED */ }
          test("test this") { /* ASSERTION_SUCCEED */ }
        }
      }
    }

    it("should throw NotAllowedException if test registration is attempted after run has been invoked on a suite") {
      class InvokedWhenNotRunningSuite extends AnyFunSuite {
        var fromMethodTestExecuted = false
        var fromConstructorTestExecuted = false
        test("from constructor") {
          fromConstructorTestExecuted = true
          /* ASSERTION_SUCCEED */
        }
        def tryToRegisterATest(): Unit = {
          test("from method") {
            fromMethodTestExecuted = true
            /* ASSERTION_SUCCEED */
          }
        }
      }
      val suite = new InvokedWhenNotRunningSuite
      suite.run(None, Args(SilentReporter))
      assert(suite.fromConstructorTestExecuted)
      assert(!suite.fromMethodTestExecuted)
      intercept[TestRegistrationClosedException] {
        suite.tryToRegisterATest()
      }
      suite.run(None, Args(SilentReporter))
      assert(!suite.fromMethodTestExecuted)
/*
      class InvokedWhenRunningSuite extends FunSuite {
        var fromMethodTestExecuted = false
        var fromConstructorTestExecuted = false
        test("from constructor") {
          tryToRegisterATest()
          fromConstructorTestExecuted = true
        }
        def tryToRegisterATest() {
          test("from method") {
            fromMethodTestExecuted = true
          }
        }
      }
      val a = new InvokedWhenNotRunningSuite
      a.run()
      intercept[TestFailedException] {
        new InvokedWhenRunningSuite
      } */
    }

    it("should invoke withFixture from runTest") {
      class SuiteA extends AnyFunSuite {
        var withFixtureWasInvoked = false
        var testWasInvoked = false
        override def withFixture(test: NoArgTest): Outcome = {
          withFixtureWasInvoked = true
          super.withFixture(test)
        }
        test("something") {
          testWasInvoked = true
          /* ASSERTION_SUCCEED */
        }
      }
      val a = new SuiteA

      import scala.language.reflectiveCalls

      a.run(None, Args(SilentReporter))
      assert(a.withFixtureWasInvoked)
      assert(a.testWasInvoked)
    }
    it("should pass the correct test name in the NoArgTest passed to withFixture") {
      class SuiteA extends AnyFunSuite {
        var correctTestNameWasPassed = false
        override def withFixture(test: NoArgTest): Outcome = {
          correctTestNameWasPassed = test.name == "something"
          super.withFixture(test)
        }
        test("something") {/* ASSERTION_SUCCEED */}
      }
      val a = new SuiteA

      import scala.language.reflectiveCalls

      a.run(None, Args(SilentReporter))
      assert(a.correctTestNameWasPassed)
    }
    it("should pass the correct config map in the NoArgTest passed to withFixture") {
      class SuiteA extends AnyFunSuite {
        var correctConfigMapWasPassed = false
        override def withFixture(test: NoArgTest): Outcome = {
          correctConfigMapWasPassed = (test.configMap == ConfigMap("hi" -> 7))
          super.withFixture(test)
        }
        test("something") { /* ASSERTION_SUCCEED */ }
      }
      val a = new SuiteA

      import scala.language.reflectiveCalls

      a.run(None, Args(SilentReporter, Stopper.default, Filter(), ConfigMap("hi" -> 7), None, new Tracker()))
      assert(a.correctConfigMapWasPassed)
    }

    describe("(with info calls)") {
      it("should, when the info appears in the body before a test, report the info before the test") {
        val msg = "hi there, dude"
        val testName = "test name"
        class MySuite extends AnyFunSuite {
          info(msg)
          test(testName) { /* ASSERTION_SUCCEED */ }
        }
        val (infoProvidedIndex, testStartingIndex, testSucceededIndex) =
          getIndexesForInformerEventOrderTests(new MySuite, testName, msg)
        assert(infoProvidedIndex < testStartingIndex)
        assert(testStartingIndex < testSucceededIndex)
      }
      it("should, when the info appears in the body after a test, report the info after the test runs") {
        val msg = "hi there, dude"
        val testName = "test name"
        class MySuite extends AnyFunSuite {
          test(testName) { /* ASSERTION_SUCCEED */ }
          info(msg)
        }
        val (infoProvidedIndex, testStartingIndex, testSucceededIndex) =
          getIndexesForInformerEventOrderTests(new MySuite, testName, msg)
        assert(testStartingIndex < testSucceededIndex)
        assert(testSucceededIndex < infoProvidedIndex)
      }
      it("should print to stdout when info is called by a method invoked after the suite has been executed") {
        class MySuite extends AnyFunSuite {
          callInfo() // This should work fine
          def callInfo(): Unit = {
            info("howdy")
          }
          test("howdy also") {
            callInfo() // This should work fine
            /* ASSERTION_SUCCEED */
          }
        }
        val suite = new MySuite
        val myRep = new EventRecordingReporter
        suite.run(None, Args(myRep))
        suite.callInfo() // TODO: Actually test that it prints to stdout
      }
    }
    it("should run tests registered via the testsFor syntax") {
      trait SharedFunSuiteTests { this: AnyFunSuite =>
        def nonEmptyStack(s: String)(i: Int): Unit = {
          test("I am shared") { /* ASSERTION_SUCCEED */ }
        }
      }
      class MySuite extends AnyFunSuite with SharedFunSuiteTests {
        testsFor(nonEmptyStack("hi")(1))
      }
      val suite = new MySuite
      val reporter = new EventRecordingReporter
      suite.run(None, Args(reporter))

      val indexedList = reporter.eventsReceived

      val testStartingOption = indexedList.find(_.isInstanceOf[TestStarting])
      assert(testStartingOption.isDefined)
      assert(testStartingOption.get.asInstanceOf[TestStarting].testName === "I am shared")
    }
    it("should throw NullArgumentException if a null test tag is provided") {
      // test
      intercept[NullArgumentException] {
        new AnyFunSuite {
          test("hi", null) { /* ASSERTION_SUCCEED */ }
        }
      }
      val caught = intercept[NullArgumentException] {
        new AnyFunSuite {
          test("hi", mytags.SlowAsMolasses, null) { /* ASSERTION_SUCCEED */ }
        }
      }
      assert(caught.getMessage == "a test tag was null")
      intercept[NullArgumentException] {
        new AnyFunSuite {
          test("hi", mytags.SlowAsMolasses, null, mytags.WeakAsAKitten) { /* ASSERTION_SUCCEED */ }
        }
      }

      // ignore
      intercept[NullArgumentException] {
        new AnyFunSuite {
          ignore("hi", null) { /* ASSERTION_SUCCEED */ }
        }
      }
      val caught2 = intercept[NullArgumentException] {
        new AnyFunSuite {
          ignore("hi", mytags.SlowAsMolasses, null) { /* ASSERTION_SUCCEED */ }
        }
      }
      assert(caught2.getMessage == "a test tag was null")
      intercept[NullArgumentException] {
        new AnyFunSuite {
          ignore("hi", mytags.SlowAsMolasses, null, mytags.WeakAsAKitten) { /* ASSERTION_SUCCEED */ }
        }
      }

      // registerTest
      intercept[NullArgumentException] {
        new AnyFunSuite {
          registerTest("hi", null) { /* ASSERTION_SUCCEED */ }
        }
      }
      val caught3 = intercept[NullArgumentException] {
        new AnyFunSuite {
          registerTest("hi", mytags.SlowAsMolasses, null) { /* ASSERTION_SUCCEED */ }
        }
      }
      assert(caught3.getMessage == "a test tag was null")
      intercept[NullArgumentException] {
        new AnyFunSuite {
          registerTest("hi", mytags.SlowAsMolasses, null, mytags.WeakAsAKitten) { /* ASSERTION_SUCCEED */ }
        }
      }

      // registerIgnoredTest
      intercept[NullArgumentException] {
        new AnyFunSuite {
          registerIgnoredTest("hi", null) { /* ASSERTION_SUCCEED */ }
        }
      }
      val caught4 = intercept[NullArgumentException] {
        new AnyFunSuite {
          registerIgnoredTest("hi", mytags.SlowAsMolasses, null) { /* ASSERTION_SUCCEED */ }
        }
      }
      assert(caught4.getMessage == "a test tag was null")
      intercept[NullArgumentException] {
        new AnyFunSuite {
          registerIgnoredTest("hi", mytags.SlowAsMolasses, null, mytags.WeakAsAKitten) { /* ASSERTION_SUCCEED */ }
        }
      }
    }

    class TestWasCalledSuite extends AnyFunSuite {
      var theTestThisCalled = false
      var theTestThatCalled = false
      test("this") {
        theTestThisCalled = true
        /* ASSERTION_SUCCEED */
      }
      test("that") {
        theTestThatCalled = true
        /* ASSERTION_SUCCEED */
      }
    }

    it("should execute all tests when run is called with testName None") {

      val b = new TestWasCalledSuite
      b.run(None, Args(SilentReporter))
      assert(b.theTestThisCalled)
      assert(b.theTestThatCalled)
    }

    it("should execute one test when run is called with a defined testName") {

      val a = new TestWasCalledSuite
      a.run(Some("this"), Args(SilentReporter))
      assert(a.theTestThisCalled)
      assert(!a.theTestThatCalled)
    }

    it("should report as ignored, and not run, tests marked ignored") {

      class SuiteA extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        test("test this") {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val a = new SuiteA

      import scala.language.reflectiveCalls

      val repA = new TestIgnoredTrackingReporter
      a.run(None, Args(repA))
      assert(!repA.testIgnoredReceived)
      assert(a.theTestThisCalled)
      assert(a.theTestThatCalled)

      class SuiteB extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        ignore("test this") {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val b = new SuiteB

      val repB = new TestIgnoredTrackingReporter
      b.run(None, Args(repB))
      assert(repB.testIgnoredReceived)
      assert(repB.lastEvent.isDefined)
      assert(repB.lastEvent.get.testName endsWith "test this")
      assert(!b.theTestThisCalled)
      assert(b.theTestThatCalled)

      class SuiteC extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        test("test this") {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        ignore("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val c = new SuiteC

      val repC = new TestIgnoredTrackingReporter
      c.run(None, Args(repC))
      assert(repC.testIgnoredReceived)
      assert(repC.lastEvent.isDefined)
      assert(repC.lastEvent.get.testName endsWith "test that", repC.lastEvent.get.testName)
      assert(c.theTestThisCalled)
      assert(!c.theTestThatCalled)

      // The order I want is order of appearance in the file.
      // Will try and implement that tomorrow. Subtypes will be able to change the order.
      class SuiteD extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        ignore("test this") {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        ignore("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val d = new SuiteD

      val repD = new TestIgnoredTrackingReporter
      d.run(None, Args(repD))
      assert(repD.testIgnoredReceived)
      assert(repD.lastEvent.isDefined)
      assert(repD.lastEvent.get.testName endsWith "test that") // last because should be in order of appearance
      assert(!d.theTestThisCalled)
      assert(!d.theTestThatCalled)
    }

    it("should ignore a test marked as ignored if run is invoked with that testName") {
      class SuiteE extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        ignore("test this") {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }

      val e = new SuiteE

      import scala.language.reflectiveCalls

      val repE = new TestIgnoredTrackingReporter
      e.run(Some("test this"), Args(repE))
      assert(repE.testIgnoredReceived)
      assert(!e.theTestThisCalled)
      assert(!e.theTestThatCalled)
    }

    it("should exclude a test with a tag included in the tagsToExclude set even if run is invoked with that testName") {
      class SuiteE extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        test("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val e = new SuiteE

      import scala.language.reflectiveCalls

      val repE = new TestIgnoredTrackingReporter
      e.run(Some("test this"), Args(repE, Stopper.default, Filter(None, Set("org.scalatest.SlowAsMolasses")), ConfigMap.empty, None, new Tracker))
      assert(!repE.testIgnoredReceived)
      assert(!e.theTestThisCalled)
      assert(!e.theTestThatCalled)
    }

    it("should exclude a registered test with a tag included in the tagsToExclude set even if run is invoked with that testName") {
      class SuiteE extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        registerTest("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val e = new SuiteE

      import scala.language.reflectiveCalls

      val repE = new TestIgnoredTrackingReporter
      e.run(Some("test this"), Args(repE, Stopper.default, Filter(None, Set("org.scalatest.SlowAsMolasses")), ConfigMap.empty, None, new Tracker))
      assert(!repE.testIgnoredReceived)
      assert(!e.theTestThisCalled)
      assert(!e.theTestThatCalled)
    }

    it("should run only those tests selected by the tags to include and exclude sets") {

      // Nothing is excluded
      class SuiteA extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        test("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val a = new SuiteA

      import scala.language.reflectiveCalls

      val repA = new TestIgnoredTrackingReporter
      a.run(None, Args(repA))
      assert(!repA.testIgnoredReceived)
      assert(a.theTestThisCalled)
      assert(a.theTestThatCalled)

      // SlowAsMolasses is included, one test should be excluded
      class SuiteB extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        test("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val b = new SuiteB
      val repB = new TestIgnoredTrackingReporter
      b.run(None, Args(repB, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set()), ConfigMap.empty, None, new Tracker))
      assert(!repB.testIgnoredReceived)
      assert(b.theTestThisCalled)
      assert(!b.theTestThatCalled)

      // SlowAsMolasses is included, and both tests should be included
      class SuiteC extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        test("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val c = new SuiteC
      val repC = new TestIgnoredTrackingReporter
      c.run(None, Args(repB, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set()), ConfigMap.empty, None, new Tracker))
      assert(!repC.testIgnoredReceived)
      assert(c.theTestThisCalled)
      assert(c.theTestThatCalled)

      // SlowAsMolasses is included. both tests should be included but one ignored
      class SuiteD extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        ignore("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val d = new SuiteD
      val repD = new TestIgnoredTrackingReporter
      d.run(None, Args(repD, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.Ignore")), ConfigMap.empty, None, new Tracker))
      assert(repD.testIgnoredReceived)
      assert(!d.theTestThisCalled)
      assert(d.theTestThatCalled)

      // SlowAsMolasses included, FastAsLight excluded
      class SuiteE extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        test("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val e = new SuiteE
      val repE = new TestIgnoredTrackingReporter
      e.run(None, Args(repE, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
                ConfigMap.empty, None, new Tracker))
      assert(!repE.testIgnoredReceived)
      assert(!e.theTestThisCalled)
      assert(e.theTestThatCalled)
      assert(!e.theTestTheOtherCalled)

      // An Ignored test that was both included and excluded should not generate a TestIgnored event
      class SuiteF extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        ignore("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val f = new SuiteF
      val repF = new TestIgnoredTrackingReporter
      f.run(None, Args(repF, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
                ConfigMap.empty, None, new Tracker))
      assert(!repF.testIgnoredReceived)
      assert(!f.theTestThisCalled)
      assert(f.theTestThatCalled)
      assert(!f.theTestTheOtherCalled)

      // An Ignored test that was not included should not generate a TestIgnored event
      class SuiteG extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        test("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        ignore("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val g = new SuiteG
      val repG = new TestIgnoredTrackingReporter
      g.run(None, Args(repG, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
                ConfigMap.empty, None, new Tracker))
      assert(!repG.testIgnoredReceived)
      assert(!g.theTestThisCalled)
      assert(g.theTestThatCalled)
      assert(!g.theTestTheOtherCalled)

      // No tagsToInclude set, FastAsLight excluded
      class SuiteH extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        test("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val h = new SuiteH
      val repH = new TestIgnoredTrackingReporter
      h.run(None, Args(repH, Stopper.default, Filter(None, Set("org.scalatest.FastAsLight")), ConfigMap.empty, None, new Tracker))
      assert(!repH.testIgnoredReceived)
      assert(!h.theTestThisCalled)
      assert(h.theTestThatCalled)
      assert(h.theTestTheOtherCalled)

      // No tagsToInclude set, SlowAsMolasses excluded
      class SuiteI extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        test("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val i = new SuiteI
      val repI = new TestIgnoredTrackingReporter
      i.run(None, Args(repI, Stopper.default, Filter(None, Set("org.scalatest.SlowAsMolasses")), ConfigMap.empty, None, new Tracker))
      assert(!repI.testIgnoredReceived)
      assert(!i.theTestThisCalled)
      assert(!i.theTestThatCalled)
      assert(i.theTestTheOtherCalled)

      // No tagsToInclude set, SlowAsMolasses excluded, TestIgnored should not be received on excluded ones
      class SuiteJ extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        ignore("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        ignore("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        test("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val j = new SuiteJ
      val repJ = new TestIgnoredTrackingReporter
      j.run(None, Args(repJ, Stopper.default, Filter(None, Set("org.scalatest.SlowAsMolasses")), ConfigMap.empty, None, new Tracker))
      assert(!repI.testIgnoredReceived)
      assert(!j.theTestThisCalled)
      assert(!j.theTestThatCalled)
      assert(j.theTestTheOtherCalled)

      // Same as previous, except Ignore specifically mentioned in excludes set
      class SuiteK extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        ignore("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        ignore("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        ignore("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val k = new SuiteK
      val repK = new TestIgnoredTrackingReporter
      k.run(None, Args(repK, Stopper.default, Filter(None, Set("org.scalatest.SlowAsMolasses", "org.scalatest.Ignore")), ConfigMap.empty, None, new Tracker))
      assert(repK.testIgnoredReceived)
      assert(!k.theTestThisCalled)
      assert(!k.theTestThatCalled)
      assert(!k.theTestTheOtherCalled)
    }

    it("should run only those registered tests selected by the tags to include and exclude sets") {

      // Nothing is excluded
      class SuiteA extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        registerTest("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val a = new SuiteA

      import scala.language.reflectiveCalls

      val repA = new TestIgnoredTrackingReporter
      a.run(None, Args(repA))
      assert(!repA.testIgnoredReceived)
      assert(a.theTestThisCalled)
      assert(a.theTestThatCalled)

      // SlowAsMolasses is included, one test should be excluded
      class SuiteB extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        registerTest("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that") {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val b = new SuiteB
      val repB = new TestIgnoredTrackingReporter
      b.run(None, Args(repB, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set()), ConfigMap.empty, None, new Tracker))
      assert(!repB.testIgnoredReceived)
      assert(b.theTestThisCalled)
      assert(!b.theTestThatCalled)

      // SlowAsMolasses is included, and both tests should be included
      class SuiteC extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        registerTest("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val c = new SuiteC
      val repC = new TestIgnoredTrackingReporter
      c.run(None, Args(repB, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set()), ConfigMap.empty, None, new Tracker))
      assert(!repC.testIgnoredReceived)
      assert(c.theTestThisCalled)
      assert(c.theTestThatCalled)

      // SlowAsMolasses is included. both tests should be included but one ignored
      class SuiteD extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        registerIgnoredTest("test this", mytags.SlowAsMolasses) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val d = new SuiteD
      val repD = new TestIgnoredTrackingReporter
      d.run(None, Args(repD, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.Ignore")), ConfigMap.empty, None, new Tracker))
      assert(repD.testIgnoredReceived)
      assert(!d.theTestThisCalled)
      assert(d.theTestThatCalled)

      // SlowAsMolasses included, FastAsLight excluded
      class SuiteE extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        registerTest("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val e = new SuiteE
      val repE = new TestIgnoredTrackingReporter
      e.run(None, Args(repE, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
        ConfigMap.empty, None, new Tracker))
      assert(!repE.testIgnoredReceived)
      assert(!e.theTestThisCalled)
      assert(e.theTestThatCalled)
      assert(!e.theTestTheOtherCalled)

      // An Ignored test that was both included and excluded should not generate a TestIgnored event
      class SuiteF extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        registerIgnoredTest("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val f = new SuiteF
      val repF = new TestIgnoredTrackingReporter
      f.run(None, Args(repF, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
        ConfigMap.empty, None, new Tracker))
      assert(!repF.testIgnoredReceived)
      assert(!f.theTestThisCalled)
      assert(f.theTestThatCalled)
      assert(!f.theTestTheOtherCalled)

      // An Ignored test that was not included should not generate a TestIgnored event
      class SuiteG extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        registerTest("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerIgnoredTest("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val g = new SuiteG
      val repG = new TestIgnoredTrackingReporter
      g.run(None, Args(repG, Stopper.default, Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight")),
        ConfigMap.empty, None, new Tracker))
      assert(!repG.testIgnoredReceived)
      assert(!g.theTestThisCalled)
      assert(g.theTestThatCalled)
      assert(!g.theTestTheOtherCalled)

      // No tagsToInclude set, FastAsLight excluded
      class SuiteH extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        registerTest("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val h = new SuiteH
      val repH = new TestIgnoredTrackingReporter
      h.run(None, Args(repH, Stopper.default, Filter(None, Set("org.scalatest.FastAsLight")), ConfigMap.empty, None, new Tracker))
      assert(!repH.testIgnoredReceived)
      assert(!h.theTestThisCalled)
      assert(h.theTestThatCalled)
      assert(h.theTestTheOtherCalled)

      // No tagsToInclude set, SlowAsMolasses excluded
      class SuiteI extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        registerTest("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val i = new SuiteI
      val repI = new TestIgnoredTrackingReporter
      i.run(None, Args(repI, Stopper.default, Filter(None, Set("org.scalatest.SlowAsMolasses")), ConfigMap.empty, None, new Tracker))
      assert(!repI.testIgnoredReceived)
      assert(!i.theTestThisCalled)
      assert(!i.theTestThatCalled)
      assert(i.theTestTheOtherCalled)

      // No tagsToInclude set, SlowAsMolasses excluded, TestIgnored should not be received on excluded ones
      class SuiteJ extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        registerIgnoredTest("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerIgnoredTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerTest("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val j = new SuiteJ
      val repJ = new TestIgnoredTrackingReporter
      j.run(None, Args(repJ, Stopper.default, Filter(None, Set("org.scalatest.SlowAsMolasses")), ConfigMap.empty, None, new Tracker))
      assert(!repI.testIgnoredReceived)
      assert(!j.theTestThisCalled)
      assert(!j.theTestThatCalled)
      assert(j.theTestTheOtherCalled)

      // Same as previous, except Ignore specifically mentioned in excludes set
      class SuiteK extends AnyFunSuite {
        var theTestThisCalled = false
        var theTestThatCalled = false
        var theTestTheOtherCalled = false
        registerIgnoredTest("test this", mytags.SlowAsMolasses, mytags.FastAsLight) {
          theTestThisCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerIgnoredTest("test that", mytags.SlowAsMolasses) {
          theTestThatCalled = true
          /* ASSERTION_SUCCEED */
        }
        registerIgnoredTest("test the other") {
          theTestTheOtherCalled = true
          /* ASSERTION_SUCCEED */
        }
      }
      val k = new SuiteK
      val repK = new TestIgnoredTrackingReporter
      k.run(None, Args(repK, Stopper.default, Filter(None, Set("org.scalatest.SlowAsMolasses", "org.scalatest.Ignore")), ConfigMap.empty, None, new Tracker))
      assert(repK.testIgnoredReceived)
      assert(!k.theTestThisCalled)
      assert(!k.theTestThatCalled)
      assert(!k.theTestTheOtherCalled)
    }
    
    it("should return the correct test count from its expectedTestCount method") {

      class SuiteA extends AnyFunSuite {
        test("test this") {/* ASSERTION_SUCCEED */}
        test("test that") {/* ASSERTION_SUCCEED */}
      }
      val a = new SuiteA
      assert(a.expectedTestCount(Filter()) == 2)

      class SuiteB extends AnyFunSuite {
        ignore("test this") {/* ASSERTION_SUCCEED */}
        test("test that") {/* ASSERTION_SUCCEED */}
      }
      val b = new SuiteB
      assert(b.expectedTestCount(Filter()) == 1)

      class SuiteC extends AnyFunSuite {
        test("test this", mytags.FastAsLight) {/* ASSERTION_SUCCEED */}
        test("test that") {/* ASSERTION_SUCCEED */}
      }
      val c = new SuiteC
      assert(c.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) == 1)
      assert(c.expectedTestCount(Filter(None, Set("org.scalatest.FastAsLight"))) == 1)

      class SuiteD extends AnyFunSuite {
        test("test this", mytags.FastAsLight, mytags.SlowAsMolasses) {/* ASSERTION_SUCCEED */}
        test("test that", mytags.SlowAsMolasses) {/* ASSERTION_SUCCEED */}
        test("test the other thing") {/* ASSERTION_SUCCEED */}
      }
      val d = new SuiteD
      assert(d.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) == 1)
      assert(d.expectedTestCount(Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight"))) == 1)
      assert(d.expectedTestCount(Filter(None, Set("org.scalatest.SlowAsMolasses"))) == 1)
      assert(d.expectedTestCount(Filter()) == 3)

      class SuiteE extends AnyFunSuite {
        test("test this", mytags.FastAsLight, mytags.SlowAsMolasses) {/* ASSERTION_SUCCEED */}
        test("test that", mytags.SlowAsMolasses) {/* ASSERTION_SUCCEED */}
        ignore("test the other thing") {/* ASSERTION_SUCCEED */}
      }
      val e = new SuiteE
      assert(e.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) == 1)
      assert(e.expectedTestCount(Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight"))) == 1)
      assert(e.expectedTestCount(Filter(None, Set("org.scalatest.SlowAsMolasses"))) == 0)
      assert(e.expectedTestCount(Filter()) == 2)

      val f = new Suites(a, b, c, d, e)
      assert(f.expectedTestCount(Filter()) == 10)
    }
    it("should return the correct test count from its expectedTestCount method when uses registerTest and registerIgnoredTest to register test") {

      class SuiteA extends AnyFunSuite {
        registerTest("test this") {/* ASSERTION_SUCCEED */}
        registerTest("test that") {/* ASSERTION_SUCCEED */}
      }
      val a = new SuiteA
      assert(a.expectedTestCount(Filter()) == 2)

      class SuiteB extends AnyFunSuite {
        registerIgnoredTest("test this") {/* ASSERTION_SUCCEED */}
        registerTest("test that") {/* ASSERTION_SUCCEED */}
      }
      val b = new SuiteB
      assert(b.expectedTestCount(Filter()) == 1)

      class SuiteC extends AnyFunSuite {
        registerTest("test this", mytags.FastAsLight) {/* ASSERTION_SUCCEED */}
        registerTest("test that") {/* ASSERTION_SUCCEED */}
      }
      val c = new SuiteC
      assert(c.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) == 1)
      assert(c.expectedTestCount(Filter(None, Set("org.scalatest.FastAsLight"))) == 1)

      class SuiteD extends AnyFunSuite {
        registerTest("test this", mytags.FastAsLight, mytags.SlowAsMolasses) {/* ASSERTION_SUCCEED */}
        registerTest("test that", mytags.SlowAsMolasses) {/* ASSERTION_SUCCEED */}
        registerTest("test the other thing") {/* ASSERTION_SUCCEED */}
      }
      val d = new SuiteD
      assert(d.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) == 1)
      assert(d.expectedTestCount(Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight"))) == 1)
      assert(d.expectedTestCount(Filter(None, Set("org.scalatest.SlowAsMolasses"))) == 1)
      assert(d.expectedTestCount(Filter()) == 3)

      class SuiteE extends AnyFunSuite {
        registerTest("test this", mytags.FastAsLight, mytags.SlowAsMolasses) {/* ASSERTION_SUCCEED */}
        registerTest("test that", mytags.SlowAsMolasses) {/* ASSERTION_SUCCEED */}
        registerIgnoredTest("test the other thing") {/* ASSERTION_SUCCEED */}
      }
      val e = new SuiteE
      assert(e.expectedTestCount(Filter(Some(Set("org.scalatest.FastAsLight")), Set())) == 1)
      assert(e.expectedTestCount(Filter(Some(Set("org.scalatest.SlowAsMolasses")), Set("org.scalatest.FastAsLight"))) == 1)
      assert(e.expectedTestCount(Filter(None, Set("org.scalatest.SlowAsMolasses"))) == 0)
      assert(e.expectedTestCount(Filter()) == 2)

      val f = new Suites(a, b, c, d, e)
      assert(f.expectedTestCount(Filter()) == 10)
    }
    it("should generate a TestPending message when the test body is (pending)") {
      class SuiteA extends AnyFunSuite {

        test("should do this") (pending)

        test("should do that") {
          assert(2 + 2 === 4)
        }

        test("should do something else") {
          assert(2 + 2 === 4)
          pending
        }
      }
      val a = new SuiteA
      val rep = new EventRecordingReporter
      a.run(None, Args(rep))
      val tp = rep.testPendingEventsReceived
      assert(tp.size === 2)
    }
    it("should generate a test failure if a Throwable, or an Error other than direct Error subtypes " +
            "known in JDK 1.5, excluding AssertionError") {
      class SuiteA extends AnyFunSuite {
        test("throws AssertionError") { throw new AssertionError }
        test("throws plain old Error") { throw new Error }
        test("throws Throwable") { throw new Throwable }
      }        
      val a = new SuiteA
      val rep = new EventRecordingReporter
      a.run(None, Args(rep))
      val tf = rep.testFailedEventsReceived
      assert(tf.size === 3)
    }
    // SKIP-SCALATESTJS,NATIVE-START
    it("should propagate out Errors that are direct subtypes of Error in JDK 1.5, other than " +
            "AssertionError, causing Suites and Runs to abort.") {
      val a = new AnyFunSuite {
        test("throws AssertionError") { throw new OutOfMemoryError }
      }
      intercept[OutOfMemoryError] {
        a.run(None, Args(SilentReporter))
      }
    }
    // SKIP-SCALATESTJS,NATIVE-END
    describe("(when a nesting rule has been violated)") {

      it("should, if they call a nested it from within an it clause, result in a TestFailedException when running the test") {

        class MySuite extends AnyFunSuite {
          test("should blow up") {
            test("should never run") {
              assert(1 === 1)
            }
            /* ASSERTION_SUCCEED */
          }
        }

        val spec = new MySuite
        ensureTestFailedEventReceived(spec, "should blow up")
      }
      it("should, if they call a nested it with tags from within an it clause, result in a TestFailedException when running the test") {

        class MySuite extends AnyFunSuite {
          test("should blow up") {
            test("should never run", mytags.SlowAsMolasses) {
              assert(1 == 1)
            }
            /* ASSERTION_SUCCEED */
          }
        }

        val spec = new MySuite
        ensureTestFailedEventReceived(spec, "should blow up")
      }
      it("should, if they call a nested registerTest with tags from within a registerTest clause, result in a TestFailedException when running the test") {

        class MySuite extends AnyFunSuite {
          registerTest("should blow up") {
            registerTest("should never run", mytags.SlowAsMolasses) {
              assert(1 == 1)
            }
            /* ASSERTION_SUCCEED */
          }
        }

        val spec = new MySuite
        ensureTestFailedEventReceived(spec, "should blow up")
      }
      it("should, if they call a nested ignore from within an it clause, result in a TestFailedException when running the test") {

        class MySuite extends AnyFunSuite {
          test("should blow up") {
            ignore("should never run") {
              assert(1 === 1)
            }
            /* ASSERTION_SUCCEED */
          }
        }

        val spec = new MySuite
        ensureTestFailedEventReceived(spec, "should blow up")
      }
      it("should, if they call a nested ignore with tags from within an it clause, result in a TestFailedException when running the test") {

        class MySuite extends AnyFunSuite {
          test("should blow up") {
            ignore("should never run", mytags.SlowAsMolasses) {
              assert(1 == 1)
            }
            /* ASSERTION_SUCCEED */
          }
        }

        val spec = new MySuite
        ensureTestFailedEventReceived(spec, "should blow up")
      }
      it("should, if they call a nested registerIgnoredTest with tags from within a registerTest clause, result in a TestFailedException when running the test") {

        class MySuite extends AnyFunSuite {
          registerTest("should blow up") {
            registerIgnoredTest("should never run", mytags.SlowAsMolasses) {
              assert(1 == 1)
            }
            /* ASSERTION_SUCCEED */
          }
        }

        val spec = new MySuite
        ensureTestFailedEventReceived(spec, "should blow up")
      }
    }

    it("should throw IllegalArgumentException if passed a testName that doesn't exist") {
      class MySuite extends AnyFunSuite {
        test("one") {/* ASSERTION_SUCCEED */}
        test("two") {/* ASSERTION_SUCCEED */}
      }
      val suite = new MySuite
      intercept[IllegalArgumentException] {
        suite.run(Some("three"), Args(SilentReporter))
      }
    }

    describe("registerTest and registerIgnoredTest method") {

      it("should allow test registration and ignored test registration") {
        class TestSpec extends AnyFunSuite {
          val a = 1
          registerTest("test 1") {
            val e = intercept[TestFailedException] {
              assert(a == 2)
            }
            assert(e.message == Some("1 did not equal 2"))
            assert(e.failedCodeFileName == Some("AnyFunSuiteSpec.scala"))
            assert(e.failedCodeLineNumber == Some(thisLineNumber - 4))
          }
          registerTest("test 2") {
            assert(a == 2)
          }
          registerTest("test 3") {
            pending
          }
          registerTest("test 4") {
            cancel()
          }
          registerIgnoredTest("test 5") {
            assert(a == 2)
          }
        }

        val rep = new EventRecordingReporter
        val s = new TestSpec
        s.run(None, Args(rep))

        assert(rep.testStartingEventsReceived.length == 4)
        assert(rep.testSucceededEventsReceived.length == 1)
        assert(rep.testSucceededEventsReceived(0).testName == "test 1")
        assert(rep.testFailedEventsReceived.length == 1)
        assert(rep.testFailedEventsReceived(0).testName == "test 2")
        assert(rep.testPendingEventsReceived.length == 1)
        assert(rep.testPendingEventsReceived(0).testName == "test 3")
        assert(rep.testCanceledEventsReceived.length == 1)
        assert(rep.testCanceledEventsReceived(0).testName == "test 4")
        assert(rep.testIgnoredEventsReceived.length == 1)
        assert(rep.testIgnoredEventsReceived(0).testName == "test 5")
      }

      it("should generate TestRegistrationClosedException with correct stack depth info when has a registerTest nested inside a registerTest") {
        class TestSpec extends AnyFunSuite {
          var registrationClosedThrown = false
          registerTest("a scenario") {
            registerTest("nested scenario") {
              assert(1 == 2)
            }; /* ASSERTION_SUCCEED */
          }
          override def withFixture(test: NoArgTest): Outcome = {
            val outcome = test.apply()
            outcome match {
              case Exceptional(ex: TestRegistrationClosedException) =>
                registrationClosedThrown = true
              case _ =>
            }
            outcome
          }
        }
        val rep = new EventRecordingReporter
        val s = new TestSpec
        s.run(None, Args(rep))
        assert(s.registrationClosedThrown == true)
        val testFailedEvents = rep.testFailedEventsReceived
        assert(testFailedEvents.size === 1)
        assert(testFailedEvents(0).throwable.get.getClass() === classOf[TestRegistrationClosedException])
        val trce = testFailedEvents(0).throwable.get.asInstanceOf[TestRegistrationClosedException]
        assert("AnyFunSuiteSpec.scala" === trce.failedCodeFileName.get)
        assert(trce.failedCodeLineNumber.get === thisLineNumber - 23)
        assert(trce.message == Some("Test cannot be nested inside another test."))
      }

      it("should generate TestRegistrationClosedException with correct stack depth info when has an registerIgnoredTest nested inside a registerTest") {
        class TestSpec extends AnyFunSuite {
          var registrationClosedThrown = false
          registerTest("a scenario") {
            registerIgnoredTest("nested scenario") {
              assert(1 == 2)
            }; /* ASSERTION_SUCCEED */
          }
          override def withFixture(test: NoArgTest): Outcome = {
            val outcome = test.apply()
            outcome match {
              case Exceptional(ex: TestRegistrationClosedException) =>
                registrationClosedThrown = true
              case _ =>
            }
            outcome
          }
        }
        val rep = new EventRecordingReporter
        val s = new TestSpec
        s.run(None, Args(rep))
        assert(s.registrationClosedThrown == true)
        val testFailedEvents = rep.testFailedEventsReceived
        assert(testFailedEvents.size === 1)
        assert(testFailedEvents(0).throwable.get.getClass() === classOf[TestRegistrationClosedException])
        val trce = testFailedEvents(0).throwable.get.asInstanceOf[TestRegistrationClosedException]
        assert("AnyFunSuiteSpec.scala" === trce.failedCodeFileName.get)
        assert(trce.failedCodeLineNumber.get === thisLineNumber - 23)
        assert(trce.message == Some("Test cannot be nested inside another test."))
      }
    }

    ignore("should support expectations") { // Unignore after we uncomment the expectation implicits in RegistrationPolicy
      class TestSpec extends AnyFunSuite with expectations.Expectations {
        test("fail scenario") {
          expect(1 === 2); /* ASSERTION_SUCCEED */
        }
        test("nested fail scenario") {
          expect(1 === 2); /* ASSERTION_SUCCEED */
        }
      }
      val rep = new EventRecordingReporter
      val s1 = new TestSpec
      s1.run(None, Args(rep))
      assert(rep.testFailedEventsReceived.size === 2)
      assert(rep.testFailedEventsReceived(0).throwable.get.asInstanceOf[TestFailedException].failedCodeFileName.get === "AnyFunSuiteSpec.scala")
      assert(rep.testFailedEventsReceived(0).throwable.get.asInstanceOf[TestFailedException].failedCodeLineNumber.get === thisLineNumber - 11)
      assert(rep.testFailedEventsReceived(1).throwable.get.asInstanceOf[TestFailedException].failedCodeFileName.get === "AnyFunSuiteSpec.scala")
      assert(rep.testFailedEventsReceived(1).throwable.get.asInstanceOf[TestFailedException].failedCodeLineNumber.get === thisLineNumber - 10)
    }
  }
  
  describe("when failure happens") {
    it("should fire TestFailed event with correct stack depth info when test failed") {
      class TestSpec extends AnyFunSuite {
        test("fail scenario") {
          assert(1 === 2); /* ASSERTION_SUCCEED */
        }
      }
      val rep = new EventRecordingReporter
      val s1 = new TestSpec
      s1.run(None, Args(rep))
      assert(rep.testFailedEventsReceived.size === 1)
      assert(rep.testFailedEventsReceived(0).throwable.get.asInstanceOf[TestFailedException].failedCodeFileName.get === "AnyFunSuiteSpec.scala")
      assert(rep.testFailedEventsReceived(0).throwable.get.asInstanceOf[TestFailedException].failedCodeLineNumber.get === thisLineNumber - 8)
    }
    
    it("should generate TestRegistrationClosedException with correct stack depth info when has a test nested inside a test") {
      class TestSpec extends AnyFunSuite {
        var registrationClosedThrown = false
        test("a scenario") {
          test("nested scenario") {
            assert(1 == 2)
          }; /* ASSERTION_SUCCEED */
        }
        override def withFixture(test: NoArgTest): Outcome = {
          val outcome = test.apply()
          outcome match {
            case Exceptional(ex: TestRegistrationClosedException) => 
              registrationClosedThrown = true
            case _ =>
          }
          outcome
        }
      }
      val rep = new EventRecordingReporter
      val s = new TestSpec
      s.run(None, Args(rep))
      assert(s.registrationClosedThrown == true)
      val testFailedEvents = rep.testFailedEventsReceived
      assert(testFailedEvents.size === 1)
      assert(testFailedEvents(0).throwable.get.getClass() === classOf[TestRegistrationClosedException])
      val trce = testFailedEvents(0).throwable.get.asInstanceOf[TestRegistrationClosedException]
      assert("AnyFunSuiteSpec.scala" === trce.failedCodeFileName.get)
      assert(trce.failedCodeLineNumber.get === thisLineNumber - 23)
      assert(trce.message == Some("A test clause may not appear inside another test clause."))
    }

    it("should generate TestRegistrationClosedException with correct stack depth info when has an ignore nested inside a test") {
      class TestSpec extends AnyFunSuite {
        var registrationClosedThrown = false
        test("a scenario") {
          ignore("nested scenario") {
            assert(1 == 2)
          }; /* ASSERTION_SUCCEED */
        }
        override def withFixture(test: NoArgTest): Outcome = {
          val outcome = test.apply()
          outcome match {
            case Exceptional(ex: TestRegistrationClosedException) =>
              registrationClosedThrown = true
            case _ =>
          }
          outcome
        }
      }
      val rep = new EventRecordingReporter
      val s = new TestSpec
      s.run(None, Args(rep))
      assert(s.registrationClosedThrown == true)
      val testFailedEvents = rep.testFailedEventsReceived
      assert(testFailedEvents.size === 1)
      assert(testFailedEvents(0).throwable.get.getClass() === classOf[TestRegistrationClosedException])
      val trce = testFailedEvents(0).throwable.get.asInstanceOf[TestRegistrationClosedException]
      assert("AnyFunSuiteSpec.scala" === trce.failedCodeFileName.get)
      assert(trce.failedCodeLineNumber.get === thisLineNumber - 23)
      assert(trce.message == Some("An ignore clause may not appear inside a test clause."))
    }

    it("should generate a DuplicateTestNameException when duplicate test name is detected") {
      class TestSpec extends AnyFunSuite {
        test("test 1") {/* ASSERTION_SUCCEED */}
        test("test 1") {/* ASSERTION_SUCCEED */}
      }
      val e = intercept[DuplicateTestNameException] {
        new TestSpec
      }
      assert("AnyFunSuiteSpec.scala" == e.failedCodeFileName.get)
      assert(e.failedCodeLineNumber.get == thisLineNumber - 6)
      assert(!e.cause.isDefined)
    }

    it("should generate a DuplicateTestNameException when duplicate test name is detected using ignore") {
      class TestSpec extends AnyFunSuite {
        test("test 1") {/* ASSERTION_SUCCEED */}
        ignore("test 1") {/* ASSERTION_SUCCEED */}
      }
      val e = intercept[DuplicateTestNameException] {
        new TestSpec
      }
      assert("AnyFunSuiteSpec.scala" == e.failedCodeFileName.get)
      assert(e.failedCodeLineNumber.get == thisLineNumber - 6)
      assert(!e.cause.isDefined)
    }
  }
}


