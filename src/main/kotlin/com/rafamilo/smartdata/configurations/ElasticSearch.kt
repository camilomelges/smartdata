package com.rafamilo.smartdata.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.web.reactive.function.client.ExchangeStrategies
import java.net.UnknownHostException
import kotlin.jvm.Throws


@Configuration
class ElasticSearch : AbstractReactiveElasticsearchConfiguration() {

    @Value("\${spring.data.elasticsearch.client.reactive.host}")
    private lateinit var host: String

    @Value("\${spring.data.elasticsearch.client.reactive.port}")
    private lateinit var port: String

    @Value("\${spring.data.elasticsearch.client.reactive.username}")
    private lateinit var username: String

    @Value("\${spring.data.elasticsearch.client.reactive.password}")
    private lateinit var password: String

    @Value("\${spring.data.elasticsearch.client.reactive.max-in-memory-size}")
    private lateinit var maxMemorySize: String

    @Bean
    override fun reactiveElasticsearchClient(): ReactiveElasticsearchClient {
        val clientConfiguration = ClientConfiguration.builder()
                .connectedTo(mountUrl())
                .withBasicAuth(username, password)
                .withWebClientConfigurer {
                    val exchangeStrategies = ExchangeStrategies.builder().codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize.toInt()) }.build()
                    return@withWebClientConfigurer it.mutate().exchangeStrategies(exchangeStrategies).build()
                }
                .build()

        return ReactiveRestClients.create(clientConfiguration)
    }

    @Bean
    @Throws(UnknownHostException::class)
    fun elasticsearchRestTemplate(): ReactiveElasticsearchTemplate? {
        return ReactiveElasticsearchTemplate(reactiveElasticsearchClient())
    }

    private fun mountUrl(): String? {
        return "$host:$port"
    }
}