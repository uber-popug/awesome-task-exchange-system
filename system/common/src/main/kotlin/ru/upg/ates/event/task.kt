package ru.upg.ates.event

import ru.upg.ates.AtesEvent
import ru.upg.ates.BusinessEvent
import ru.upg.ates.CUDEvent
import java.util.UUID

data class TaskChange(
    val pid: UUID,
    val userPid: UUID,
    val name: String,
    val price: Int,
    val finished: Boolean
)

interface TaskCUD : AtesEvent, CUDEvent {
    data class Created(val task: TaskChange) : TaskCUD
    data class Updated(val task: TaskChange) : TaskCUD
}

interface TaskBE : AtesEvent, BusinessEvent {
    data class Assigned(val taskPid: UUID, val userPid: UUID) : TaskBE
    data class Finished(val taskPid: UUID) : TaskBE
}
