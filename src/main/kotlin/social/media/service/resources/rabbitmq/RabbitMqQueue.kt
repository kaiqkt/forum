package social.media.service.resources.rabbitmq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Delivery
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.*
import social.media.service.domain.entities.Notification
import social.media.service.domain.gateways.messaging.QueueService
import social.media.service.domain.services.NotificationService
import java.util.logging.Level

@Component
class RabbitMqQueue(
    private val sender: Sender,
    private val receiver: Receiver,
    private val objectMapper: ObjectMapper
) : QueueService {

    private val log: Logger = LoggerFactory.getLogger(NotificationService::class.java)

    override fun sendMessage(receiverId: String, content: Notification, topicName: String): Mono<Void> {
        val routingKey = "$topicName-$receiverId"
        val body = objectMapper.writeValueAsString(content).toByteArray()
        val message = OutboundMessage(topicName, routingKey, body)
        val declareExchange: Mono<AMQP.Exchange.DeclareOk> = sender.declareExchange(
            ExchangeSpecification.exchange()
                .name(topicName)
                .durable(true)
                .type("topic")
        )

        return declareExchange
            .flatMap {
                log.info("Sent to queue {} payload {}", routingKey, content)

                sender.send(
                    Mono.fromSupplier { message }
                )
            }
    }

    override fun receive(receiverId: String, topicName: String): Flux<Notification> {
        val routingKey = "$topicName-$receiverId"

        val declareQueue = sender
            .declareQueue(QueueSpecification.queue())
            .log("declare-queue", Level.FINER)

        val bindQueue = declareQueue
            .flatMap { declareOk: AMQP.Queue.DeclareOk ->
                sender.bindQueue(
                    BindingSpecification.binding()
                        .queue(declareOk.queue)
                        .exchange(topicName)
                        .routingKey(routingKey)
                )
                    .map { declareOk.queue }
            }
            .log("bind-queue", Level.FINER)

        return bindQueue
            .flatMapMany { queue: String? ->
                receiver.consumeAutoAck(
                    queue!!
                )
            }
            .map { item: Delivery ->
                log.info("Consumed from {}", routingKey)

                objectMapper.readValue(String(item.body), Notification::class.java)
            }
    }
}