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

import org.scalactic.exceptions.NullArgumentException
import StackDepthExceptionHelper.posOrElseStackDepthFun
import org.scalactic.Requirements._
import org.scalactic.source

/**
 * Exception that indicates a test failed.
 *
 * <p>
 * One purpose of this exception is to encapsulate information about
 * the stack depth at which the line of test code that failed resides, so that information can be presented to
 * the user that makes it quick to find the failing line of test code. (In other words, the user need not scan through the
 * stack trace to find the correct filename and line number of the failing test.)
 * </p>
 *
 * <p>
 * Another purpose of this exception is to encapsulate a payload, an object to be included in a <a href="../events/TestFailed.html"><code>TestFailed</code></a> event
 * as its payload, so it can be consumed by a custom reporter that understands the payload. For example, tests could take a screen
 * shot image of a GUI when a test fails, and include that as a payload. A custom reporter could listen for such payloads and
 * display the screen shots to the user.
 * </p>
 *
 * @param messageFun a function that produces an optional detail message for this <code>TestFailedException</code>.
 * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestFailedException</code> to be thrown.
 * @param posOrStackDepthFun a source position or a function that produces the depth in the stack trace of this exception at which the line of test code that failed resides.
 * @param payload an optional payload, which ScalaTest will include in a resulting <code>TestFailed</code> event
 *
 * @throws NullArgumentException if either <code>messageFun</code>, <code>cause</code> or <code>failedCodeStackDepthFun</code> is <code>null</code>, or <code>Some(null)</code>.
 *
 * @author Bill Venners
 */
