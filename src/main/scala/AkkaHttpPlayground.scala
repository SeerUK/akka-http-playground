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

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directive1
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._
import scala.io.StdIn
import scala.reflect.ClassTag
import scala.util.{Success, Try}

/**
 * AkkaHttpPlayground
 *
 * @author Elliot Wright <hello@elliotdwright.com>
 */
object AkkaHttpPlayground extends App {

  implicit val system = ActorSystem("AkkaHttpPlayground")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout: Timeout = 30.seconds

  def createActor[A <: Actor: ClassTag](implicit as: ActorSystem): ActorRef =
    as.actorOf(Props[A])

  def askActor(ref: ActorRef, message: Any): Directive1[Try[Any]] =
    onComplete(ref ? message)

  val route = encodeResponse {
    pathSingleSlash {
      (get & askActor(createActor[RequestHandler], RequestHandler.Handle)) {
        case Success(result: RequestHandler.Result) => complete(result.data)
        case _ => complete(StatusCodes.InternalServerError)
      }
    }
  }

  val addr = "0.0.0.0"
  val port = 9000
  val bindingFuture = Http().bindAndHandle(route, addr, port)

  println(s"Server online at http://$addr:$port/\nPress RETURN to stop...")

  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind()) // Unbind the port
    .onComplete(_ => system.terminate()) // Shutdown actor system
}

object RequestHandler {

  case object Handle

  case class Result(data: String)

}

class RequestHandler extends Actor {

  import RequestHandler._

  def receive: Receive = {
    case Handle =>
      sender ! Result("Hello, World!")
      context.stop(self)
  }

}
