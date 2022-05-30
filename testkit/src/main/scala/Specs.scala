package testkit

import carte.Rules.{Rule, NoRule}
import carte.Violations.{Violation, NoViolation}
import carte.Spans.{Span, NoSpan}

import scala.meta._
import scala.meta.dialects.Scala3

/**
  * Specifications for test input in the form of comments
  * The comment on the head specifies checker parameters.
  * The other comments mark positions and messages of violations.
  * Comments should be wrapped in /* */
  */
object Specs {

  def fromPath(path: TestPath, dialect: Dialect = Scala3): List[String] = {
    val input = path.read
    val tree = dialect(input).parse[Source].get
    val commentTokens = findAllComments(tree.tokens)
    commentTokens.drop(1).map(tokenToString)
  }

  private def findAllComments(tokens: Tokens): List[Token] = {
    tokens
      .filter { x =>
        x.is[Token.Comment] && x.syntax.startsWith("/*")
      }
      .toList
  }

  private def tokenToString(token: Token): String = {
    val pos = token.pos
    val str = token.syntax.stripPrefix("/*").stripSuffix("*/")
    val lines = str.split("\n")
    val column = lines(1).indexOf('^')
    val msg = lines.last.strip()
    s"${pos.startLine}:${column}: ${msg}"
  }

}