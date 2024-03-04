package ru.upg.ates.rest.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import ru.upg.ates.tasks.TasksDomain
import ru.upg.ates.tasks.model.Task
import ru.upg.ates.tasks.command.CreateTaskCommand
import ru.upg.ates.common.ddd.execute


class CreateTaskHandler(
    private val mapper: ObjectMapper,
    private val domain: TasksDomain
): HttpHandler {

    data class RequestPayload(
        val name: String
    )

    data class ResponsePayload(
        val task: Task
    )

    override fun invoke(request: Request): Response {
        val requestContent = request.bodyString()
        val payload = mapper.readValue<RequestPayload>(requestContent)

        val aggregate = CreateTaskCommand.Aggregate(payload.name)
        val command = CreateTaskCommand(aggregate)
        val result = domain.execute(command)

        val responsePayload = ResponsePayload(result)
        val responseContent = mapper.writeValueAsString(responsePayload)
        return Response(Status.OK).body(responseContent)
    }
}