package js.hera.assist;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import js.log.Log;
import js.log.LogFactory;
import js.tiny.container.servlet.AppServlet;
import js.tiny.container.servlet.RequestContext;
import js.util.Classes;
import js.util.Strings;

public class LoginServlet extends AppServlet
{
  private static final long serialVersionUID = -3303470739796193442L;
  private static final Log log = LogFactory.getLog(LoginServlet.class);

  public LoginServlet()
  {
    super();
    log.trace("LoginServlet()");
  }

  @Override
  protected void handleRequest(RequestContext context) throws IOException, ServletException
  {
    log.trace("handleRequest(RequestContext)");

    switch(context.getRequest().getMethod()) {
    case "GET":
      handleGET(context);
      break;

    case "POST":
      handlePOST(context);
      break;
    }
  }

  private void handleGET(RequestContext context) throws IOException
  {
    log.trace("handleGET(HttpServletRequest,HttpServletResponse)");
    final HttpServletRequest httpRequest = context.getRequest();
    final HttpServletResponse httpResponse = context.getResponse();

    String redirectURL = httpRequest.getParameter("responseurl");
    log.debug("Redirect URL: %s", redirectURL);

    httpResponse.setStatus(HttpServletResponse.SC_OK);
    httpResponse.setContentType("text/html");

    PrintWriter writer = httpResponse.getWriter();
    writer.print(String.format(Strings.load(Classes.getResourceAsReader("login-form.htm")), redirectURL));
    writer.flush();
  }

  private void handlePOST(RequestContext context) throws IOException
  {
    log.trace("handlePOST(HttpServletRequest,HttpServletResponse)");
    HttpServletRequest httpRequest = context.getRequest();
    String username = httpRequest.getParameter("username").toLowerCase();
    String password = httpRequest.getParameter("password");
    log.debug("User name |%s|, password |%s|", username, password);
    if(!username.equals("iuli") && !username.equals("vasy")) {
      throw new IOException("Invalid user.");
    }

    // HACK: uses 'code' URL parameter to convey information about user, needed by token servlet
    // for that replace 'code' value - that seems not use anyway, with user name
    String redirectURL = URLDecoder.decode(context.getRequest().getParameter("responseurl"), "UTF-8").replace("xxxxxx", username);
    log.debug("Redirect URL: %s", redirectURL);
    sendRedirect(context, redirectURL);
  }
}
