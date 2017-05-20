package actor

import akka.actor.ActorSystem

object Context {
  val actorSystem = ActorSystem("catalog")
}
