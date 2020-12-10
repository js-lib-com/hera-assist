package js.hera.assist;

import java.io.IOException;

import javax.servlet.ServletException;

import js.log.Log;
import js.log.LogFactory;
import js.tiny.container.servlet.AppServlet;
import js.tiny.container.servlet.RequestContext;

public class TokenServlet extends AppServlet
{
  private static final long serialVersionUID = 7759138297836782884L;
  private static final Log log = LogFactory.getLog(TokenServlet.class);
  private static final int DAY_SECONDS = 24 * 3600;

  public TokenServlet()
  {
    super();
    log.trace("TokenServlet()");
  }

  @Override
  protected void handleRequest(RequestContext context) throws IOException, ServletException
  {
    log.trace("handleRequest(RequestContext)");

    Token token = new Token();
    token.setToken_type("bearer");
    token.setExpires_in(DAY_SECONDS);
    token.setAccess_token("123access");
    if("authorization_code".equals(context.getRequest().getParameter("grant_type"))) {
      token.setRefresh_token("123refresh");
    }
    log.debug("Generate token: %s", token);

    sendJsonObject(context, token, 200);
  }
}
