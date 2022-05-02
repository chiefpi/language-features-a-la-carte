package syntactic_checker

import java.io.File
import scala.io.BufferedSource
import scala.meta.{Dialect, Source, Tree}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try, Using}

trait Checker[+V <: Violation] {

  /**
   * Check whether the input tree uses only allowed features
   *
   * @param tree the tree to be checked
   * @return Some[Violation] if a violation was found, None o.w.
   */
  def checkTree(tree: Tree): Option[V]

  /**
   * Check whether the input program (as a source) uses only allowed features
   *
   * @param src the source to be checked
   * @return a CheckResult (Valid, Invalid or ParsingError)
   */
  final def checkSource(src: Source): CheckResult[V] = {
    val violations = src.collect {
      case tree: Tree => checkTree(tree)
    }.flatten
    if (violations.isEmpty) CheckResult.Valid
    else CheckResult.Invalid(violations)
  }

  /**
   * Check whether the input program (as a string) uses only allowed features
   *
   * @param dialect    the Scala dialect that the parsing should use
   * @param sourceCode the string to be checked
   * @return a CheckResult (Valid, Invalid or ParsingError)
   */
  final def checkCodeString(dialect: Dialect, sourceCode: String): CheckResult[V] = {
    val inputWithDialect = dialect(sourceCode)
    Try {
      inputWithDialect.parse[Source].get
    } match {
      case Success(source) => checkSource(source)
      case Failure(NonFatal(throwable)) => CheckResult.ParsingError(throwable)
      case Failure(fatal) => throw fatal
    }
  }

  /**
   * Check whether the program contained in the given source uses only allowed features
   *
   * @param dialect        the Scala dialect that the parsing should use
   * @param bufferedSource the source of the program to be checked
   * @return a CheckResult (Valid, Invalid or ParsingError)
   */
  final def checkBufferedSource(dialect: Dialect, bufferedSource: BufferedSource): CheckResult[V] = {
    val content = Using(bufferedSource) { bufferedSource =>
      bufferedSource.getLines().mkString("\n")
    }
    content.map(checkCodeString(dialect, _)) match {
      case Failure(exception) => CheckResult.ParsingError(exception)
      case Success(checkRes) => checkRes
    }
  }

  /**
   * Check whether the program contained in the given file uses only allowed features
   *
   * @param dialect the Scala dialect that the parsing should use
   * @param file    the file containing the program to be checked
   * @return a CheckResult (Valid, Invalid or ParsingError)
   */
  final def checkFile(dialect: Dialect, file: File): CheckResult[V] = {
    checkBufferedSource(dialect, scala.io.Source.fromFile(file))
  }

  /**
   * Check whether the program contained in the file with the given filename uses only allowed features
   *
   * @param dialect  the Scala dialect that the parsing should use
   * @param filename the name of the file containing the program to be checked
   * @return a CheckResult (Valid, Invalid or ParsingError)
   */
  final def checkFile(dialect: Dialect, filename: String): CheckResult[V] = {
    checkBufferedSource(dialect, scala.io.Source.fromFile(filename))
  }

}
