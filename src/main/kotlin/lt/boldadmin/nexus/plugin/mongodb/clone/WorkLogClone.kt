package lt.boldadmin.nexus.plugin.mongodb.clone

import lt.boldadmin.nexus.api.type.entity.*
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "workLog")
class WorkLogClone(

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

    internal fun set(workLog: WorkLog) {
        this.apply {
            project = workLog.project
            collaborator = workLog.collaborator
            timestamp = workLog.timestamp
            workStatus = workLog.workStatus
            intervalId = workLog.intervalId
            description = workLog.description
            id = workLog.id
        }
    }

    internal fun convertToWorkLog() = WorkLog(project, collaborator, timestamp, workStatus, intervalId, description, id)

}