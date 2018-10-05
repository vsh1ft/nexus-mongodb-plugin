package lt.boldadmin.nexus.plugin.mongodb.type.entity

import lt.boldadmin.nexus.api.type.entity.*
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "workLog")
class WorkLogClone(

    @DBRef(lazy = true)
    val project: Project,

    @DBRef(lazy = true)
    val collaborator: Collaborator,

    val timestamp: Long,

    val workStatus: WorkStatus,

    val intervalId: String,

    val description: String = "",

    var id: String? = null
)