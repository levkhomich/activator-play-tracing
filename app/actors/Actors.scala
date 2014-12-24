package actors

import akka.actor.Props
import play.api._
import play.api.libs.concurrent.Akka

/**
 * Lookup for actors
 */
object Actors {

    private def actors(implicit app: Application) = app.plugin[Actors]
        .getOrElse(sys.error("Actors plugin not registered"))

    def parser(implicit app: Application) = actors.parser

}

/**
 * Manages the creation of actors.
 *
 * Discovered by Play in the `play.plugins` file.
 */
class Actors(app: Application) extends Plugin {

    private def system = Akka.system(app)

    override def onStart() = {
    }

    private lazy val parser = system.actorOf(Props[Parser], "parser")

}
