package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;
import spark.Response;

public abstract class BaseHandler {
    protected static final Gson gson = new GsonBuilder().serializeNulls().create();

    // Update the method to accept Spark's request and response
    protected abstract Object handleRequest(Request request, Response response);
}

