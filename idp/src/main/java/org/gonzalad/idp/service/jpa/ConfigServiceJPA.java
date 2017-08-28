package org.gonzalad.idp.service.jpa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.fediz.service.idp.domain.Idp;
import org.apache.cxf.fediz.service.idp.rest.IdpService;
import org.apache.cxf.fediz.service.idp.service.ConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Fix: adds role with ROLE_ prefix
 */
public class ConfigServiceJPA implements ConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(org.apache.cxf.fediz.service.idp.service.jpa.ConfigServiceJPA.class);

    private static final String ROLE_PREFIX = "ROLE_";

    IdpService idpService;

    @Override
    public Idp getIDP(String realm) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            final Set<GrantedAuthority> authorities = new HashSet<>();

            if (realm == null || realm.length() == 0) {
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + "IDP_LIST"));
                UsernamePasswordAuthenticationToken technicalUser =
                        new UsernamePasswordAuthenticationToken("IDP_TEST", "N.A", authorities);

                SecurityContextHolder.getContext().setAuthentication(technicalUser);

                return idpService.getIdps(0, 1, Arrays.asList("all"), null).getIdps().iterator().next();
            } else {
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + "IDP_READ"));
                UsernamePasswordAuthenticationToken technicalUser =
                        new UsernamePasswordAuthenticationToken("IDP_TEST", "N.A", authorities);

                SecurityContextHolder.getContext().setAuthentication(technicalUser);

                return idpService.getIdp(realm, Arrays.asList("all"));
            }
        } finally {
            SecurityContextHolder.getContext().setAuthentication(currentAuthentication);
            LOG.info("Old Spring security context restored");
        }
    }

    @Override
    public void setIDP(Idp config) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeIDP(String realm) {
        // TODO Auto-generated method stub

    }

    public IdpService getIdpService() {
        return idpService;
    }

    public void setIdpService(IdpService idpService) {
        this.idpService = idpService;
    }


}
