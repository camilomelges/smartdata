package com.rafamilo.smartdata.domain.searches.entities

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "searches")
class Search {
    var text: String? = null
    var gender: String? = null
    var birthDate: Date? = null
    var createdDate: Date? = null

    constructor(text: String?) {
        this.text = text
    }
}
