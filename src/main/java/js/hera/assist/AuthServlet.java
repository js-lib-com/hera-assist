package js.hera.assist;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import js.log.Log;
import js.log.LogFactory;
import js.tiny.container.servlet.AppServlet;
import js.tiny.container.servlet.RequestContext;

public class AuthServlet extends AppServlet
{
  private static final long serialVersionUID = 3512268810310930613L;

  private static final Log log = LogFactory.getLog(AuthServlet.class);

  public AuthServlet()
  {
    super();
    log.trace("AuthServlet()");
  }

  @Override
  protected void handleRequest(RequestContext context) throws IOException, ServletException
  {
    log.trace("handleRequest(RequestContext)");
    final HttpServletRequest httpRequest = context.getRequest();
    final HttpServletResponse httpResponse = context.getResponse();

    String redirectURL = String.format("%s%%3Fcode=%s%%26state=%s", parameter(httpRequest, "redirect_uri"), "xxxxxx", httpRequest.getParameter("state"));
    log.debug("Redirect URL: %s", redirectURL);

    String loginURL = httpResponse.encodeRedirectURL("/login?responseurl=" + redirectURL);
    log.debug("Login URL: %s", loginURL);
    sendRedirect(context, loginURL);
  }

  private static String parameter(HttpServletRequest httpRequest, String parameterName) throws IOException
  {
    String parameter = httpRequest.getParameter(parameterName);
    return parameter != null ? URLDecoder.decode(parameter, "UTF8") : "unknown";
  }
}
