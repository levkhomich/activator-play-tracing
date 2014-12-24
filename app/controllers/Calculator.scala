package controllers

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.util.Timeout
import com.github.levkhomich.akka.tracing.pattern.ask
import com.github.levkhomich.akka.tracing.play.PlayControllerTracing
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.{Action, Controller}
import play.api.Play.current

import actors.{Parser, Actors}

object Calculator extends Controller with PlayControllerTracing {

  def index = Action {
    Ok(views.html.index("Calculator"))
  }

  implicit val sentimentAskTimeout: Timeout = Duration(10, SECONDS)

  def request() = Action.async { implicit request =>
    request.body.asJson.flatMap(json => (json \ "expr").asOpt[String]) match {
      case Some(expr) =>
        // derived request (Parser.Expression) is marked as child of play request to trace
        // its processing as separate (child) span of original request
        (Actors.parser ? Parser.Expression(expr).asChildOf(request)).mapTo[Double].map { resp =>
          // play response shouldn't be marked by ".asResponseTo"
          Ok(JsObject(Seq(
            "result" -> JsNumber(resp)
          )))
        }
      case None =>
        Future.successful(BadRequest)
    }
  }

}
