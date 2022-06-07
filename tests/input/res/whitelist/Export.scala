/*
mode = whitelist
allowededFeatures = [
  LiteralsAndExpressions,
  BasicOop,
  Defs,
  Exports
]
*/

class Foo(x: Int){
  def getMulBy(y: Int): Int = x*y
}

class Bar(f: Foo){
  export f.getMulBy
}
