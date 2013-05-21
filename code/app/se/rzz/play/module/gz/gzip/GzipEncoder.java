package se.rzz.play.module.gz.gzip;

import play.core.j.JavaResultExtractor;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.io.*;
import java.util.zip.GZIPOutputStream;


public class GzipEncoder extends Action<Gzip> {




    @Override
    public Result call(Http.Context ctx) throws Throwable {



        String encoding = ctx.request().getHeader("Accept-Encoding");

        if(encoding == null || !encoding.contains("se/rzz/play/module/gz/gzip")){

            return delegate.call(ctx);
        }

        Result result = delegate.call(ctx);

        return encode(result, ctx);


    }


    public final static Result encode(Result result, Http.Context ctx){
        final int statusCode = JavaResultExtractor.getStatus(result);
        byte[] body = JavaResultExtractor.getBody(result);

        try {
            body = gzip(body);

        } catch (IOException e) {
            return result;
        }

        if(!ctx.response().getHeaders().containsKey("Content-Type")){
            ctx.response().setContentType("text/html;charset=UTF-8");
        }

        ctx.response().setHeader("Content-Encoding", "se/rzz/play/module/gz/gzip");
        ctx.response().setHeader("Content-Length", Integer.toString(body.length));

        return status(statusCode, body);
    }


    private final static byte[] gzip(byte[] input)
            throws IOException {

        final InputStream inputStream = new ByteArrayInputStream(input);

        final ByteArrayOutputStream compressedOutputStream = new ByteArrayOutputStream((int) (input.length * 0.75));

        final OutputStream gzipOutputStream = new GZIPOutputStream(compressedOutputStream);

        final byte[] buf = new byte[5000];
        int len;

        while ((len = inputStream.read(buf)) > 0) {
            gzipOutputStream.write(buf, 0, len);
        }

        inputStream.close();
        gzipOutputStream.close();

        return compressedOutputStream.toByteArray();
    }

}

