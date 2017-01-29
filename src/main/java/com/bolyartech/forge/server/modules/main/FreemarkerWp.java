package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.handler.WebPage;
import com.bolyartech.forge.server.misc.TemplateEngine;
import com.bolyartech.forge.server.misc.TemplateEngineFactory;
import com.bolyartech.forge.server.route.RequestContext;


public class FreemarkerWp extends WebPage {

    public FreemarkerWp(TemplateEngineFactory templateEngineFactory) {
        super(templateEngineFactory);
    }


    @Override
    public String produceHtml(RequestContext ctx, TemplateEngine tple) {
        tple.assign("from", "Freemarker template engine");

        return tple.render("freemarker.ftl");
    }
}
