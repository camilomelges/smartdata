package com.rafamilo.smartdata.configurations

import com.beust.klaxon.Klaxon
import com.rafamilo.smartdata.domain.searches.entities.Search
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.ResourceUtils
import reactor.kotlin.core.publisher.toFlux
import java.nio.file.Files


@ExtendWith(SpringExtension::class)
@SpringBootTest
class CreateStartMongoData : MongoDBContainerSingleton() {

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun startMongoData() {
        val searchesMap: List<Map<String, Any>>? = Klaxon().parseArray(String(Files.readAllBytes(ResourceUtils.getFile("classpath:searches.json").toPath())))
//        searchesMap?.forEach(this::insertByQuantity)
        val searches: MutableList<Search> = mutableListOf()
        searchesMap?.forEach { it -> searches.addAll(getList(it)) }

        reactiveMongoTemplate.insertAll(searches.toFlux().collectList()).blockLast()
        reactiveMongoTemplate.findAll(Search::class.java)
        reactiveMongoTemplate.findAll(Search::class.java).toStream().forEach {
            it.text
        }
    }

    private fun insertByQuantity(searchMap: Map<String, Any>) {
        val quantity: Int = searchMap["quantity"] as Int

        for (i in 0.rangeTo(quantity / 100)) insertSearch(buildEntity(searchMap))
    }

    private fun getList(searchMap: Map<String, Any>): MutableList<Search> {
        val quantity: Int = searchMap["quantity"] as Int
        val searchs: MutableList<Search> = mutableListOf()

        for (i in 0.rangeTo(quantity / 100)) searchs.add(buildEntity(searchMap))

        return searchs
    }

    private fun insertSearch(search: Search) {
        reactiveMongoTemplate.save(search).block()
    }

    private fun buildEntity(searchMap: Map<String, Any>): Search {
        return Search(searchMap["text"] as String?)
    }
}
