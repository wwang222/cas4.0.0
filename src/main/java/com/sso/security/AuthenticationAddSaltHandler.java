package com.sso.security;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.ticket.ServiceTicketImpl;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.registry.DefaultTicketRegistry;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.authentication.dao.SaltSource;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Map;


@SuppressWarnings("deprecation")
public class AuthenticationAddSaltHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    private SaltSource saltSource;

    @NotNull
    private DefaultTicketRegistry ticketRegistry;

    @NotNull
    private String sql;

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential) throws  GeneralSecurityException, PreventedException{
        final String username = credential.getUsername();
        /***
         * 通过username去找缓存判断该用户是否已经登陆过
         * 登陆过找到该用户对应的TGT
         * 调用logout进行注销操作
         */
        if(CacheUtil.users.contains(username)){
            final Collection<Ticket> ticketsInCache = this.ticketRegistry.getTickets();
            for (final Ticket ticket : ticketsInCache) {
                TicketGrantingTicket t = null;
                try {
                    t = (TicketGrantingTicketImpl)ticket;
                }catch (Exception e){
                    t = ((ServiceTicketImpl)ticket).getGrantingTicket();
                }
                if(t.getAuthentication().getPrincipal().getId().equals(username) && t.getId()!=null){
                    /***
                     * 注销方法二
                     */
                    t.markTicketExpired();
                    ticketRegistry.deleteTicket(t.getId());
                    CacheUtil.users.remove(username);
                }
            }
        }
        Object salt = null;
        if (getSaltSource() != null) {
            User userDetails =  new User();
            userDetails.setUsername(username);
            salt = getSaltSource().getSalt(userDetails);
        }
        String saltedPass = mergePasswordAndSalt(credential.getPassword(), salt, false);
        final String encryptedPassword = this.getPasswordEncoder().encode(saltedPass);

        try {
            final Map<String,Object> result = getJdbcTemplate().queryForMap(sql, username);
            if(result ==null){
                throw new AccountNotFoundException(username + " not found in backing map.");
            }
            Object dbPassword = result.get("password");
            Object isActive = result.get("is_active");

//            if (!encryptedPassword.equals(dbPassword) || new  Integer(0).equals(isActive)) {
//                throw new FailedLoginException();
//            }
        } catch (final IncorrectResultSizeDataAccessException e) {
            throw new AccountNotFoundException(username + " not found in backing map.");
        } catch (final DataAccessException e) {
            e.printStackTrace();
            throw new AccountNotFoundException(username + " not found in backing map.");
        }
        /***
         * 向缓存输入当前登录用户名
         */
        CacheUtil.users.add(username);
        HandlerResult handlerResult = createHandlerResult(credential, new SimplePrincipal(username), null);
        return handlerResult;
    }

    protected String mergePasswordAndSalt(String password, Object salt, boolean strict) {
        if (password == null) {
            password = "";
        }

        if (strict && (salt != null)) {
            if ((salt.toString().lastIndexOf("{") != -1) || (salt.toString().lastIndexOf("}") != -1)) {
                throw new IllegalArgumentException("Cannot use { or } in salt.toString()");
            }
        }

        if ((salt == null) || "".equals(salt)) {
            return password;
        } else {
            return password + "{" + salt.toString() + "}";
        }
    }

    public SaltSource getSaltSource() {
        return saltSource;
    }

    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }
    /**
     * @param sql The sql to set.
     */
    public void setSql(final String sql) {
        this.sql = sql;
    }

    public void setTicketRegistry(DefaultTicketRegistry ticketRegistry){
        this.ticketRegistry = ticketRegistry;
    }

}
