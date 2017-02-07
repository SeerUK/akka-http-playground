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

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.stream.ActorMaterializer

import scala.concurrent.Promise
import scala.io.StdIn

/**
 * AkkaHttpPlayground
 *
 * @author Elliot Wright <hello@elliotdwright.com>
 */
object AkkaHttpPlayground extends App {

  final class ImperativeRequestContext(ctx: RequestContext, promise: Promise[RouteResult]) {
    private implicit val ec = ctx.executionContext

    def complete(obj: ToResponseMarshallable): Unit =
      ctx.complete(obj).onComplete(promise.complete)

    def fail(error: Throwable): Unit =
      ctx.fail(error).onComplete(promise.complete)
  }

  def imperativelyComplete(inner: ImperativeRequestContext => Unit): Route = { ctx: RequestContext =>
    val promise = Promise[RouteResult]()
    inner(new ImperativeRequestContext(ctx, promise))
    promise.future
  }

  implicit val system = ActorSystem("AkkaHttpPlayground")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route =
    pathSingleSlash {
      get {
        imperativelyComplete { ctx =>
          system.actorOf(Props[TestController]) ! TestController.Handle(ctx)
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
