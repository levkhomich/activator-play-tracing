package model

import com.github.levkhomich.akka.tracing.TracingSupport

sealed abstract class Tree extends TracingSupport
case class Add(t1: Tree, t2: Tree) extends Tree
case class Sub(t1: Tree, t2: Tree) extends Tree
case class Mul(t1: Tree, t2: Tree) extends Tree
case class Div(t1: Tree, t2: Tree) extends Tree
case class Num(t: Double) extends Tree
