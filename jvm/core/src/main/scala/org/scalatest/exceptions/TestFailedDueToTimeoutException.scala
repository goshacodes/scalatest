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
package org.scalatest.exceptions

import org.scalatest.time.Span
import org.scalactic.source
import StackDepthExceptionHelper.posOrElseStackDepthFun

/**
 * Subclass of <a href="TestFailedException.html"><code>TestFailedException</code></a> representing tests that failed because of a timeout.
 *
 * <p>
 * This exception is thrown by the <code>failAfter</code>
 * method of trait <a href="../concurrent/Timeouts.html"><code>Timeouts</code></a>, the <code>eventually</code> method of trait
 * <a href="../concurrent/Eventually.html"><code>Eventually</code></a>, and the <code>await</code> methods of trait
 * <a href="../concurrent/AsyncAssertions.html"><code>AsyncAssertions</code></a>.
 * </p>
 *
 * @param messageFun a function that produces an optional detail message for this <code>TestFailedDueToTimeoutException</code>.
 * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestFailedDueToTimeoutException</code> to be thrown.
 * @param posOrStackDepthFun either a source position or a function that produces the depth in the stack trace of this exception at which the line of test code that failed resides.
 * @param timeout the timeout that expired
 *
 * @throws NullArgumentException if either <code>messageFun</code>, <code>cause</code> or <code>failedCodeStackDepthFun</code> is <code>null</code>, or <code>Some(null)</code>.
 *
 * @author Bill Venners
 */
class TestFailedDueToTimeoutException(
  messageFun: StackDepthException => Option[String],
  cause: Option[Throwable],
  posOrStackDepthFun: Either[source.Position, StackDepthException => Int],
  payload: Option[Any],
  val timeout: Span
) extends TestFailedException(messageFun, cause, posOrStackDepthFun, payload, Vector.empty) with TimeoutField {

  /**
    * Constructs a <code>TestFailedDueToTimeoutException</code> with the given error message function, optional cause, source position and optional payload.
    *
    * @param messageFun a function that return an optional detail message for this <code>TestCanceledException</code>.
    * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestCanceledException</code> to be thrown.
    * @param pos a source position
    * @param payload an optional payload, which ScalaTest will include in a resulting <code>TestCanceled</code> event
    */
  def this(
    messageFun: StackDepthException => Option[String],
    cause: Option[Throwable],
    pos: source.Position,
    payload: Option[Any],
    timeout: Span
  ) = this(messageFun, cause, Left(pos), payload, timeout)

  /**
    * Constructs a <code>TestFailedDueToTimeoutException</code> with the given error message function, optional cause, stack depth function, optional payload and timeout.
    *
    * @param messageFun a function that return an optional detail message for this <code>TestFailedDueToTimeoutException</code>.
    * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestFailedDueToTimeoutException</code> to be thrown.
    * @param failedCodeStackDepthFun a function that return the depth in the stack trace of this exception at which the line of test code that failed resides.
    * @param payload an optional payload, which ScalaTest will include in a resulting <code>TestCanceled</code> event
    * @param timeout the timeout that expired
    */
  def this(
    messageFun: StackDepthException => Option[String],
    cause: Option[Throwable],
    failedCodeStackDepthFun: StackDepthException => Int,
    payload: Option[Any],
    timeout: Span
  ) = this(messageFun, cause, Right(failedCodeStackDepthFun), payload, timeout)

  /**
   * Returns an instance of this exception's class, identical to this exception,
   * except with the detail message option string replaced with the result of passing
   * the current detail message to the passed function, <code>fun</code>.
   *
   * @param fun A function that, given the current optional detail message, will produce
   * the modified optional detail message for the result instance of <code>TestFailedDueToTimeoutException</code>.
   */
  override def modifyMessage(fun: Option[String] => Option[String]): TestFailedDueToTimeoutException = {
    val mod = new TestFailedDueToTimeoutException((_: StackDepthException) => fun(message), cause, posOrStackDepthFun, payload, timeout)
    mod.setStackTrace(getStackTrace)
    mod
  }

  /**
   * Returns an instance of this exception's class, identical to this exception,
   * except with the payload option replaced with the result of passing
   * the current payload option to the passed function, <code>fun</code>.
   *
   * @param fun A function that, given the current optional payload, will produce
   * the modified optional payload for the result instance of <code>TestFailedDueToTimeoutException</code>.
   */
  override def modifyPayload(fun: Option[Any] => Option[Any]): TestFailedDueToTimeoutException = {
    val currentPayload = payload
    val mod = new TestFailedDueToTimeoutException(messageFun, cause, posOrStackDepthFun, fun(currentPayload), timeout)
    mod.setStackTrace(getStackTrace)
    mod
  }
}

/*
Will need to add cancelAfter to the doc comment in 2.0.
*/

