package org.xml_to_db.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import org.xml_to_db.utils.GlobalExceptionHandler;

import java.util.Optional;
import java.util.function.BiFunction;

public abstract class BaseFunction {
    protected HttpResponseMessage execute(
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context,
            BiFunction<HttpRequestMessage<Optional<String>>, ExecutionContext, HttpResponseMessage> function) {
        try {
            return function.apply(request, context);
        } catch (Exception e) {
            return GlobalExceptionHandler.handleException(request, e);
        }
    }
}
