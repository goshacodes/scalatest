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
package org.scalatest.verbs

import org.scalatest._
import SharedHelpers._
import events.TestSucceeded
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.wordspec.AnyWordSpec

class CanVerbSuite extends AnyFunSuite {
  test("can use can in WordSpec (which might be very convenient at times)") {
    class MySpec extends AnyWordSpec {
      "A thingy" can {
        "do this thing" in {}
        "do that thing" in {}
      }
    }
    val suite = new MySpec
    val rep = new EventRecordingReporter
    suite.run(None, Args(rep))
    val testSucceededEvents = rep.testSucceededEventsReceived
    assert(testSucceededEvents.size === 2)
    assert(rep.testSucceededEventsReceived.head.testName === "A thingy can do this thing")
    assert(rep.testSucceededEventsReceived.tail.head.testName === "A thingy can do that thing")
  }
  test("can use can in a FlatSpec that mixes in CanVerb") {
    class MySpec extends AnyFlatSpec with CanVerb {
      "A thingy" can "do this thing" in {}
      it can "do that thing" in {}
    }
    val suite = new MySpec
    val rep = new EventRecordingReporter
    suite.run(None, Args(rep))
    val testSucceededEvents = rep.testSucceededEventsReceived
    assert(testSucceededEvents.size === 2)
    assert(rep.testSucceededEventsReceived.head.testName === "A thingy can do this thing")
    assert(rep.testSucceededEventsReceived.tail.head.testName === "A thingy can do that thing")
  }
  test("can use 'can behave like' in a FlatSpec that mixes in CanVerb") {
    class MySpec extends AnyFlatSpec with CanVerb {
      "A thingy" can "do this thing" in {}
      it can "do that thing" in {}
    }
    val suite = new MySpec
    val rep = new EventRecordingReporter
    suite.run(None, Args(rep))
    val testSucceededEvents = rep.testSucceededEventsReceived
    assert(testSucceededEvents.size === 2)
    assert(rep.testSucceededEventsReceived.head.testName === "A thingy can do this thing")
    assert(rep.testSucceededEventsReceived.tail.head.testName === "A thingy can do that thing")
  }
}
