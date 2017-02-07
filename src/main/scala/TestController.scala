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

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
 * TestController
 *
 * @author Elliot Wright <hello@elliotdwright.com>
 */
class TestController extends Actor {

  override def receive: Receive = {
    case _ =>
  }

}
