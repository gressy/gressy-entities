package gressy.entities.model;

import play.db.jpa.JPA;

import javax.persistence.NoResultException;

/**
 * Extension to EntityDao allowing operations specific to Users.
 */
public class UserDao extends EntityDao<User> {

    /**
     * Finds the user with the given email.
     * @param email The email of the user.
     * @return The user if it exists, or null otherwise.
     */
    public User findByEmail(String email) {
        email = email.toLowerCase();
        try {
            return JPA.em().createNamedQuery("User.findByEmail", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
