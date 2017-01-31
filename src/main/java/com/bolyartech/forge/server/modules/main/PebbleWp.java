package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.handler.WebPage;
import com.bolyartech.forge.server.misc.TemplateEngine;
import com.bolyartech.forge.server.misc.TemplateEngineFactory;
import com.bolyartech.forge.server.route.RequestContext;


public class PebbleWp extends WebPage {

    public PebbleWp(TemplateEngineFactory templateEngineFactory) {
        super(templateEngineFactory);
    }


    @Override
    public String produceHtml(RequestContext ctx, TemplateEngine tple) {
        tple.assign("from", "Pebble template engine");

        return tple.render("pebble.pebble");
    }
}
