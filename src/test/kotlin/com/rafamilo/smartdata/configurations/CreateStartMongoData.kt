package com.rafamilo.smartdata.configurations

import com.beust.klaxon.Klaxon
import com.rafamilo.smartdata.domain.searches.entities.Search
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.ResourceUtils
import org.testcontainers.shaded.com.google.common.collect.ImmutableList
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.nio.file.Files

@ExtendWith(SpringExtension::class)
@SpringBootTest
class CreateStartMongoData : MongoDBContainerSingleton() {

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun shouldBeInsert1254SearchesInDB() {
        Klaxon()
                .parseArray<Map<String, Any>>(getPathFromFile())
                ?.stream()
                ?.map(::getList)
                ?.map(::toFlux)
                ?.map(::collectList)
                ?.map(::toSearchsList)
                ?.forEachOrdered { insertAllMongo(it).blockLast() }

        Assertions.assertEquals(1254, getSearchsCountMongo().block())
    }

    private fun toSearchsList(list: Mono<out MutableList<out Any>>): Mono<out MutableList<out Search>> {
        return list as Mono<out MutableList<out Search>>
    }

    private fun toFlux(immutableList: ImmutableList<*>): Flux<*> {
        return immutableList.toFlux()
    }

    private fun collectList(flux: Flux<*>): Mono<out MutableList<out Any>> {
        return flux.collectList()
    }

    private fun getSearchsCountMongo(): Mono<Long> {
        return reactiveMongoTemplate.count(Query(), "searches")
    }

    private fun insertAllMongo(searches: Mono<out MutableList<out Search>>): Flux<Search> {
        return reactiveMongoTemplate.insertAll(searches)
    }

    private fun getPathFromFile(): String {
        return String(Files.readAllBytes(ResourceUtils.getFile("classpath:searches.json").toPath()))
    }

    private fun getList(searchMap: Map<String, Any>): ImmutableList<Search> {
        val quantity: Int = searchMap["quantity"] as Int
        val searches: MutableList<Search> = mutableListOf()

        for (i in 0.rangeTo(quantity / 100000)) searches.add(buildEntity(searchMap))

        return ImmutableList.copyOf(searches)
    }

    private fun buildEntity(searchMap: Map<String, Any>): Search {
        return Search(searchMap["text"] as String?)
    }
}
