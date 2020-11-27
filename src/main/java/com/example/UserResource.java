package com.example;

import com.example.model.User;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Path("/users")
@Produces("application/json")
@Consumes("application/json")
public class UserResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(User.class.getName());

    @Inject
    UserService userService;

    @GET
    public List<User> get() {
        return userService.getUsers();
    }

    @POST
    @Transactional
    public Response create(User u) {
        if (u.getId() != null) {
            throw new WebApplicationException("Id was invalidly sent on request", 422);
        }
        var errorMessages = userService.validateUser(u);
        if (errorMessages != null) {
            throw new WebApplicationException(errorMessages, 400);
        }
        userService.create(u);
        return Response.ok(u).status(201).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @SuppressWarnings("CdiInjectionPointsInspection")
        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
//            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code)
                .entity(exceptionJson)
                .build();
        }

    }

}
