import actor.{CatalogActor, CatalogMessage}
import akka.actor.{ActorRef, ActorSystem, Props}

object main extends App {
  val system: ActorSystem = ActorSystem("catalog")
  val catalogActor: ActorRef = system.actorOf(Props(new CatalogActor("catalogActor")), name = "catalogActor")
  catalogActor ! CatalogMessage("http://book.qidian.com/info/1004595865#Catalog")
}
