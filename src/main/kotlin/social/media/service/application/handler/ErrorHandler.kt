package social.media.service.application.handler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import java.time.LocalDateTime
import java.util.stream.Collectors

@ControllerAdvice
internal class ErrorHandler {
    private val log: Logger = LoggerFactory.getLogger(ErrorHandler::class.java)

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleRequestBodyError(ex: WebExchangeBindException): ResponseEntity<Any> {
        log.error("Exception caught in handleRequestBodyError :  {} ", ex.message, ex)
        val body: MutableMap<String, Any> = LinkedHashMap()

        val error = ex.bindingResult.allErrors.stream()
            .map { obj: ObjectError -> obj.defaultMessage }
            .sorted()
            .collect(Collectors.joining(","))
        log.error("errorList : {}", error)

        body["timestamp"] = LocalDateTime.now()
        body["message"] = error

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }
}