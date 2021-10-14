package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.response.Response;
import com.bolyartech.forge.server.response.ResponseException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;


public class NotFoundResponse implements Response {
    private static String body = "Not found";

    @Override
    public long toServletResponse(HttpServletResponse resp) throws ResponseException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        PrintWriter pw;
        try {
            pw = resp.getWriter();
            pw.print(body);
            pw.flush();
            pw.close();

            return body.length();
        } catch (IOException e) {
            throw new ResponseException(e);
        }
    }
}
