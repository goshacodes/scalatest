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

import SharedHelpers._
import org.scalatest.funsuite.AnyFunSuite

class BigSuiteSuite extends AnyFunSuite {
  test("a BigSuite(Some(0)) has 100 tests") {
    val bs = new BigSuite(Some(0), Map.empty)
    assert(bs.expectedTestCount(Filter()) === 100)
  }
  test("a BigSuite(Some(0)) has no nested suites") {
    val bs = new BigSuite(Some(0), Map.empty)
    assert(bs.nestedSuites.size === 0)
  }
  test("a BigSuite(Some(1)) has 1 nested suite") {
    val bs = new BigSuite(Some(1), Map.empty)
    assert(bs.nestedSuites.size === 1)
  }
  test("a BigSuite(Some(1))'s 1 nested suite has no nested suites") {
    val bs = new BigSuite(Some(1), Map.empty)
    assert(bs.nestedSuites.head.nestedSuites.size === 0)
  }
  test("a BigSuite(Some(2)) has 2 nested suites") {
    val bs = new BigSuite(Some(2), Map.empty)
    assert(bs.nestedSuites.size === 2)
  }
  test("a BigSuite(Some(1))'s 2 nested suites have one nested suite each, etc.") {
    val bs = new BigSuite(Some(2), Map.empty)
    for (suite <- bs.nestedSuites) {
      assert(suite.nestedSuites.size === 1)
      for (s2 <- suite.nestedSuites)
        assert(s2.nestedSuites.size === 0)
    }
  }
  test("a BigSuite(Some(3)) has 3 nested suites") {
    val bs = new BigSuite(Some(3), Map.empty)
    assert(bs.nestedSuites.size === 3)
  }
  test("a BigSuite(Some(2))'s 3 nested suites have two nested suites each, etc.") {
    val bs = new BigSuite(Some(3), Map.empty)
    for (suite <- bs.nestedSuites) {
      assert(suite.nestedSuites.size === 2)
      for (s2 <- suite.nestedSuites) {
        assert(s2.nestedSuites.size === 1)
        for (s3 <- s2.nestedSuites)
          assert(s3.nestedSuites.size === 0)
      }
    }
  }
  def ensureTestFailedEventReceivedOrNot(suite: Suite, shouldReceiveCount: Int): Unit = {
    val reporter = new EventRecordingReporter
    suite.run(None, Args(reporter))
    val testFailedEvents = reporter.testFailedEventsReceived
    assert(testFailedEvents.size === shouldReceiveCount)
    if (shouldReceiveCount > 0)
      assert(testFailedEvents(0).testName === "test number 1")
  }
  test("A BigSuite(Some(0)) has one test failure if somefailures property defined") {
    ensureTestFailedEventReceivedOrNot(new BigSuite(Some(0), Map("org.scalatest.BigSuite.someFailures" -> "true")), 1)
  }
  test("A BigSuite(Some(n > 0)) has no test failures if somefailures property defined") {
    val map = Map("org.scalatest.BigSuite.someFailures" -> "true")
    ensureTestFailedEventReceivedOrNot(new BigSuite(Some(1), map), 1)
    ensureTestFailedEventReceivedOrNot(new BigSuite(Some(2), map), 2)
    ensureTestFailedEventReceivedOrNot(new BigSuite(Some(3), map), 6)
  }
  test("A BigSuite() has no test failures if somefailures property is not defined") {
    val map = Map("org.scalatest.BigSuite.someFailures" -> "")
    ensureTestFailedEventReceivedOrNot(new BigSuite(Some(0), map), 0)
    ensureTestFailedEventReceivedOrNot(new BigSuite(Some(1), map), 0)
    ensureTestFailedEventReceivedOrNot(new BigSuite(Some(2), map), 0)
    ensureTestFailedEventReceivedOrNot(new BigSuite(Some(3), map), 0)
  }
  test("A BigSuite(None) has no nested suites if the config map is empty") {
    val bs = new BigSuite(Some(0), Map.empty)
    assert(bs.nestedSuites.size === 0)
  }
  test("A BigSuite() has no nested suites if a system property is empty") {
    val bs = new BigSuite(None, Map.empty)
    assert(bs.nestedSuites.size === 0)
  }
  test("A BigSuite(None) has 1 nested suite if a system property says so") {
    val bs = new BigSuite(None, Map("org.scalatest.BigSuite.size" -> "1"))
    assert(bs.nestedSuites.size === 1)
  }
  test("A BigSuite() has 1 nested suite if a system property says so") {
    val bs = new BigSuite(None, Map("org.scalatest.BigSuite.size" -> "1"))
    assert(bs.nestedSuites.size === 1)
  }
  test("A BigSuite(None) has no nested suites if a system property is not parsable as an Int") {
    val bs = new BigSuite(None, Map("org.scalatest.BigSuite.size" -> "bob"))
    assert(bs.nestedSuites.size === 0)
  }
  test("A BigSuite() has no nested suites if a system property is not parsable as an Int") {
    val bs = new BigSuite(None, Map("org.scalatest.BigSuite.size" -> "bob"))
    assert(bs.nestedSuites.size === 0)
  }
  test("A BigSuite(None) has 2 nested suites if a system property says 2") {
    val bs = new BigSuite(None, Map("org.scalatest.BigSuite.size" -> "2"))
    assert(bs.nestedSuites.size === 2)
  }
  test("A BigSuite() has 2 nested suites if a system property says 2") {
    val bs = new BigSuite(None, Map("org.scalatest.BigSuite.size" -> "2"))
    assert(bs.nestedSuites.size === 2)
  }
  test("A BigSuite(None) has 4 nested suites if a system property says 4") {
    val bs = new BigSuite(None, Map("org.scalatest.BigSuite.size" -> "4"))
    assert(bs.nestedSuites.size === 4)
  }
  test("A BigSuite() has 4 nested suites if a system property says 4") {
    val bs = new BigSuite(None, Map("org.scalatest.BigSuite.size" -> "4"))
    assert(bs.nestedSuites.size === 4)
  }
}
