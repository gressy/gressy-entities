package com.github.gressy.entities.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.gressy.entities.exceptions.NotLoggedInException;
import com.github.gressy.entities.model.User;
import com.github.gressy.entities.model.UserDao;
import com.github.gressy.entities.exceptions.GressyException;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Controller for the Panel login and logout.
 */
public class UserController extends Controller {

    @Inject
    protected UserDao userDao;

    @Transactional(readOnly = true)
    public Result login() {
        JsonNode json = request().body().asJson();

        if (json == null) throw new GressyException(400, "Expected a JSON body.");

        String email = json.findPath("email").textValue();
        String pass = json.findPath("password").textValue();

        if (email == null || pass == null) {
            throw new GressyException(400, "Missing required field.");
        }

        User user = userDao.findByEmail(email);
        if (user == null || !user.authenticate(pass)) {
            throw new NotLoggedInException("Invalid username or password.");
        }

        session().clear();
        session("id", Long.toString(user.getId()));

        ObjectNode result = Json.newObject();
        result.set("user", Json.newArray().add(Json.toJson(user)));
        return ok(result);
    }

    public Result logout() {
        session().clear();
        return ok(Json.newObject());
    }

}
