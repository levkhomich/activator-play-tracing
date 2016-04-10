package controllers

import javax.inject.{Inject, Singleton}

import actors.Parser
import akka.actor._
import akka.util.Timeout
import akka.pattern.ask
import com.github.levkhomich.akka.tracing.TracingSupport
import com.github.levkhomich.akka.tracing.play.PlayControllerTracing
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.{Action, Controller}

import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global

@Singleton
class Calculator @Inject()(system: ActorSystem)
  extends Controller with PlayControllerTracing {

  private lazy val parser = system.actorOf(Props[Parser], "parser")

  def index = Action {
    Ok(views.html.index("Calculator"))
  }

  implicit val sentimentAskTimeout: Timeout = Duration(10, SECONDS)

  def request() = Action.async { implicit request =>
    request.body.asJson.flatMap(json => (json \ "expr").asOpt[String]) match {
      case Some(expr) =>
        // derived request (Parser.Expression) is marked as child of play request to trace
        // its processing as separate (child) span of original request
        val clientRequest = new TracingSupport {}
        trace.createChild(clientRequest, request)
        (parser ? Parser.Expression(expr)).mapTo[Double].map { resp =>
          // play response shouldn't be marked by ".asResponseTo"
          Ok(JsObject(Seq(
            "result" -> JsNumber(resp)
          )))
        }
      case None =>
        Future.successful(BadRequest)
    }
  }

  override implicit def actorSystem: ActorSystem = {
    system
  }
}
