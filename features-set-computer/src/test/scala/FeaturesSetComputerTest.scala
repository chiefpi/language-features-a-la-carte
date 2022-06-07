import FeaturesSetComputerTest.TestFeaturesProvider
import org.junit.Assert.{assertEquals, fail}
import org.junit.Test
import syntactic.CheckResult
import syntactic.whitelist.Feature.{AtomicFeature, CompositeFeature}
import syntactic.whitelist.{FeaturesProvider, PredefFeatures, ScalaFeature, WhitelistChecker}

import scala.meta.{Defn, Source, Term, XtensionParseInputLike}

class FeaturesSetComputerTest {

  private def parse(codeStr: String): Source = codeStr.parse[Source].get

  @Test
  def featuresSetComputationFromTreeTest(): Unit = {
    val codeStr =
      """
        |object Main {
        |  def main(args: Array[String]): Unit = {
        |    println("Hello world")
        |  }
        |}
        |""".stripMargin
    val src = parse(codeStr)
    val exp = Some(Set(
      PredefFeatures.LiteralsAndExpressions,
      PredefFeatures.Defs,
      PredefFeatures.PolymorphicTypes,
      PredefFeatures.ADTs
    ))
    val featuresSetComputer = new FeaturesSetComputer(PredefFeatures.allDefinedFeatures)
    assertEquals(exp, featuresSetComputer.minimalFeaturesSetFor(src))
  }

  @Test
  def featuresSetComputationFromViolationsTest(): Unit = {
    val codeStr =
      """
        |object Foo {
        |  def bar(z: String): Unit = {
        |    val x = 5 + 42 + z.length
        |    val y = f(x, 0)
        |    println(y)
        |    var u = 1
        |    u += 2
        |    println(u)
        |  }
        |}
        |""".stripMargin
    val src = parse(codeStr)
    val initChecker = WhitelistChecker(PredefFeatures.LiteralsAndExpressions, PredefFeatures.ADTs)
    val checkResult = initChecker.checkSource(src)
    assert(checkResult.isInstanceOf[CheckResult.Invalid])
    val featuresSetComputer = new FeaturesSetComputer(PredefFeatures.allDefinedFeatures)
    val requiredFeatures = featuresSetComputer.minimalFeaturesSetToResolve(checkResult.asInstanceOf[CheckResult.Invalid].violations)
    val exp = Some(Set(PredefFeatures.Vals, PredefFeatures.ImperativeConstructs, PredefFeatures.Defs))
    assertEquals(exp, requiredFeatures)
  }

  @Test
  def nonDisjointFeaturesSetTest(): Unit = {
    val codeStr =
      """
        |object Bar {
        |  def baz(s: String, u: Int): Boolean = {
        |    val cst = 42
        |    var p = u + cst
        |    for (ch <- s.toList){
        |      p += u
        |    }
        |    u % 3 == 0
        |  }
        |}
        |""".stripMargin
    val src = parse(codeStr)
    val availableFeatures = TestFeaturesProvider.allDefinedFeatures ++ List(PredefFeatures.LiteralsAndExpressions, PredefFeatures.ForExpr)
    val featuresSetComputer = new FeaturesSetComputer(availableFeatures)
    val exp = Some(Set(
      TestFeaturesProvider.VarsAndValsFt,
      TestFeaturesProvider.DefAndObjectFt,
      PredefFeatures.LiteralsAndExpressions,
      PredefFeatures.ForExpr
    ))
    assertEquals(exp, featuresSetComputer.minimalFeaturesSetFor(src))
  }

}

object FeaturesSetComputerTest {

  private object TestFeaturesProvider extends FeaturesProvider {

    @ScalaFeature
    case object VarsFt extends AtomicFeature({
      case _ : Defn.Var => true
    })

    @ScalaFeature
    case object VarsAndValsFt extends AtomicFeature({
      case _ : Defn.Var => true
      case _ : Defn.Val => true
    })

    @ScalaFeature
    case object OopFt extends AtomicFeature({
      case _ : Defn.Object => true
      case _ : Defn.Class => true
      case _ : Defn.Trait => true
    })

    @ScalaFeature
    case object DefFt extends AtomicFeature({
      case _ : Defn.Def => true
      case _ : Term.Param => true
    })

    @ScalaFeature
    case object DefAndObjectFt extends CompositeFeature(DefFt, OopFt)

    @ScalaFeature
    case object ImcompleteForFt extends AtomicFeature({
      case _ : Term.For => true
    })

  }

}