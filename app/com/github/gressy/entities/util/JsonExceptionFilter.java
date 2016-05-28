package com.github.gressy.entities.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;
import akka.stream.Materializer;
import com.github.gressy.entities.exceptions.GressyException;
import play.libs.Json;
import play.mvc.*;

public class JsonExceptionFilter extends Filter {

    @Inject
    public JsonExceptionFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        CompletableFuture<Result> future = new CompletableFuture<>();
        nextFilter.apply(requestHeader).whenComplete((result, ex) -> {
            // Exception is a CompletionException, but we're interested in the cause.
            if (ex == null) {
                future.complete(result);
            } else {
                Throwable cause = ex.getCause();
                if (cause != null && GressyException.class.isAssignableFrom(cause.getClass())) {
                    GressyException ge = (GressyException) cause;
                    future.complete(Results
                            .status(ge.getStatusCode())
                            .sendJson(Json.newObject().put("error", ge.getMessage())));
                } else {
                    future.completeExceptionally(ex);
                }
            }
        });
        return future;
    }
}
