package com.bolyartech.forge.server.skeleton.misc;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HandlerException;
import spark.Request;
import spark.Response;

public class FssHandler implements Handler<String> {
    @Override
    public String handle(Request request, Response response) throws HandlerException {

        request.scheme().toLowerCase();

    }
}