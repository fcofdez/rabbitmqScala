package rabbit

import akka.actor.Actor
import akka.actor.ActorRef
import akka.dispatch.Filter
import com.rabbitmq.client._
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.SmallestMailboxRouter


case class Greeting(who: String)

class GreetingActor extends Actor with ActorLogging{
  var acc = 0;
  def receive = {
    case Greeting(who) =>
      acc += 1
      log.info("Hello" + who)

  }
}

object RabbitMQConnection {

  private val connection: Connection = null;

  /**
   * Return a connection if one doesn't exist. Else create
   * a new one
   */
  def getConnection(): Connection = {
    connection match {
      case null => {
        val factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.newConnection();
      }
      case _ => connection
    }
  }
}


class MsgConsumer(channel: Channel, target: ActorRef) extends DefaultConsumer(channel){

  override def handleDelivery(consumer_tag: String,
                              envelope: Envelope,
                              properties: AMQP.BasicProperties,
                              body: Array[Byte]): Unit =
  {
    val delivery_tag = envelope.getDeliveryTag
    val body_text = new String(body)
    try {
      val msg = body_text
      target ! Greeting(msg)
      channel.basicAck(delivery_tag, false)
    } catch {
      case e: Exception =>
        channel.basicReject(delivery_tag, false)
    }

  }
}

object Rabbit {
  def main(args: Array[String]){

    val connection = RabbitMQConnection.getConnection
    val channel = connection.createChannel
    channel.queueDeclare("task_queue", true, false, false, null)
    channel.basicQos(1)

    val system = ActorSystem("MySystem")


    val event_filter = system.actorOf(
      Props[GreetingActor],
      name="EventFilter"
    )

    channel.basicConsume(
      "task_queue",
      false, // do not auto ack
      new MsgConsumer(channel, event_filter)
    )




    //val consumer = new QueueingConsumer(channel)
    //channel.basicConsume("task_queue", false, consumer)

    //while(true){
    //var delivery = consumer.nextDelivery
    //var message = new String(delivery.getBody())
    //println(message)
    //doWork(message)

    //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false)
    //}
  }

  def doWork(str: String){
    Thread.sleep(9000)
  }
}
