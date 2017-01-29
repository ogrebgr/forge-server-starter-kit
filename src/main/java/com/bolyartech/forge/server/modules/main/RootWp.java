package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.handler.WebPage;
import com.bolyartech.forge.server.misc.TemplateEngine;
import com.bolyartech.forge.server.misc.TemplateEngineFactory;
import com.bolyartech.forge.server.route.RequestContext;


public class RootWp extends WebPage {
    public RootWp(TemplateEngineFactory templateEngineFactory) {
        super(templateEngineFactory);
    }


    public RootWp(TemplateEngineFactory templateEngineFactory, boolean enableGzipSupport) {
        super(templateEngineFactory, enableGzipSupport);
    }


    @Override
    public String produceHtml(RequestContext ctx, TemplateEngine tple) {
        tple.assign("from", "Velocity template engine");

        return tple.render("root.vm");
    }
}