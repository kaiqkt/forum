package social.media.service.resources.rabbitmq

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.amqp.RabbitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.rabbitmq.*
import javax.annotation.PreDestroy


@Configuration
class RabbitMqConfiguration {

    @Autowired
    private val rabbitMqConnectionMono: Mono<Connection>? = null

    // the mono for connection, it is cached to re-use the connection across sender and receiver instances
    // this should work properly in most cases
    @Bean
    fun rabbitMqConnection(rabbitProperties: RabbitProperties): Mono<Connection> {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = rabbitProperties.host
        connectionFactory.port = rabbitProperties.port
        connectionFactory.useNio() // <- with this flag our RabbitMq connection will be non-blocking
        connectionFactory.username = rabbitProperties.username
        connectionFactory.password = rabbitProperties.password
        return Mono.fromCallable {
            connectionFactory.newConnection(
                "reactor-rabbit"
            )
        }.cache()
    }

    @Bean
    fun sender(connectionMono: Mono<Connection?>?): Sender {
        return RabbitFlux.createSender(
            SenderOptions()
                .connectionMono(connectionMono)
        )
    }

    @Bean
    fun receiver(connectionMono: Mono<Connection?>?): Receiver {
        return RabbitFlux.createReceiver(
            ReceiverOptions()
                .connectionMono(connectionMono)
        )
    }

    @PreDestroy
    @Throws(Exception::class)
    fun close() {
        rabbitMqConnectionMono!!.block()!!.close()
    }
}