package zipkin

import java.io.File

import scala.sys.process._

object Start extends App {
  new File("./project/zipkin.sh").setExecutable(true)
  "./project/zipkin.sh start".run()
}

object Stop extends App {
  new File("./project/zipkin.sh").setExecutable(true)
  "./project/zipkin.sh stop".!
}
