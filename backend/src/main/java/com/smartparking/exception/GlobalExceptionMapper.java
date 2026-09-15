package com.smartparking.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof ApiException api) {
            return json(api.status, api.code, api.getMessage());
        }
        if (exception instanceof ConstraintViolationException cve) {
            String msg = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            return json(400, "VALIDATION", msg);
        }
        if (exception instanceof jakarta.ws.rs.NotAuthorizedException) {
            return json(401, "UNAUTHORIZED", exception.getMessage());
        }
        if (exception instanceof jakarta.ws.rs.ForbiddenException) {
            return json(403, "FORBIDDEN", exception.getMessage());
        }
        LOG.error("Unhandled error", exception);
        return json(500, "INTERNAL", "Unexpected server error");
    }

    private Response json(int status, String code, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("code", code, "message", message == null ? "" : message))
                .build();
    }
}
