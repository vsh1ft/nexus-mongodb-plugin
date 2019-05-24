package lt.boldadmin.nexus.plugin.mongodb.type.aggregation

import org.springframework.data.mongodb.core.mapping.Field

class WorklogInterval(

    @Field
    var intervalId: String = ""
)
