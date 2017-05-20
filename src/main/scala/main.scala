import actor.{Context, CatalogActor, CatalogMessage}
import akka.actor.{ActorRef, ActorSystem, Props}

object main extends App {
  val system: ActorSystem = Context.actorSystem
  val catalogActor: ActorRef = system.actorOf(Props(new CatalogActor("catalogActor")), name = "catalogActor")
  catalogActor ! CatalogMessage("http://book.qidian.com/info/1005752892#Catalog")
}
