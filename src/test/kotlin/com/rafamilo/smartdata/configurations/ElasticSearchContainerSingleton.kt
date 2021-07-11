package com.rafamilo.smartdata.configurations

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.Request
import org.elasticsearch.client.Response
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.springframework.beans.factory.annotation.Value
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.utility.DockerImageName
import java.util.*
import javax.annotation.PostConstruct


open class ElasticSearchContainerSingleton {

    @Value("\${smartdata.properties.spring.data.elasticsearch.client.reactive.host}")
    private fun setHost(host: String) {
        HOST = host
    }

    @Value("\${smartdata.properties.spring.data.elasticsearch.client.reactive.port}")
    private fun setPort(port: String) {
        PORT = port
    }

    @Value("\${smartdata.properties.spring.data.elasticsearch.client.reactive.username}")
    private fun setUsername(username: String) {
        USERNAME = username
    }

    @Value("\${smartdata.properties.spring.data.elasticsearch.client.reactive.password}")
    private fun setPassword(password: String) {
        PASSWORD = password
    }

    @Value("\${smartdata.properties.spring.data.elasticsearch.image.name}")
    private fun setImageName(imageName: String) {
        IMAGE_NAME = imageName
    }

    @Value("\${smartdata.properties.spring.data.elasticsearch.image.version}")
    private fun setImageVersion(imageVersion: String) {
        IMAGE_VERSION = imageVersion
    }

    var ELASTIC_SEARCH_CONTAINER: ElasticsearchContainer? = null
    private var HOST: String? = null
    private var PORT: String? = null
    private var USERNAME: String? = null
    private var PASSWORD: String? = null
    private var IMAGE_NAME: String? = null
    private var IMAGE_VERSION: String? = null

    @PostConstruct
    fun elasticSearchSingleton() {
        if (ELASTIC_SEARCH_CONTAINER == null) {
            ELASTIC_SEARCH_CONTAINER = ElasticsearchContainer(getElasticSearchImageName)
                    .withPassword(PASSWORD)
                    .withEnv("bootstrap.memory_lock", "true")
                    .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx256m")
            ELASTIC_SEARCH_CONTAINER!!.portBindings = listOf("$PORT:9200", "9300:9300")
            ELASTIC_SEARCH_CONTAINER!!.start()
        }
    }

    private val getElasticSearchImageName: DockerImageName?
        get() = DockerImageName.parse(IMAGE_NAME).withTag(IMAGE_VERSION)
}