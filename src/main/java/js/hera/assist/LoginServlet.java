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

  private static final String FORM = "" + //
      "<html>" + //
      "  <body>" + //
      "    <form action='/login' method='post'>" + //
      "      <input type='hidden' name='responseurl' value='%s'/>" + //
      "      <button type='submit' style='font-size:14pt'>Link this service to Google</button>" + //
      "    </form>" + //
      "  </body>" + //
      "</html>";

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
    writer.print(String.format(FORM, redirectURL));
    writer.flush();
  }

  private void handlePOST(RequestContext context) throws IOException
  {
    log.trace("handlePOST(HttpServletRequest,HttpServletResponse)");
    // Here, you should validate the user account.
    // In this sample, we do not do that.
    String redirectURL = URLDecoder.decode(context.getRequest().getParameter("responseurl"), "UTF-8");
    log.debug("Redirect URL: %s", redirectURL);
    sendRedirect(context, redirectURL);
  }
}
