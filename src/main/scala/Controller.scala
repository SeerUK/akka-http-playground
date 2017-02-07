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

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.http.scaladsl.server.RequestContext

/**
 * Controller
 *
 * @author Elliot Wright <hello@elliotdwright.com>
 */
trait Controller extends Actor with ActorLogging {

  def ctx: RequestContext
  def target: ActorRef

}
