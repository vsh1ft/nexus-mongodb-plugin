package lt.boldadmin.nexus.plugin.mongodb.type.entity

import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import org.springframework.data.mongodb.core.mapping.DBRef

class WorkLogProxy(

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