package lt.boldadmin.nexus.plugin.mongodb.type.entity.clone

import lt.boldadmin.nexus.api.type.entity.Collaborator
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "worklog")
class WorklogClone(

    @DBRef(lazy = true)
    internal var project: Project = Project(),

    @DBRef(lazy = true)
    internal var collaborator: Collaborator = Collaborator(),

    internal var timestamp: Long = 0,

    internal var workStatus: WorkStatus = WorkStatus.START,

    internal var intervalId: String = "",

    internal var description: String = "",

    internal var id: String? = null
) {

    internal fun set(worklog: Worklog) {
        this.apply {
            project = worklog.project
            collaborator = worklog.collaborator
            timestamp = worklog.timestamp
            workStatus = worklog.workStatus
            intervalId = worklog.intervalId
            description = worklog.description
            id = worklog.id
        }
    }

    internal fun get() = Worklog(project, collaborator, timestamp, workStatus, intervalId, description, id)

}