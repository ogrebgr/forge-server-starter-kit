package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.handler.WebPage;
import com.bolyartech.forge.server.misc.TemplateEngine;
import com.bolyartech.forge.server.misc.TemplateEngineFactory;
import com.bolyartech.forge.server.route.RequestContext;


public class HandlebarsWp extends WebPage {
    public HandlebarsWp(TemplateEngineFactory templateEngineFactory) {
        super(templateEngineFactory);
    }


    @Override
    public String produceHtml(RequestContext ctx, TemplateEngine tple) {
        tple.assign("from", "Handlebars template engine");

        return tple.render("handlebars.hbs");
    }
}
