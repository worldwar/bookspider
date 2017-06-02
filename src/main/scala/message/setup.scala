package message

import actor.{CatalogMessage, CatalogActor, Context}
import akka.actor.{Props, ActorRef}
import com.newmotion.akka.rabbitmq
import com.newmotion.akka.rabbitmq._
import scala.concurrent.duration._

import org.json4s._
import org.json4s.native.JsonMethods._

object Setup {
  implicit val formats = DefaultFormats
  def setup() = {
    val factory: ConnectionFactory = new rabbitmq.ConnectionFactory()
    factory.setUri("amqp://bookreader:bookreader@localhost/%2F")
    val connectionActor: ActorRef = Context.actorSystem.actorOf(ConnectionActor.props(factory, 10 seconds), "message-connection")
    val catalogActor: ActorRef = Context.actorSystem.actorOf(Props(new CatalogActor("catalogActor")), name = "catalogActor")
    def setupChannel(channel: Channel, self: ActorRef) {
      println("start to declare queue")
      val queue = channel.queueDeclare("add-book", true, false, false, null).getQueue

      channel.queueBind(queue, "bookreadertopic", "add-book")
      println("start to create consumer")
      val consumer = new DefaultConsumer(channel) {
        override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
          val in: String = new String(body, "utf-8")
          println("received: " + in)
          val request: AddBookRequest = parse(in).extract[AddBookRequest]
          println("url: " + request.url)
          catalogActor ! CatalogMessage(request.url)
        }
      }
      channel.basicConsume(queue, true, consumer)
    }
    println("start to create channel")
    connectionActor ! CreateChannel(ChannelActor.props(setupChannel), name = Some("channel-actor"))
  }
}
