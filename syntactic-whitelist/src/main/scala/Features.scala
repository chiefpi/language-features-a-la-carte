import Feature._
import scala.meta._

object Features {

  // TODO restrictions on modifiers

  case object AllowLiteralsAndExpressions extends Feature {
    override def check(tree: Tree): Boolean = tree match {
      case _ : Lit.Boolean => true
      case _ : Lit.Unit => true
      case _ : Lit.Int => true
      case _ : Lit.Double => true
      case _ : Lit.Float => true
      case _ : Lit.Long => true
      case _ : Lit.Byte => true
      case _ : Lit.Short => true
      case _ : Lit.Char => true
      case _ : Lit.Symbol => true
      case _ : Lit.String => true
      case _ : Term.Select => true
      case _ : Term.ApplyUnary => true
      case _ : Term.Apply => true
      case _ : Term.ApplyInfix => true
      case _ : Term.If => true
      case _ : Term.Interpolate => true
      case _ => false
    }
  }

  case object AllowNull extends AtomicFeature({
    case _ : Lit.Null => true
  })

  case object AllowVals extends AtomicFeature({
    case _ : Decl.Val => true
    case _ : Defn.Val => true
    case _ : Term.Assign => true
    case _ : Pat.Var => true
  })

  case object AllowDefs extends AtomicFeature({
    case _ : Decl.Def => true
    case _ : Defn.Def => true
    case _ : Term.Assign => true
    case _ : Term.Repeated => true
    case _ : Type.Repeated => true
  })

  case object AllowADTs extends AtomicFeature({
    case Defn.Class((modLs, name, paramLs, primaryCtor, template)) => {
      modLs.exists {
        case Mod.Case() => true
        case Mod.Sealed() => true
        case _ => false
      }
    }
    case Defn.Trait((modLs, name, paramLs, primaryCtor, template)) => {
      modLs.exists {
        case Mod.Sealed() => true
        case _ => false
      }
    }
    case Defn.Object((modLs, name, template)) => {
      modLs.exists {
        case Mod.Case() => true
        case _ => false
      }
    }
    case _ : Term.Super => true
    case _ : Defn.Enum => true
    case _ : Defn.EnumCase => true
    case _ : Defn.RepeatedEnumCase => true
    case _ : Term.This => true
    case _ : Term.Tuple => true
    case _ : Type.Tuple => true
    case _ : Term.Match => true
    case _ : Term.PartialFunction => true
    case _ : Term.New => true
    case _ : Lit => true
    case _ : Pat.Wildcard => true
    case _ : Pat.SeqWildcard => true
    case _ : Pat.Var => true
    case _ : Pat.Bind => true
    case _ : Pat.Alternative => true
    case _ : Pat.Tuple => true
    case _ : Pat.Extract => true
    case _ : Pat.ExtractInfix => true
    case _ : Pat.Interpolate => true
    case _ : Pat.Typed => true
    case _ : Case => true
    case _ : Ctor.Primary => true
    case _ : Init => true
    case _ : Mod.Case => true
    case _ : Mod.Sealed => true
  })

  case object AllowLiteralFunctions extends AtomicFeature({
    case _ : Term.Function => true
    case _ : Type.Function => true
  })

  case object AllowForExpr extends AtomicFeature({
    case _ : Term.For => true
    case _ : Term.ForYield => true
  })

  case object AllowPolymorphicTypes extends AtomicFeature({
    case _ : Decl.Type => true
    case _ : Defn.Type => true
    case _ : Type.Param => true
  })

  case object AllowLaziness extends AtomicFeature({
    case _ : Type.ByName => true
  })

  case object AllowRecursiveCalls extends AtomicFeature({
    ??? // TODO
  })

  private case object BasicOopAddition extends AtomicFeature({
    case Defn.Class((modLs, name, paramLs, primaryCtor, template)) => true
    case Defn.Trait((modLs, name, paramLs, primaryCtor, template)) => true
    case Defn.Object((modLs, name, template)) => true
    case _ : Term.Super => true
    case _ : Ctor.Secondary => true
  })

  private case object AdvancedOOPAddition extends AtomicFeature({
    case _ : Term.NewAnonymous => true
      // TODO
  })

  case object AllowBasicOop extends CompositeFeature(AllowADTs, BasicOopAddition)

  case object AllowAdvancedOop extends CompositeFeature(AllowBasicOop, AdvancedOOPAddition)

  case object AllowImperativeConstructs extends AtomicFeature({
    case _ : Decl.Var => true
    case _ : Defn.Var => true
    case _ : Term.Return => true
    case _ : Term.Throw => true
    case _ : Term.Try => true
    case _ : Term.TryWithHandler => true
    case _ : Term.While => true
    case _ : Term.Do => true
  })

  case object AllowContextualConstructs extends AtomicFeature({
    case _ : Defn.Given => true
    case _ : Defn.GivenAlias => true
    case _ : Term.ApplyUsing => true
    case _ : Term.ContextFunction => true
    case _ : Type.ContextFunction => true
  })

  /*
  Not implemented:
    Defn.Macro
    Defn.ExtensionGroup
    Term.Annotate
    Term.Eta
    Term.Xml
    Term.QuotedMacroExpr
    Term.QuotedMacroType
    Term.SplicedMacroExpr
    Term.PolyFunction
    Type.Singleton
    Type.Apply
    Type.ApplyInfix
    Type.With
    Type.And
    Type.Or
    Type.Refine
    Type.Existential
    Type.Annotate
    Type.Lambda
    Type.Method
    Term.ApplyType
    Type.Select
    Type.Project
    Type.Var
    Type.PolyFunction
    Type.Match
    Pat.Xml
    Term.Macro
    Term.Given (found in doc. but not found by Intellij)
    TypeCase
    name.Indeterminate
   */

}
