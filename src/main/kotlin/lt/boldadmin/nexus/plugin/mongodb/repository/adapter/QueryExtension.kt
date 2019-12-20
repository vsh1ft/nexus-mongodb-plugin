package lt.boldadmin.nexus.plugin.mongodb.repository.adapter

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

internal fun Query.addCriteria(keyValues: Map<String, String>) =
    this.apply { keyValues.forEach { this.addCriteria(Criteria.where(it.key).`is`(it.value)) } }
