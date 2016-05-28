package com.github.gressy.entities.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.gressy.entities.exceptions.InvalidValueException;
import com.github.gressy.entities.util.serializers.UserPasswordSerializer;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Base User class, intended to be subclassed by each user type in the system.
 *
 * Using this superclass allows a generic user controller for login and logout,
 * as well as providing a pre-configured system for authentication and role keeping.
 */
@NamedQueries({
        @NamedQuery(
                name = "User.findByEmail",
                query = "select x from User x where x.email = :email"
        )
})
@javax.persistence.Entity
@Inheritance
@DiscriminatorColumn(name="role")
public abstract class User implements com.github.gressy.entities.model.Entity {

    @Id
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    private long id;

    @NotNull
    private String email;

    @JsonSerialize(using = UserPasswordSerializer.class)
    private String password;

    public User() {}
    public User(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) throw new InvalidValueException("Email cannot be null.");
        Constraints.EmailValidator emailValidator = new Constraints.EmailValidator();
        if (!emailValidator.isValid(email)) throw new InvalidValueException("Email is not valid.");
        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && password.length() > 0) this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean authenticate(String password) {
        return password != null && BCrypt.checkpw(password, this.password);
    }

    @JsonProperty("role")
    public abstract String getRole();


}
