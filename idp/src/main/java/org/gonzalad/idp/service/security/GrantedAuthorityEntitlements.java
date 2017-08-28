package org.gonzalad.idp.service.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.cxf.fediz.service.idp.domain.Entitlement;
import org.apache.cxf.fediz.service.idp.domain.Role;
import org.apache.cxf.fediz.service.idp.service.RoleDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Patch for Spring Security 4: needs a real AnonymousAuthenticationToken
 * instead of UsernamePasswordAuthenticationToken otherwise
 * authenticationTrustResolver.isAnonymous returns false
 */
public class GrantedAuthorityEntitlements extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(org.apache.cxf.fediz.service.idp.service.security.GrantedAuthorityEntitlements.class);

    @Autowired
    private RoleDAO roleDAO;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth == null) {
                chain.doFilter(request, response);
                return;
            }

            final Set<GrantedAuthority> authorities = new HashSet<>();
            if (currentAuth.getAuthorities() != null) {
                authorities.addAll(currentAuth.getAuthorities());
            }

            Iterator<? extends GrantedAuthority> authIt = currentAuth.getAuthorities().iterator();
            while (authIt.hasNext()) {
                GrantedAuthority ga = authIt.next();
                String roleName = ga.getAuthority();

                try {
                    Role role = roleDAO.getRole(roleName.substring(5), Arrays.asList("all"));
                    for (Entitlement e : role.getEntitlements()) {
                        authorities.add(new SimpleGrantedAuthority(e.getName()));
                    }
                } catch (Exception ex) {
                    LOG.error("Role '{}' not found", roleName);
                }
            }
            LOG.debug("Granted Authorities: {}", authorities);

            AbstractAuthenticationToken enrichedAuthentication;
            if (authorities.size() == 1 && "ROLE_ANONYMOUS".equals(authorities.iterator().next().getAuthority())) {
                enrichedAuthentication = new AnonymousAuthenticationToken("anonymousUser",
                        "anonymousUser", authorities);
            } else {
                enrichedAuthentication = new UsernamePasswordAuthenticationToken(
                        currentAuth.getName(), currentAuth.getCredentials(), authorities);
            }
            enrichedAuthentication.setDetails(currentAuth.getDetails());

            SecurityContextHolder.getContext().setAuthentication(enrichedAuthentication);
            LOG.info("Enriched AuthenticationToken added");

        } catch (Exception ex) {
            LOG.error("Failed to enrich security context with entitlements", ex);
        }

        chain.doFilter(request, response);
    }

}
