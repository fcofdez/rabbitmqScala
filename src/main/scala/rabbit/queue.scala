package rabbit

import com.rabbitmq.client._

object Rabbit {
  def main(args: Array[String]){

    var factory = new ConnectionFactory()
      factory.setHost("localhost")
    val connection = factory.newConnection
    val channel = connection.createChannel

    channel.queueDeclare("task_queue", true, false, false, null)

    channel.basicQos(1)

    val consumer = new QueueingConsumer(channel)
      channel.basicConsume("task_queue", false, consumer)

    while(true){
      var delivery = consumer.nextDelivery
      var message = new String(delivery.getBody())
        println(message)

      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false)
    }
  }

  def doWork(str: String){
    
  }
}

// vim: set ts=2 sw=2 et:
