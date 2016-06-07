package com.bolyartech.forge.server.modules.main;


import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;


public class RootEp extends StringEndpoint {
    public RootEp(RootHandler handler) {
        super(HttpMethod.GET, "", handler);
    }


    public static class RootHandler implements Handler<String> {
        private final VelocityTemplateEngine mTple;

        public RootHandler(VelocityTemplateEngine tple) {
            mTple = tple;
        }


        @Override
        public String handle(Request request, Response response) {
            Map<String, Object> model = new HashMap<>();

            return mTple.render(new ModelAndView(model, "templates/root.vm"));
        }
    }
}
