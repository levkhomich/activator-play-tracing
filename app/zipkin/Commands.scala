package zipkin

import scala.sys.process._

object Start extends App {
  "./project/zipkin.sh start".run()
}

object Stop extends App {
  "./project/zipkin.sh stop".!
}
