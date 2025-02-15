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

import java.io.PrintStream
import java.io.ByteArrayOutputStream
import org.scalatest.events._
import org.scalactic.exceptions.NullArgumentException
import SharedHelpers._
import concurrent.Eventually._
import OptionValues._
import Inside._
import tagobjects.Retryable
import Retries._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatest.tagobjects.Flicker

class DispatchReporterSpec extends AnyFunSpec {

  override def withFixture(test: NoArgTest) = {
    if (isRetryable(test))
      withRetry { super.withFixture(test) }
    else
      super.withFixture(test)
  }

  val TestStartingOrdinal = new Ordinal(223)
  val SecondTestStartingOrdinal = TestStartingOrdinal.next
  val TestFinishedOrdinal = SecondTestStartingOrdinal.next
  val SecondTestFinishedOrdinal = TestFinishedOrdinal.next 
  describe("the DispatchReporter") {
    describe("when slowpoke detection is enabled") {
      def fireTestStarting(): (EventRecordingReporter, DispatchReporter) = {
        val erp = new EventRecordingReporter
        val dispatch = new DispatchReporter(List(erp, SilentReporter), Console.err, true, 1, 1)
        dispatch(
          TestStarting(
            ordinal = TestStartingOrdinal,
            suiteName = "the suite name",
            suiteId = "the suite ID",
            suiteClassName = Some("suiteClassName"),
            testName = "the test name",
            testText = "test name"
          )
        )
        (erp, dispatch)
      }
      it("should throw NullArgumentException if constructed with a null reporters arg") {
        intercept[NullArgumentException] {
          val dispatch = new DispatchReporter(null, Console.err, true, 1, 1)
        }
      }
      it("should send out AlertProvided events with useful message if a slowpoke is detected") {
        val (erp, dispatch) = fireTestStarting()
        val alertProvidedEvent =
          eventually {
            val ips = erp.alertProvidedEventsReceived
            ips.size should be > 0
            ips(0)
          }
        dispatch.doDispose()
        val msg = alertProvidedEvent.message
        msg should (include ("the suite name") and include ("the test name"))
        inside (alertProvidedEvent.formatter.value) { case IndentedText(formattedText, rawText, indentationLevel) =>
          formattedText should equal (Resources.alertFormattedText(msg))
          rawText should equal (msg)
          indentationLevel should equal (0)
        }
      }
      it("should send out AlertProvided events with a message that mentions all detected slowpokes") {
        val (erp, dispatch) = fireTestStarting()
        dispatch(
          TestStarting(
            ordinal = TestStartingOrdinal,
            suiteName = "the other suite name",
            suiteId = "the other suite ID",
            suiteClassName = Some("otherSuiteClassName"),
            testName = "the other test name",
            testText = "other test name"
          )
        )
        try eventually {
          val ips = erp.alertProvidedEventsReceived
          val size = ips.size
          size should be > 0
          ips(size - 1).message should (
            include ("the suite name") and include ("the test name") and
            include ("the other suite name") and include ("the other test name")
          )
        }
        finally dispatch.doDispose()
      }
      def doTestStartingAndFinishedEvents(testFinishedEvent: Event): Unit = {
        val (erp, dispatch) = fireTestStarting()
        eventually {
          erp.alertProvidedEventsReceived.size should be > 0
        }
        dispatch(testFinishedEvent)
        var sizeWasSameCount = 0
        var previousSize = erp.alertProvidedEventsReceived.size
        eventually {
          val size = erp.alertProvidedEventsReceived.size 
          if (size == previousSize)
            sizeWasSameCount += 1
          else
            sizeWasSameCount = 0 
          previousSize = size
          sizeWasSameCount should be >= 5
        }
        dispatch.doDispose()
      }
      it("should stop sending out AlertProvided events after a detected slowpoke succeeds", Flicker) {
        doTestStartingAndFinishedEvents(
          TestSucceeded(
            ordinal = TestFinishedOrdinal,
            suiteName = "the suite name",
            suiteId = "the suite ID",
            suiteClassName = Some("suiteClassName"),
            testName = "the test name",
            testText = "test name",
            recordedEvents = collection.immutable.IndexedSeq.empty
          )
        )
      }
      it("should stop sending out AlertProvided events after a detected slowpoke fails", Flicker) {
        doTestStartingAndFinishedEvents(
          TestFailed(
            ordinal = TestFinishedOrdinal,
            message = "I meant to do that!",
            suiteName = "the suite name",
            suiteId = "the suite ID",
            suiteClassName = Some("suiteClassName"),
            testName = "the test name",
            testText = "test name",
            recordedEvents = collection.immutable.IndexedSeq.empty,
            analysis = collection.immutable.IndexedSeq.empty
          )
        )
      }
      it("should stop sending out AlertProvided events after a detected slowpoke is canceled", Flicker) {
        doTestStartingAndFinishedEvents(
          TestCanceled(
            ordinal = TestFinishedOrdinal,
            message = "I meant to do that!",
            suiteName = "the suite name",
            suiteId = "the suite ID",
            suiteClassName = Some("suiteClassName"),
            testName = "the test name",
            testText = "test name",
            recordedEvents = collection.immutable.IndexedSeq.empty
          )
        )
      }
      it("should stop sending out AlertProvided events after a detected slowpoke is reported as pending", Flicker) {
        doTestStartingAndFinishedEvents(
          TestPending(
            ordinal = TestFinishedOrdinal,
            suiteName = "the suite name",
            suiteId = "the suite ID",
            suiteClassName = Some("suiteClassName"),
            testName = "the test name",
            testText = "test name",
            recordedEvents = collection.immutable.IndexedSeq.empty
          )
        )
      }
/* There is no TestOmitted event as yet!
      it("should stop sending out AlertProvided events after a detected slowpoke is reported as omitted") {
        doTestStartingAndFinishedEvents(
          TestOmitted(
            ordinal = TestFinishedOrdinal,
            suiteName = "the suite name",
            suiteId = "the suite ID",
            suiteClassName = Some("suiteClassName"),
            testName = "the test name",
            testText = "test name",
            recordedEvents = collection.immutable.IndexedSeq.empty
          )
        )
      }
*/
      it("should send AlertProvided events if a slowpoke is detected with the only seen ordinal") {
        val (erp, dispatch) = fireTestStarting()
        val initialAlertProvided =
          eventually {
            val ips = erp.alertProvidedEventsReceived
            ips.size should be > 0
            ips(0)
          }
        dispatch.doDispose()
        initialAlertProvided.ordinal should be (TestStartingOrdinal)
      }
      it("should send AlertProvided events if a slowpoke is detected with largest seen ordinal", Retryable) {
        val (erp, dispatch) = fireTestStarting()
        dispatch(
          TestStarting(
            ordinal = SecondTestStartingOrdinal,
            suiteName = "the second suite name",
            suiteId = "the second suite ID",
            suiteClassName = Some("suiteClassName"),
            testName = "the second test name",
            testText = "second test name"
          )
        )
        try eventually {
          val ips = erp.alertProvidedEventsReceived
          val sz = ips.size
          sz should be > 0
          ips(sz - 1).ordinal should be (SecondTestStartingOrdinal)
        }
        finally dispatch.doDispose()
      }
    }
  }
}

