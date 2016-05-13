package gressy.entities.actions;

import com.google.inject.Inject;
import gressy.entities.exceptions.NotAllowedException;
import gressy.entities.exceptions.NotLoggedInException;
import gressy.entities.model.User;
import gressy.entities.model.UserDao;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

/**
 * Action for runtime checking of the {@link gressy.entities.actions.Authenticated @Authenticated} annotation.
 *
 * @see gressy.entities.actions.Authenticated
 */
public class AuthenticatedAction extends Action<Authenticated> {

    @Inject
    private UserDao userDao;

    @Override
    public CompletionStage<Result> call(Http.Context ctx) {

        // Check if logged in
         if(ctx.session().get("id") == null) {
             throw new NotLoggedInException();
        }

        long id = Long.parseLong(ctx.session().get("id"));
        User user = userDao.findById(id);
        if (user == null) {
            // Can't find the user. Did it get deleted?
            throw new NotLoggedInException("Please log in again.");
        }

        // Check for roles
        if (configuration.role().length > 0) {
            boolean hasRole = false;
            for (String role : configuration.role()) {
                if (user.getRole().equals(role)) {
                    hasRole = true;
                    break;
                }
            }
            if (!hasRole) {
                throw new NotAllowedException();
            }
        }

        // Ok, clear to go
        ctx.args.put("user", user);
        return delegate.call(ctx);
    }

}
