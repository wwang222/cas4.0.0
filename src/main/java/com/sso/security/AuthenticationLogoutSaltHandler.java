package com.sso.security;

import org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl;
import org.jasig.cas.logout.LogoutRequest;
import org.jasig.cas.logout.LogoutRequestStatus;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.flow.AbstractLogoutAction;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class AuthenticationLogoutSaltHandler extends AbstractLogoutAction {

    @NotNull
    private ServicesManager servicesManager;
    private boolean followServiceRedirects;
    private final TicketRegistry ticketRegistry;
    @NotNull
    private final CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    public AuthenticationLogoutSaltHandler(final TicketRegistry ticketRegistry,
                                           final CookieRetrievingCookieGenerator tgtCookieGenerator) {
        this.ticketRegistry = ticketRegistry;
        this.ticketGrantingTicketCookieGenerator = tgtCookieGenerator;
    }

    @Override
    protected Event doInternalExecute(HttpServletRequest request, HttpServletResponse response, RequestContext context) throws Exception {
        boolean needFrontSlo = false;
        putLogoutIndex(context, 0);
        final List<LogoutRequest> logoutRequests = WebUtils.getLogoutRequests(context);
        if (logoutRequests != null) {
            for (LogoutRequest logoutRequest : logoutRequests) {
                // if some logout request must still be attempted
                if (logoutRequest.getStatus() == LogoutRequestStatus.NOT_ATTEMPTED) {
                    needFrontSlo = true;
                    break;
                }
            }
        }

        final String service = request.getParameter("service");
        if (this.followServiceRedirects && service != null) {
            final RegisteredService rService = this.servicesManager.findServiceBy(new SimpleWebApplicationServiceImpl(service));

            if (rService != null && rService.isEnabled()) {
                context.getFlowScope().put("logoutRedirectUrl", service);
            }
        }

        // there are some front services to logout, perform front SLO
        if (needFrontSlo) {
            return new Event(this, FRONT_EVENT);
        } else {
            // otherwise, finish the logout process
            return new Event(this, FINISH_EVENT);
        }
    }

    public void setFollowServiceRedirects(final boolean followServiceRedirects) {
        this.followServiceRedirects = followServiceRedirects;
    }

    public void setServicesManager(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }
}
