package com.bolyartech.forge.server.skeleton.modules.main;


import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.ModelAndViewEndpoint;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;


public class RootEp extends ModelAndViewEndpoint {
    public RootEp(RootHandler h) {
        super(HttpMethod.GET, "/", h);
    }


    public static class RootHandler implements Handler<ModelAndView> {
        public RootHandler() {
        }


        @Override
        public ModelAndView handle(Request request, Response response) {
            Map<String, Object> model = new HashMap<>();

            return new ModelAndView(model, "templates/root.vm");
        }
    }
}
