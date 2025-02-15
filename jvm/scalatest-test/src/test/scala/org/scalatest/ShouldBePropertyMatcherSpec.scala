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

import org.scalatest.exceptions.TestFailedException

import matchers.BePropertyMatcher
import matchers.BePropertyMatchResult
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

class ShouldBePropertyMatcherSpec extends AnyFunSpec with ReturnsNormallyThrowsAssertion with BookPropertyMatchers {

  // Checking for a specific size
  describe("The be (BePropertyMatcher) syntax") {

    case class MyFile(
      val name: String,
      val file: Boolean,
      val isDirectory: Boolean
    )

    class FileBePropertyMatcher extends BePropertyMatcher[MyFile] {
      def apply(file: MyFile) = {
        new BePropertyMatchResult(file.file, "file")
      }
    }

    class DirectoryBePropertyMatcher extends BePropertyMatcher[MyFile] {
      def apply(file: MyFile) = {
        new BePropertyMatchResult(file.isDirectory, "directory")
      }
    }

    def file = new FileBePropertyMatcher
    def directory = new DirectoryBePropertyMatcher

    val myFile = new MyFile("temp.txt", true, false)
    val myFilePrettified = "MyFile(\"temp.txt\", true, false)"

    val book = new Book("A Tale of Two Cities", "Dickens", 1859, 45, true)
    val bookPrettified = "Book(\"A Tale of Two Cities\", \"Dickens\", 1859, 45, true)"
    val badBook = new Book("A Tale of Two Cities", "Dickens", 1859, 45, false)
    val badBookPrettified = "Book(\"A Tale of Two Cities\", \"Dickens\", 1859, 45, false)"

    it("should do nothing if the property is true") {
      book should be (goodRead)
      book should be a (goodRead)
      book should be an (goodRead)
      
      // book shouldBe goodRead
      // book shouldBe a (goodRead)
      // book shouldBe an (goodRead)
    }

    it("should throw TestFailedException if the property is false") {

      val caught1 = intercept[TestFailedException] {
        badBook should be (goodRead)
      }
      assert(caught1.getMessage === s"$badBookPrettified was not goodRead")

      val caught2 = intercept[TestFailedException] {
        badBook should be a (goodRead)
      }
      assert(caught2.getMessage === s"$badBookPrettified was not a goodRead")

      val caught3 = intercept[TestFailedException] {
        badBook should be an (goodRead)
      }
      assert(caught3.getMessage === s"$badBookPrettified was not an goodRead")
      
/*
      val caught4 = intercept[TestFailedException] {
        badBook shouldBe goodRead
      }
      assert(caught4.getMessage === "Book(A Tale of Two Cities,Dickens,1859,45,false) was not goodRead")

      val caught5 = intercept[TestFailedException] {
        badBook shouldBe a (goodRead)
      }
      assert(caught5.getMessage === "Book(A Tale of Two Cities,Dickens,1859,45,false) was not a goodRead")

      val caught6 = intercept[TestFailedException] {
        badBook shouldBe an (goodRead)
      }
      assert(caught6.getMessage === "Book(A Tale of Two Cities,Dickens,1859,45,false) was not an goodRead")
 */
    }

    it("should do nothing if the property is false, when used with not") {
      badBook should not be (goodRead)
      badBook should not be a (goodRead)
      badBook should not be an (goodRead)
    }

    it("should throw TestFailedException if the property is true, when used with not") {

      val caught1 = intercept[TestFailedException] {
        book should not be (goodRead)
      }
      assert(caught1.getMessage === s"$bookPrettified was goodRead")

      val caught2 = intercept[TestFailedException] {
        book should not be a (goodRead)
      }
      assert(caught2.getMessage === s"$bookPrettified was a goodRead")

      val caught3 = intercept[TestFailedException] {
        book should not be an (goodRead)
      }
      assert(caught3.getMessage === s"$bookPrettified was an goodRead")

      val caught4 = intercept[TestFailedException] {
        book should not (be (goodRead))
      }
      assert(caught4.getMessage === s"$bookPrettified was goodRead")

      val caught5 = intercept[TestFailedException] {
        book should not (be a (goodRead))
      }
      assert(caught5.getMessage === s"$bookPrettified was a goodRead")

      val caught6 = intercept[TestFailedException] {
        book should not (be an (goodRead))
      }
      assert(caught6.getMessage === s"$bookPrettified was an goodRead")

      val caught7 = intercept[TestFailedException] {
        book should (not (be (goodRead)))
      }
      assert(caught7.getMessage === s"$bookPrettified was goodRead")

      val caught8 = intercept[TestFailedException] {
        book should (not (be a (goodRead)))
      }
      assert(caught8.getMessage === s"$bookPrettified was a goodRead")

      val caught9 = intercept[TestFailedException] {
        book should (not (be an (goodRead)))
      }
      assert(caught9.getMessage === s"$bookPrettified was an goodRead")
    }

    it("should do nothing if the the property returns true, when used in a logical-and expression") {

      myFile should ((be (file)) and (be (file)))
      myFile should (be (file) and (be (file)))
      myFile should (be (file) and be (file))

      myFile should ((be a (file)) and (be a (file)))
      myFile should (be a (file) and (be a (file)))
      myFile should (be a (file) and be a (file))

      myFile should ((be an (file)) and (be an (file)))
      myFile should (be an (file) and (be an (file)))
      myFile should (be an (file) and be an (file))
    }

    it("should throw TestFailedException if at least one of the properties returns false, when used in a logical-and expression") {

      // first false
      val caught1 = intercept[TestFailedException] {
        myFile should ((be (directory)) and (be (file)))
      }
      assert(caught1.getMessage === s"$myFilePrettified was not directory")

      val caught2 = intercept[TestFailedException] {
        myFile should (be (directory) and (be (file)))
      }
      assert(caught2.getMessage === s"$myFilePrettified was not directory")

      val caught3 = intercept[TestFailedException] {
        myFile should (be (directory) and be (file))
      }
      assert(caught3.getMessage === s"$myFilePrettified was not directory")


      val caught4 = intercept[TestFailedException] {
        myFile should ((be a (directory)) and (be a (file)))
      }
      assert(caught4.getMessage === s"$myFilePrettified was not a directory")

      val caught5 = intercept[TestFailedException] {
        myFile should (be a (directory) and (be a (file)))
      }
      assert(caught5.getMessage === s"$myFilePrettified was not a directory")

      val caught6 = intercept[TestFailedException] {
        myFile should (be a (directory) and be a (file))
      }
      assert(caught6.getMessage === s"$myFilePrettified was not a directory")


      val caught7 = intercept[TestFailedException] {
        myFile should ((be an (directory)) and (be an (file)))
      }
      assert(caught7.getMessage === s"$myFilePrettified was not an directory")

      val caught8 = intercept[TestFailedException] {
        myFile should (be an (directory) and (be an (file)))
      }
      assert(caught8.getMessage === s"$myFilePrettified was not an directory")

      val caught9 = intercept[TestFailedException] {
        myFile should (be an (directory) and be an (file))
      }
      assert(caught9.getMessage === s"$myFilePrettified was not an directory")


      // second false
      val caught10 = intercept[TestFailedException] {
        myFile should ((be (file)) and (be (directory)))
      }
      assert(caught10.getMessage === s"$myFilePrettified was file, but $myFilePrettified was not directory")

      val caught11 = intercept[TestFailedException] {
        myFile should (be (file) and (be (directory)))
      }
      assert(caught11.getMessage === s"$myFilePrettified was file, but $myFilePrettified was not directory")

      val caught12 = intercept[TestFailedException] {
        myFile should (be (file) and be (directory))
      }
      assert(caught12.getMessage === s"$myFilePrettified was file, but $myFilePrettified was not directory")


      val caught13 = intercept[TestFailedException] {
        myFile should ((be a (file)) and (be a (directory)))
      }
      assert(caught13.getMessage === s"$myFilePrettified was a file, but $myFilePrettified was not a directory")

      val caught14 = intercept[TestFailedException] {
        myFile should (be a (file) and (be a (directory)))
      }
      assert(caught14.getMessage === s"$myFilePrettified was a file, but $myFilePrettified was not a directory")

      val caught15 = intercept[TestFailedException] {
        myFile should (be a (file) and be a (directory))
      }
      assert(caught15.getMessage === s"$myFilePrettified was a file, but $myFilePrettified was not a directory")


      val caught16 = intercept[TestFailedException] {
        myFile should ((be an (file)) and (be an (directory)))
      }
      assert(caught16.getMessage === s"$myFilePrettified was an file, but $myFilePrettified was not an directory")

      val caught17 = intercept[TestFailedException] {
        myFile should (be an (file) and (be an (directory)))
      }
      assert(caught17.getMessage === s"$myFilePrettified was an file, but $myFilePrettified was not an directory")

      val caught18 = intercept[TestFailedException] {
        myFile should (be an (file) and be an (directory))
      }
      assert(caught18.getMessage === s"$myFilePrettified was an file, but $myFilePrettified was not an directory")


      // both false
      val caught19 = intercept[TestFailedException] {
        myFile should ((be (directory)) and (be (directory)))
      }
      assert(caught19.getMessage === s"$myFilePrettified was not directory")

      val caught20 = intercept[TestFailedException] {
        myFile should (be (directory) and (be (directory)))
      }
      assert(caught20.getMessage === s"$myFilePrettified was not directory")

      val caught21 = intercept[TestFailedException] {
        myFile should (be (directory) and be (directory))
      }
      assert(caught21.getMessage === s"$myFilePrettified was not directory")


      val caught22 = intercept[TestFailedException] {
        myFile should ((be a (directory)) and (be a (directory)))
      }
      assert(caught22.getMessage === s"$myFilePrettified was not a directory")

      val caught23 = intercept[TestFailedException] {
        myFile should (be a (directory) and (be a (directory)))
      }
      assert(caught23.getMessage === s"$myFilePrettified was not a directory")

      val caught24 = intercept[TestFailedException] {
        myFile should (be a (directory) and be a (directory))
      }
      assert(caught24.getMessage === s"$myFilePrettified was not a directory")


      val caught25 = intercept[TestFailedException] {
        myFile should ((be an (directory)) and (be an (directory)))
      }
      assert(caught25.getMessage === s"$myFilePrettified was not an directory")

      val caught26 = intercept[TestFailedException] {
        myFile should (be an (directory) and (be an (directory)))
      }
      assert(caught26.getMessage === s"$myFilePrettified was not an directory")

      val caught27 = intercept[TestFailedException] {
        myFile should (be an (directory) and be an (directory))
      }
      assert(caught27.getMessage === s"$myFilePrettified was not an directory")
    }

    it("should do nothing if the property returns true, when used in a logical-or expression") {

      // second true
      myFile should ((be (directory)) or (be (file)))
      myFile should (be (directory) or (be (file)))
      myFile should (be (directory) or be (file))

      myFile should ((be a (directory)) or (be a (file)))
      myFile should (be a (directory) or (be a (file)))
      myFile should (be a (directory) or be a (file))

      myFile should ((be an (directory)) or (be an (file)))
      myFile should (be an (directory) or (be an (file)))
      myFile should (be an (directory) or be an (file))

      // first true
      myFile should ((be (file)) or (be (directory)))
      myFile should (be (file) or (be (directory)))
      myFile should (be (file) or be (directory))

      myFile should ((be a (file)) or (be a (directory)))
      myFile should (be a (file) or (be a (directory)))
      myFile should (be a (file) or be a (directory))

      myFile should ((be an (file)) or (be an (directory)))
      myFile should (be an (file) or (be an (directory)))
      myFile should (be an (file) or be an (directory))

      // both true
      myFile should ((be (file)) or (be (file)))
      myFile should (be (file) or (be (file)))
      myFile should (be (file) or be (file))

      myFile should ((be a (file)) or (be a (file)))
      myFile should (be a (file) or (be a (file)))
      myFile should (be a (file) or be a (file))

      myFile should ((be an (file)) or (be an (file)))
      myFile should (be an (file) or (be an (file)))
      myFile should (be an (file) or be an (file))
    }

    it("should throw TestFailedException if the both properties return false, when used in a logical-or expression") {

      val caught1 = intercept[TestFailedException] {
        myFile should ((be (directory)) or (be (directory)))
      }
      assert(caught1.getMessage === s"$myFilePrettified was not directory, and $myFilePrettified was not directory")

      val caught2 = intercept[TestFailedException] {
        myFile should (be (directory) or (be (directory)))
      }
      assert(caught2.getMessage === s"$myFilePrettified was not directory, and $myFilePrettified was not directory")

      val caught3 = intercept[TestFailedException] {
        myFile should (be (directory) or be (directory))
      }
      assert(caught3.getMessage === s"$myFilePrettified was not directory, and $myFilePrettified was not directory")


      val caught4 = intercept[TestFailedException] {
        myFile should ((be a (directory)) or (be a (directory)))
      }
      assert(caught4.getMessage === s"$myFilePrettified was not a directory, and $myFilePrettified was not a directory")

      val caught5 = intercept[TestFailedException] {
        myFile should (be a (directory) or (be a (directory)))
      }
      assert(caught5.getMessage === s"$myFilePrettified was not a directory, and $myFilePrettified was not a directory")

      val caught6 = intercept[TestFailedException] {
        myFile should (be a (directory) or be a (directory))
      }
      assert(caught6.getMessage === s"$myFilePrettified was not a directory, and $myFilePrettified was not a directory")


      val caught7 = intercept[TestFailedException] {
        myFile should ((be an (directory)) or (be an (directory)))
      }
      assert(caught7.getMessage === s"$myFilePrettified was not an directory, and $myFilePrettified was not an directory")

      val caught8 = intercept[TestFailedException] {
        myFile should (be an (directory) or (be an (directory)))
      }
      assert(caught8.getMessage === s"$myFilePrettified was not an directory, and $myFilePrettified was not an directory")

      val caught9 = intercept[TestFailedException] {
        myFile should (be an (directory) or be an (directory))
      }
      assert(caught9.getMessage === s"$myFilePrettified was not an directory, and $myFilePrettified was not an directory")
    }

    it("should do nothing if the property returns false, when used in a logical-and expression with not") {

      myFile should (not (be (directory)) and not (be (directory)))
      myFile should ((not be (directory)) and (not be (directory)))
      myFile should (not be (directory) and not be (directory))

      myFile should (not (be a (directory)) and not (be a (directory)))
      myFile should ((not be a (directory)) and (not be a (directory)))
      myFile should (not be a (directory) and not be a (directory))

      myFile should (not (be an (directory)) and not (be an (directory)))
      myFile should ((not be an (directory)) and (not be an (directory)))
      myFile should (not be an (directory) and not be an (directory))
    }

    it("should throw TestFailedException if at least one property returns false, when used in a logical-and expression with not") {

      // second false
      val caught1 = intercept[TestFailedException] {
        myFile should (not (be (directory)) and not (be (file)))
      }
      assert(caught1.getMessage === s"$myFilePrettified was not directory, but $myFilePrettified was file")

      val caught2 = intercept[TestFailedException] {
        myFile should ((not be (directory)) and (not be (file)))
      }
      assert(caught2.getMessage === s"$myFilePrettified was not directory, but $myFilePrettified was file")

      val caught3 = intercept[TestFailedException] {
        myFile should (not be (directory) and not be (file))
      }
      assert(caught3.getMessage === s"$myFilePrettified was not directory, but $myFilePrettified was file")


      val caught4 = intercept[TestFailedException] {
        myFile should (not (be a (directory)) and not (be a (file)))
      }
      assert(caught4.getMessage === s"$myFilePrettified was not a directory, but $myFilePrettified was a file")

      val caught5 = intercept[TestFailedException] {
        myFile should ((not be a (directory)) and (not be a (file)))
      }
      assert(caught5.getMessage === s"$myFilePrettified was not a directory, but $myFilePrettified was a file")

      val caught6 = intercept[TestFailedException] {
        myFile should (not be a (directory) and not be a (file))
      }
      assert(caught6.getMessage === s"$myFilePrettified was not a directory, but $myFilePrettified was a file")


      val caught7 = intercept[TestFailedException] {
        myFile should (not (be an (directory)) and not (be an (file)))
      }
      assert(caught7.getMessage === s"$myFilePrettified was not an directory, but $myFilePrettified was an file")

      val caught8 = intercept[TestFailedException] {
        myFile should ((not be an (directory)) and (not be an (file)))
      }
      assert(caught8.getMessage === s"$myFilePrettified was not an directory, but $myFilePrettified was an file")

      val caught9 = intercept[TestFailedException] {
        myFile should (not be an (directory) and not be an (file))
      }
      assert(caught9.getMessage === s"$myFilePrettified was not an directory, but $myFilePrettified was an file")


      // first false
      val caught10 = intercept[TestFailedException] {
        myFile should (not (be (file)) and not (be (directory)))
      }
      assert(caught10.getMessage === s"$myFilePrettified was file")

      val caught11 = intercept[TestFailedException] {
        myFile should ((not be (file)) and (not be (directory)))
      }
      assert(caught11.getMessage === s"$myFilePrettified was file")

      val caught12 = intercept[TestFailedException] {
        myFile should (not be (file) and not be (directory))
      }
      assert(caught12.getMessage === s"$myFilePrettified was file")


      val caught13 = intercept[TestFailedException] {
        myFile should (not (be a (file)) and not (be a (directory)))
      }
      assert(caught13.getMessage === s"$myFilePrettified was a file")

      val caught14 = intercept[TestFailedException] {
        myFile should ((not be a (file)) and (not be a (directory)))
      }
      assert(caught14.getMessage === s"$myFilePrettified was a file")

      val caught15 = intercept[TestFailedException] {
        myFile should (not be a (file) and not be a (directory))
      }
      assert(caught15.getMessage === s"$myFilePrettified was a file")


      val caught16 = intercept[TestFailedException] {
        myFile should (not (be an (file)) and not (be an (directory)))
      }
      assert(caught16.getMessage === s"$myFilePrettified was an file")

      val caught17 = intercept[TestFailedException] {
        myFile should ((not be an (file)) and (not be an (directory)))
      }
      assert(caught17.getMessage === s"$myFilePrettified was an file")

      val caught18 = intercept[TestFailedException] {
        myFile should (not be an (file) and not be an (directory))
      }
      assert(caught18.getMessage === s"$myFilePrettified was an file")


      // both false
      val caught19 = intercept[TestFailedException] {
        myFile should (not (be (file)) and not (be (file)))
      }
      assert(caught19.getMessage === s"$myFilePrettified was file")

      val caught20 = intercept[TestFailedException] {
        myFile should ((not be (file)) and (not be (file)))
      }
      assert(caught20.getMessage === s"$myFilePrettified was file")

      val caught21 = intercept[TestFailedException] {
        myFile should (not be (file) and not be (file))
      }
      assert(caught21.getMessage === s"$myFilePrettified was file")


      val caught22 = intercept[TestFailedException] {
        myFile should (not (be a (file)) and not (be a (file)))
      }
      assert(caught22.getMessage === s"$myFilePrettified was a file")

      val caught23 = intercept[TestFailedException] {
        myFile should ((not be a (file)) and (not be a (file)))
      }
      assert(caught23.getMessage === s"$myFilePrettified was a file")

      val caught24 = intercept[TestFailedException] {
        myFile should (not be a (file) and not be a (file))
      }
      assert(caught24.getMessage === s"$myFilePrettified was a file")


      val caught25 = intercept[TestFailedException] {
        myFile should (not (be an (file)) and not (be an (file)))
      }
      assert(caught25.getMessage === s"$myFilePrettified was an file")

      val caught26 = intercept[TestFailedException] {
        myFile should ((not be an (file)) and (not be an (file)))
      }
      assert(caught26.getMessage === s"$myFilePrettified was an file")

      val caught27 = intercept[TestFailedException] {
        myFile should (not be an (file) and not be an (file))
      }
      assert(caught27.getMessage === s"$myFilePrettified was an file")
    }

    it("should do nothing if the property returns false, when used in a logical-or expression with not") {

      // first true
      myFile should (not (be (directory)) or not (be (file)))
      myFile should ((not be (directory)) or (not be (file)))
      myFile should (not be (directory) or not be (file))

      myFile should (not (be a (directory)) or not (be a (file)))
      myFile should ((not be a (directory)) or (not be a (file)))
      myFile should (not be a (directory) or not be a (file))

      myFile should (not (be an (directory)) or not (be an (file)))
      myFile should ((not be an (directory)) or (not be an (file)))
      myFile should (not be an (directory) or not be an (file))

      // second true
      myFile should (not (be (file)) or not (be (directory)))
      myFile should ((not be (file)) or (not be (directory)))
      myFile should (not be (file) or not be (directory))

      myFile should (not (be a (file)) or not (be a (directory)))
      myFile should ((not be a (file)) or (not be a (directory)))
      myFile should (not be a (file) or not be a (directory))

      myFile should (not (be an (file)) or not (be an (directory)))
      myFile should ((not be an (file)) or (not be an (directory)))
      myFile should (not be an (file) or not be an (directory))

      // both true
      myFile should (not (be (directory)) or not (be (directory)))
      myFile should ((not be (directory)) or (not be (directory)))
      myFile should (not be (directory) or not be (directory))

      myFile should (not (be a (directory)) or not (be a (directory)))
      myFile should ((not be a (directory)) or (not be a (directory)))
      myFile should (not be a (directory) or not be a (directory))

      myFile should (not (be an (directory)) or not (be an (directory)))
      myFile should ((not be an (directory)) or (not be an (directory)))
      myFile should (not be an (directory) or not be an (directory))
    }

    it("should throw TestFailedException if both properties return false, when used in a logical-or expression with not") {

      val caught1 = intercept[TestFailedException] {
        myFile should (not (be (file)) or not (be (file)))
      }
      assert(caught1.getMessage === s"$myFilePrettified was file, and $myFilePrettified was file")

      val caught2 = intercept[TestFailedException] {
        myFile should ((not be (file)) or (not be (file)))
      }
      assert(caught2.getMessage === s"$myFilePrettified was file, and $myFilePrettified was file")

      val caught3 = intercept[TestFailedException] {
        myFile should (not be (file) or not be (file))
      }
      assert(caught3.getMessage === s"$myFilePrettified was file, and $myFilePrettified was file")


      val caught4 = intercept[TestFailedException] {
        myFile should (not (be a (file)) or not (be a (file)))
      }
      assert(caught4.getMessage === s"$myFilePrettified was a file, and $myFilePrettified was a file")

      val caught5 = intercept[TestFailedException] {
        myFile should ((not be a (file)) or (not be a (file)))
      }
      assert(caught5.getMessage === s"$myFilePrettified was a file, and $myFilePrettified was a file")

      val caught6 = intercept[TestFailedException] {
        myFile should (not be a (file) or not be a (file))
      }
      assert(caught6.getMessage === s"$myFilePrettified was a file, and $myFilePrettified was a file")


      val caught7 = intercept[TestFailedException] {
        myFile should (not (be an (file)) or not (be an (file)))
      }
      assert(caught7.getMessage === s"$myFilePrettified was an file, and $myFilePrettified was an file")

      val caught8 = intercept[TestFailedException] {
        myFile should ((not be an (file)) or (not be an (file)))
      }
      assert(caught8.getMessage === s"$myFilePrettified was an file, and $myFilePrettified was an file")

      val caught9 = intercept[TestFailedException] {
        myFile should (not be an (file) or not be an (file))
      }
      assert(caught9.getMessage === s"$myFilePrettified was an file, and $myFilePrettified was an file")
    }
  }
  describe("the compose method on BePropertyMatcher") {
    it("should return another BePropertyMatcher") {
      val book1 = new Book("A Tale of Two Cities", "Dickens", 1859, 45, true)
      val book2 = new Book("The Handmaid's Tail", "Atwood", 1985, 200, true)
      val badBook = new Book("Some Bad Book", "Bad Author", 1999, 150, false)
      case class Library(books: List[Book])
      val goodLibrary = Library(List(book1, book2))
      val badLibrary = Library(List(badBook, book1, book2))
      val filledWithGoodReads = goodRead compose { (lib: Library) => lib.books.head }
      goodLibrary should be (filledWithGoodReads)
      badLibrary should not be (filledWithGoodReads)
      
      // goodLibrary shouldBe filledWithGoodReads
    }
  }
  describe("A factory method on BePropertyMatcher's companion object") {
    it("should produce a be-property-matcher that executes the passed function when its apply is called") {
      val f = { (s: String) => BePropertyMatchResult(s.isEmpty, "empty") }
      val empty = BePropertyMatcher(f)
      "" should be (empty)
      "x" should not be (empty)
      "xx" should not be (empty)
      "xxx" should not be (empty)
      "xxxx" should not be (empty)
      
      // "" shouldBe empty
    }
  }
}
