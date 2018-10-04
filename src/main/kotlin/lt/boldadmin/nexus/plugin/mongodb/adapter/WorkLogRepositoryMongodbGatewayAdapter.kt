package lt.boldadmin.nexus.plugin.mongodb.adapter

import lt.boldadmin.nexus.api.gateway.WorkLogRepositoryGateway
import lt.boldadmin.nexus.api.type.entity.WorkLog
import lt.boldadmin.nexus.api.type.valueobject.WorkStatus
import lt.boldadmin.nexus.plugin.mongodb.repository.WorkLogRepository
import java.util.*

class WorkLogRepositoryMongodbGatewayAdapter(private val workLogRepository: WorkLogRepository):
    WorkLogRepositoryGateway {

    override fun findById(id: String): Optional<WorkLog> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(workLog: WorkLog) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsByIntervalId(intervalId: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByCollaboratorId(collaboratorId: String): Collection<WorkLog> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByIntervalId(intervalId: String): Collection<WorkLog> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByIntervalIdAndWorkStatusNotOrderByTimestampAsc(
        intervalId: String, workStatus: WorkStatus
    ): Collection<WorkLog> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByProjectId(projectId: String): Collection<WorkLog> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findFirstByCollaboratorIdAndWorkStatusNotOrderByTimestampDesc(
        collaboratorId: String, workStatus: WorkStatus
    ): WorkLog? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findFirstByIntervalId(intervalId: String): WorkLog {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.    }
    }

    override fun findFirstByIntervalIdAndWorkStatusOrderByTimestampDesc(
        intervalId: String, workStatus: WorkStatus
    ): WorkLog? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}