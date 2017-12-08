package uk.gov.dvsa.motr.web.viewmodel;

import com.amazonaws.util.StringUtils;

public class EmailConfirmationPendingViewModel {

    private String email;
    private String emailDisplayString;

    public String getEmail() {

        return email;
    }

    public EmailConfirmationPendingViewModel setEmail(String email) {

        this.email = email;
        this.emailDisplayString = StringUtils.isNullOrEmpty(email) ? "" : email + " ";
        return this;
    }

    public String getEmailDisplayString() {

        return emailDisplayString;
    }
}
