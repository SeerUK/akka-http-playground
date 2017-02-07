/**
 * This file is part of the "akkaHttpPlayground" project.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the LICENSE is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Success

/**
 * AkkaHttpPlayground
 *
 * @author Elliot Wright <hello@elliotdwright.com>
 */
object AkkaHttpPlayground extends App {

  implicit val system = ActorSystem("AkkaHttpPlayground")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  object RequestHandler {

    case object Handle

    case class Result(data: String)

  }

  class RequestHandler extends Actor {

    import RequestHandler._

    def receive: Receive = {
      case Handle =>
        sender ! "ok"
        context.stop(self)
    }

  }

  val route =
    pathSingleSlash {
      get {
        // We need a timeout for the ask Q_Q
        implicit val askTimeout: Timeout = 3.seconds
        val actor = system.actorOf(Props[RequestHandler])
        val response = actor ? RequestHandler.Handle

        onComplete(response) {
          case Success(result: String) => complete(result)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }

  val addr = "0.0.0.0"
  val port = 8080
  val bindingFuture = Http().bindAndHandle(route, addr, port)

  println(s"Server online at http://$addr:$port/\nPress RETURN to stop...")

  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind()) // Unbind the port
    .onComplete(_ => system.terminate()) // Shutdown actor system
}
