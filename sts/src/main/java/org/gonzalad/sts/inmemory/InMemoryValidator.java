package org.gonzalad.sts.inmemory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.common.security.SimpleGroup;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.principal.WSUsernameTokenPrincipalImpl;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSConfig;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.message.token.UsernameToken;
import org.apache.wss4j.dom.validate.Credential;
import org.apache.wss4j.dom.validate.Validator;

/**
 * TODO: finish it
 */
public class InMemoryValidator implements Validator {

    private static final Logger LOG = Logger.getLogger(InMemoryValidator.class.getName());

    static {
        WSSConfig.init();
    }

    private String address;

    private Map<String, String> users = new HashMap<>();

    public Credential validate(Credential credential, RequestData data) throws WSSecurityException {
        if (credential == null || credential.getUsernametoken() == null) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "noCredential");
        }

        // Validate the UsernameToken
        UsernameToken usernameToken = credential.getUsernametoken();
        String pwType = usernameToken.getPasswordType();
        LOG.fine("UsernameToken user " + usernameToken.getName());
        LOG.fine("UsernameToken password type " + pwType);

        if (!WSConstants.PASSWORD_TEXT.equals(pwType)) {
            LOG.fine("Authentication failed - digest passwords are not accepted");
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }
        if (usernameToken.getPassword() == null) {
            LOG.fine("Authentication failed - no password was provided");
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }

        try {
            List<String> roles = authenticate(usernameToken);
            Subject subject = new Subject();

            WSUsernameTokenPrincipalImpl principal = new WSUsernameTokenPrincipalImpl(usernameToken.getName(), false);
            principal.setPassword(usernameToken.getPassword());
            principal.setNonce(Base64.decodeBase64(usernameToken.getNonce()));
            credential.setPrincipal(principal);

            subject.getPrincipals().add(principal);
            for (String roleName : roles) {
                subject.getPrincipals().add(new SimpleGroup(roleName, usernameToken.getName()));
            }
            subject.setReadOnly();
            credential.setSubject(subject);

        } catch (RuntimeException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }

        return credential;
    }

    List<String> authenticate(UsernameToken usernameToken) {
        String password = users.get(usernameToken.getName());
        if (password == null || !password.equals(usernameToken.getPassword())) {
            throw new RuntimeException("No such user " + usernameToken.getName());
        }
        return Collections.emptyList();
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }
}