class TestFailedException(
  messageFun: StackDepthException => Option[String],
  cause: Option[Throwable],
  posOrStackDepthFun: Either[source.Position, StackDepthException => Int],
  val payload: Option[Any],
  val analysis: scala.collection.immutable.IndexedSeq[String]
) extends StackDepthException(messageFun, cause, posOrStackDepthFun) with ModifiableMessage[TestFailedException] with PayloadField with ModifiablePayload[TestFailedException] {

  /**
    * Constructs a <code>TestFailedException</code> with the given error message function, optional cause, source position and optional payload.
    *
    * @param messageFun a function that return an optional detail message for this <code>TestFailedException</code>.
    * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestFailedException</code> to be thrown.
    * @param pos a source position
    * @param payload an optional payload, which ScalaTest will include in a resulting <code>TestFailedException</code> event
    */
  def this(
    messageFun: StackDepthException => Option[String],
    cause: Option[Throwable],
    pos: source.Position,
    payload: Option[Any]
  ) = this(messageFun, cause, Left(pos), payload, Vector.empty)

  /**
    * Constructs a <code>TestFailedException</code> with the given error message function, optional cause and source position.
    *
    * @param messageFun a function that return an optional detail message for this <code>TestFailedException</code>.
    * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestFailedException</code> to be thrown.
    * @param pos a source position
    */
  def this(
    messageFun: StackDepthException => Option[String],
    cause: Option[Throwable],
    pos: source.Position
  ) = this(messageFun, cause, Left(pos), None, Vector.empty)

  /**
   * Constructs a <code>TestFailedException</code> with pre-determined <code>message</code> and <code>failedCodeStackDepth</code>. (This was
   * the primary constructor form from ScalaTest 1.5 to 1.8.)
   *
   * @param messageFun a function that produces an optional detail message for this <code>TestFailedException</code>.
   * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestFailedException</code> to be thrown.
   * @param failedCodeStackDepthFun a function that produces the depth in the stack trace of this exception at which the line of test code that failed resides.
   *
   * @throws NullArgumentException if either <code>message</code> of <code>cause</code> is <code>null</code>, or <code>Some(null)</code>.
   */
  def this(messageFun: StackDepthException => Option[String], cause: Option[Throwable], failedCodeStackDepthFun: StackDepthException => Int) =
    this(messageFun, cause, Right(failedCodeStackDepthFun), None, Vector.empty)

  /**
   * Constructs a <code>TestFailedException</code> with pre-determined <code>message</code> and <code>failedCodeStackDepth</code>. (This was
   * the primary constructor form prior to ScalaTest 1.5.)
   *
   * @param message an optional detail message for this <code>TestFailedException</code>.
   * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestFailedException</code> to be thrown.
   * @param failedCodeStackDepth the depth in the stack trace of this exception at which the line of test code that failed resides.
   *
   * @throws NullArgumentException if either <code>message</code> of <code>cause</code> is <code>null</code>, or <code>Some(null)</code>.
   */
  def this(message: Option[String], cause: Option[Throwable], failedCodeStackDepth: Int) =
    this(
      StackDepthException.toExceptionFunction(message),
      cause,
      Right((_: StackDepthException) => failedCodeStackDepth),
      None,
      Vector.empty
    )

  /**
   * Create a <code>TestFailedException</code> with specified stack depth and no detail message or cause.
   *
   * @param failedCodeStackDepth the depth in the stack trace of this exception at which the line of test code that failed resides.
   *
   */
  def this(failedCodeStackDepth: Int) =
    this(
      StackDepthException.toExceptionFunction(None),
      None,
      Right((_: StackDepthException) => failedCodeStackDepth),
      None,
      Vector.empty
    )

  /**
   * Create a <code>TestFailedException</code> with a specified stack depth and detail message.
   *
   * @param message A detail message for this <code>TestFailedException</code>.
   * @param failedCodeStackDepth the depth in the stack trace of this exception at which the line of test code that failed resides.
   *
   * @throws NullArgumentException if <code>message</code> is <code>null</code>.
   */
  def this(message: String, failedCodeStackDepth: Int) =
    this(
      {
        requireNonNull(message)
        StackDepthException.toExceptionFunction(Some(message))
      },
      None,
      Right((_: StackDepthException) => failedCodeStackDepth),
      None,
      Vector.empty
    )

  /**
   * Create a <code>TestFailedException</code> with the specified stack depth and cause.  The
   * <code>message</code> field of this exception object will be initialized to
   * <code>if (cause.getMessage == null) "" else cause.getMessage</code>.
   *
   * @param cause the cause, the <code>Throwable</code> that caused this <code>TestFailedException</code> to be thrown.
   * @param failedCodeStackDepth the depth in the stack trace of this exception at which the line of test code that failed resides.
   *
   * @throws NullArgumentException if <code>cause</code> is <code>null</code>.
   */
  def this(cause: Throwable, failedCodeStackDepth: Int) =
    this(
      {
        requireNonNull(cause)
        StackDepthException.toExceptionFunction(if (cause.getMessage == null) None else Some(cause.getMessage))
      },
      Some(cause),
      Right((_: StackDepthException) => failedCodeStackDepth),
      None,
      Vector.empty
    )

  /**
   * Create a <code>TestFailedException</code> with the specified stack depth, detail
   * message, and cause.
   *
   * <p>Note that the detail message associated with cause is
   * <em>not</em> automatically incorporated in this throwable's detail
   * message.
   *
   * @param message A detail message for this <code>TestFailedException</code>.
   * @param cause the cause, the <code>Throwable</code> that caused this <code>TestFailedException</code> to be thrown.
   * @param failedCodeStackDepth the depth in the stack trace of this exception at which the line of test code that failed resides.
   *
   * @throws NullArgumentException if either <code>message</code> or <code>cause</code> is <code>null</code>.
   */
  def this(message: String, cause: Throwable, failedCodeStackDepth: Int) =
    this(
      {
        requireNonNull(message)
        StackDepthException.toExceptionFunction(Some(message))
      },
      {
        requireNonNull(cause)
        Some(cause)
      },
      Right((_: StackDepthException) => failedCodeStackDepth),
      None,
      Vector.empty
    )

  /**
    * Constructs a <code>TestFailedException</code> with the given error message function, optional cause, stack depth function and optional payload.
    *
    * @param messageFun a function that return an optional detail message for this <code>TestFailedException</code>.
    * @param cause an optional cause, the <code>Throwable</code> that caused this <code>TestFailedException</code> to be thrown.
    * @param failedCodeStackDepthFun a function that produces the depth in the stack trace of this exception at which the line of test code that failed resides.
    * @param payload an optional payload, which ScalaTest will include in a resulting <code>TestFailedException</code> event
    */
  def this(
    messageFun: StackDepthException => Option[String],
    cause: Option[Throwable],
    failedCodeStackDepthFun: StackDepthException => Int,
    payload: Option[Any]) = this(messageFun, cause, Right(failedCodeStackDepthFun), payload, Vector.empty)

  /**
   * Returns an exception of class <code>TestFailedException</code> with <code>failedExceptionStackDepth</code> set to 0 and 
   * all frames above this stack depth severed off. This can be useful when working with tools (such as IDEs) that do not
   * directly support ScalaTest. (Tools that directly support ScalaTest can use the stack depth information delivered
   * in the StackDepth exceptions.)
   */
  def severedAtStackDepth: TestFailedException = {
    val truncated = getStackTrace.drop(failedCodeStackDepth)
    val e = new TestFailedException(messageFun, cause, posOrStackDepthFun, payload, analysis)
    e.setStackTrace(truncated)
    e
  }

  /**
   * Returns an instance of this exception's class, identical to this exception,
   * except with the detail message option string replaced with the result of passing
   * the current detail message to the passed function, <code>fun</code>.
   *
   * @param fun A function that, given the current optional detail message, will produce
   * the modified optional detail message for the result instance of <code>TestFailedException</code>.
   */
  def modifyMessage(fun: Option[String] => Option[String]): TestFailedException = {
    val mod = new TestFailedException(StackDepthException.toExceptionFunction(fun(message)), cause, posOrStackDepthFun, payload, analysis) // TODO: Seems like here I could just compose the message functions and not evaluate them, in case it is never used
    mod.setStackTrace(getStackTrace)
    mod
  }
  
  /**
   * Returns an instance of this exception's class, identical to this exception,
   * except with the payload option replaced with the result of passing
   * the current payload option to the passed function, <code>fun</code>.
   *
   * @param fun A function that, given the current optional payload, will produce
   * the modified optional payload for the result instance of <code>TestFailedException</code>.
   */
  def modifyPayload(fun: Option[Any] => Option[Any]): TestFailedException = {
    val currentPayload = payload
    val mod = new TestFailedException(messageFun, cause, posOrStackDepthFun, fun(currentPayload), analysis) // TODO: Should I be lazy about replacing the payload?
    mod.setStackTrace(getStackTrace)
    mod
  }

  /**
   * Indicates whether this object can be equal to the passed object.
   */
  override def canEqual(other: Any): Boolean = other.isInstanceOf[TestFailedException]

  /**
   * Indicates whether this object is equal to the passed object. If the passed object is
   * a <code>TestFailedException</code>, equality requires equal <code>message</code>,
   * <code>cause</code>, and <code>failedCodeStackDepth</code> fields, as well as equal
   * return values of <code>getStackTrace</code>.
   */
  override def equals(other: Any): Boolean =
    other match {
      case that: TestFailedException => super.equals(that) && payload == that.payload && analysis == that.analysis
      case _ => false
    }

  /**
   * Returns a hash code value for this object.
   */
  override def hashCode: Int =
    41 * (
      super.hashCode
    ) + payload.hashCode + analysis.hashCode
}

