package controllers;

import actions.Gzip;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {



    @Gzip
    public static Result index() {

        return ok(index.render("Your new application is ready."));
    }
  
}
