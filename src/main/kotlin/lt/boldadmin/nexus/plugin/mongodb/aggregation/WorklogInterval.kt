package lt.boldadmin.nexus.plugin.mongodb.aggregation

import org.springframework.data.mongodb.core.mapping.Field

class WorklogInterval(

    @Field
    var intervalId: String = ""
)
