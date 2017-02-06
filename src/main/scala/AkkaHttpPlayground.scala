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

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, pathSingleSlash}
import akka.stream.ActorMaterializer

import scala.io.StdIn

/**
 * AkkaHttpPlayground
 *
 * @author Elliot Wright <hello@elliotdwright.com>
 */
object AkkaHttpPlayground extends App {
  implicit val system = ActorSystem("AkkaHttpPlayground")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val route =
    pathSingleSlash {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<p>Hello, World!</p>"))
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
