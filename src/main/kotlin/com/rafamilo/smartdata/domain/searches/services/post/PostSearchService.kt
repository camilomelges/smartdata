package com.rafamilo.smartdata.domain.searches.services.post

//import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.ReactiveMongoTemplate


class PostSearchService : IPostSearchService {
    private val reactiveMongoTemplate: ReactiveMongoTemplate? = null

//    fun run(apiReturn: List<MutableMap<String?, Any>>): Any {
//        val iTunesData = HashMap()
//        iTunesData.setData(data)
//        reactiveMongoTemplate!!.save<Any>(iTunesData)
//    }
}
