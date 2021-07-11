package com.rafamilo.smartdata.domain.searches.entities

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@org.springframework.data.elasticsearch.annotations.Document(indexName = "searches")
@Document(collection = "searches")
class Search(var text: String?) {
    var gender: String? = null
    var birthDate: Date? = null
    var createdDate: Date? = null
}
