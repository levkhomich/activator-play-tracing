package actors

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.{Props, ActorLogging, Actor}
import akka.pattern.pipe
import akka.util.Timeout
import com.github.levkhomich.akka.tracing.pattern.ask
import com.github.levkhomich.akka.tracing.play.PlayActorTracing

import model._

class Evaluator extends Actor with PlayActorTracing with ActorLogging {

  import context.dispatcher
  implicit val evalAskTimeout: Timeout = Duration(1, SECONDS)

  def eval(left: Tree, right: Tree, parent: Tree)(f: (Double, Double) => Double): Future[Double] = {
    def eval(tree: Tree, parent: Tree): Future[Double] = {
      (context.system.actorOf(Props[Evaluator]) ? tree.asChildOf(parent)).mapTo[Double]
    }
    eval(left, parent) zip eval(right, parent) map { case (r1, r2) => f(r1, r2) }
  }

  override def receive: Receive = {
    case p@Add(t1, t2) => eval(t1, t2, p)(_ + _) pipeTo sender
    case p@Sub(t1, t2) => eval(t1, t2, p)(_ - _) pipeTo sender
    case p@Mul(t1, t2) => eval(t1, t2, p)(_ * _) pipeTo sender
    case p@Div(t1, t2) => eval(t1, t2, p)(_ / _) pipeTo sender
    case p@Num(t) =>
      sender ! t
  }
}
