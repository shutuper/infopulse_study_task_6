package validation;

import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;


public class AuthorizedPerson {

    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String password;
    @Alive(30)
    private LocalDateTime authorizedFor;

    public AuthorizedPerson(String firstName, String lastName, String email, String password, LocalDateTime authorizedFor) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.authorizedFor = authorizedFor;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getAuthorizedFor() {
        return authorizedFor;
    }

    public void setAuthorizedFor(LocalDateTime authorizedFor) {
        this.authorizedFor = authorizedFor;
    }
}
