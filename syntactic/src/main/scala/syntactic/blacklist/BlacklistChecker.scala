package syntactic.blacklist

import syntactic.{Checker, Violation}

import scala.meta._

/**
 * @param rules rules to be checked
 */
class BlacklistChecker private(rules: List[BlacklistRule]) extends Checker {
  require(rules.nonEmpty, "checker must have at least 1 rule")

  // matches trees that do not match any rule
  private val defaultPartFunc: PartialFunction[Tree, List[Violation]] = {
    case _ => Nil
  }

  private val checkFuncs = rules.map(_.checkFunc.orElse(defaultPartFunc))

  override def checkNode(node: Tree): List[Violation] = {
    checkFuncs.map(_.apply(node)).foldLeft(List.empty[Violation])(_ ++ _)
  }

}

object BlacklistChecker {

  def apply(rule: BlacklistRule, rules: BlacklistRule*): BlacklistChecker = new BlacklistChecker(rule :: rules.toList)

}
