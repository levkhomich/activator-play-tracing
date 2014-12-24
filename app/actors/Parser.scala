package actors

import scala.concurrent.duration._
import scala.util.parsing.combinator.JavaTokenParsers

import akka.actor.{Props, Actor}
import akka.pattern.pipe
import akka.util.Timeout
import com.github.levkhomich.akka.tracing.TracingSupport
import com.github.levkhomich.akka.tracing.play.PlayActorTracing
import com.github.levkhomich.akka.tracing.pattern.ask

import model._

class Parser extends Actor with PlayActorTracing {

  import context.dispatcher
  implicit val evalAskTimeout: Timeout = Duration(1, SECONDS)

  val evaluator = context.system.actorOf(Props[Evaluator])

  override def receive: Receive = {
    case r@Parser.Expression(expr) =>
      val parser = new ExprParser()
      val tree = parser.parseAll(parser.expr, expr).get
      evaluator ? tree.asChildOf(r) pipeTo sender
  }

}

object Parser {
  def props(): Props =
    Props(new Parser)

  final case class Expression(value: String) extends TracingSupport
}


class ExprParser extends JavaTokenParsers {

  lazy val expr: Parser[Tree] = term ~ rep("[+-]".r ~ term) ^^ {
    case t ~ ts => ts.foldLeft(t) {
      case (t1, "+" ~ t2) => Add(t1, t2)
      case (t1, "-" ~ t2) => Sub(t1, t2)
    }
  }

  lazy val term = factor ~ rep("[*/]".r ~ factor) ^^ {
    case t ~ ts => ts.foldLeft(t) {
      case (t1, "*" ~ t2) => Mul(t1, t2)
      case (t1, "/" ~ t2) => Div(t1, t2)
    }
  }

  lazy val factor = "(" ~> expr <~ ")" | num

  lazy val num = floatingPointNumber ^^ { t => Num(t.toDouble) }
}