package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.handler.StaticFileHandler;
import com.bolyartech.forge.server.misc.MimeTypeResolver;
import com.bolyartech.forge.server.misc.MimeTypeResolverImpl;
import com.bolyartech.forge.server.misc.TemplateEngineFactory;
import com.bolyartech.forge.server.module.HttpModule;
import com.bolyartech.forge.server.route.Route;
import com.bolyartech.forge.server.route.RouteImpl;
import com.bolyartech.forge.server.tple.freemarker.FreemarkerTemplateEngineFactory;
import com.bolyartech.forge.server.tple.handlebars.HandlebarsTemplateEngineFactory;
import com.bolyartech.forge.server.tple.jade.JadeTemplateEngineFactory;
import com.bolyartech.forge.server.tple.jetbrick.JetbrickTemplateEngineFactory;
import com.bolyartech.forge.server.tple.jtwig.JtwigTemplateEngineFactory;
import com.bolyartech.forge.server.tple.mustache.MustacheTemplateEngineFactory;
import com.bolyartech.forge.server.tple.pebble.PebbleTemplateEngineFactory;
import com.bolyartech.forge.server.tple.thymeleaf.ThymeleafTemplateEngineFactory;
import com.bolyartech.forge.server.tple.velocity.VelocityTemplateEngineFactory;

import java.util.ArrayList;
import java.util.List;


public final class MainModule implements HttpModule {
    private static final String MODULE_SYSTEM_NAME = "main";
    private static final int MODULE_VERSION_CODE = 1;
    private static final String MODULE_VERSION_NAME = "1.0.0";

    private final TemplateEngineFactory mVelocityTpleFactory;


    public MainModule() {
        mVelocityTpleFactory = new VelocityTemplateEngineFactory("templates/modules/main/");
    }


    public List<Route> createRoutes() {
        List<Route> ret = new ArrayList<>();

        NotFoundResponse notFoundResponse = new NotFoundResponse();
        MimeTypeResolver mimeTypeResolver = new MimeTypeResolverImpl();

        RootWp rootWp = new RootWp(mVelocityTpleFactory, true);
        ret.add(new RouteImpl(HttpMethod.GET, "/", rootWp));
        ret.add(new RouteImpl(HttpMethod.POST, "/", rootWp));
        ret.add(new RouteImpl(HttpMethod.GET, "/css", new StaticFileHandler("/static/css", notFoundResponse,
                mimeTypeResolver, true)));

        TemplateEngineFactory fmf = new FreemarkerTemplateEngineFactory("/templates/modules/main/");
        ret.add(new RouteImpl(HttpMethod.GET, "/freemarker", new FreemarkerWp(fmf)));

        TemplateEngineFactory mf = new MustacheTemplateEngineFactory("templates/modules/main/");
        ret.add(new RouteImpl(HttpMethod.GET, "/mustache", new MustacheWp(mf)));

        TemplateEngineFactory hbf = new HandlebarsTemplateEngineFactory("/templates/modules/main/");
        ret.add(new RouteImpl(HttpMethod.GET, "/handlebars", new HandlebarsWp(hbf)));

        TemplateEngineFactory jf = new JadeTemplateEngineFactory("templates/modules/main/");
        ret.add(new RouteImpl(HttpMethod.GET, "/jade", new JadeWp(jf)));

        TemplateEngineFactory tlf = new ThymeleafTemplateEngineFactory("templates/modules/main/");
        ret.add(new RouteImpl(HttpMethod.GET, "/thymeleaf", new ThymeleafWp(tlf)));

        TemplateEngineFactory jbf = new JetbrickTemplateEngineFactory("templates/modules/main/");
        ret.add(new RouteImpl(HttpMethod.GET, "/jetbrick", new JetbrickWp(jbf)));

        TemplateEngineFactory pf = new PebbleTemplateEngineFactory("templates/modules/main/");
        ret.add(new RouteImpl(HttpMethod.GET, "/pebble", new PebbleWp(pf)));

        TemplateEngineFactory jtf = new JtwigTemplateEngineFactory("templates/modules/main/");
        ret.add(new RouteImpl(HttpMethod.GET, "/jtwig", new JtwigWp(jtf)));

        return ret;
    }


    @Override
    public String getSystemName() {
        return MODULE_SYSTEM_NAME;
    }


    @Override
    public String getShortDescription() {
        return "";
    }


    @Override
    public int getVersionCode() {
        return MODULE_VERSION_CODE;
    }


    @Override
    public String getVersionName() {
        return MODULE_VERSION_NAME;
    }
}


