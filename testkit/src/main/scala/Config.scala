package testkit

import carte.Rules.Rule
import syntactic.Rule._

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.combinator._

/**
  * Configuration for test input
  *
  * @param blacklist
  * @param rules
  */
case class Config(blacklist: Boolean, rules: List[Rule])

/**
  * An empty config which blacklists nothing 
  */
object EmptyConfig extends Config(true, Nil)

object Config extends StandardTokenParsers {

  lexical.delimiters ++= List("=", ",", "[", "]")
  lexical.reserved ++= List("mode", "rules", "blacklist", "whitelist", "NoNull", "NoCast", "NoVar", "NoWhile")

  def mode: Parser[Boolean] =
    "blacklist" ^^^ true |
    "whitelist" ^^^ false
  def rule: Parser[Rule] =
    // TODO: generalize to more rules
    "NoNull" ^^^ NoNull |
    "NoCast" ^^^ NoCast |
    "NoVar" ^^^ NoVar |
    "NoWhile" ^^^ NoWhile
  def rules: Parser[List[Rule]] = rule ~ rep("," ~> rule) ^^ {
    case r ~ rs => r :: rs
  }
  def config: Parser[Config] = "mode" ~ "=" ~ mode ~ "rules" ~ "=" ~ ("[" ~> rules <~ "]") ^^ {
    case _ ~ _ ~ md ~ _ ~ _ ~ rs => new Config(md, rs)
  }

  def fromString(str: String): Config = {
    val tokens = new lexical.Scanner(str)
    phrase(config)(tokens) match {
      case Success(cfg, _) => cfg
      case e => {
        println(e)
        EmptyConfig
      }
    }
  }
}