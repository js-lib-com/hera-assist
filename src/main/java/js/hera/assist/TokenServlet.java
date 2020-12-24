package js.hera.assist;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import js.log.Log;
import js.log.LogFactory;
import js.tiny.container.servlet.AppServlet;
import js.tiny.container.servlet.RequestContext;
import js.util.Strings;

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
    HttpServletRequest httpRequest = context.getRequest();
    log.debug("URL: %s", URL(httpRequest));

    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while(headerNames.hasMoreElements()) {
      StringBuilder header = new StringBuilder();
      String headerName = headerNames.nextElement();
      header.append(headerName);
      header.append(": ");

      Enumeration<String> headers = httpRequest.getHeaders(headerName);
      while(headers.hasMoreElements()) {
        String headerValue = headers.nextElement();
        header.append(headerValue);
        header.append(", ");
      }

      log.debug(header);
    }

    String body = Strings.load(httpRequest.getInputStream());
    log.debug(body);
    int startIndex = body.indexOf("&code=") + 6;
    int endIndex = body.indexOf("&", startIndex);
    String username = body.substring(startIndex, endIndex);
    log.debug("User Name: %s", username);

    Token token = new Token();
    token.setToken_type("bearer");
    token.setExpires_in(DAY_SECONDS);
    token.setAccess_token(username + "123access");
    if("authorization_code".equals(httpRequest.getParameter("grant_type"))) {
      token.setRefresh_token(username + "123refresh");
    }
    log.debug("Generate token: %s", token);

    sendJsonObject(context, token, 200);
  }

  private static String URL(HttpServletRequest request)
  {
    StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
    String queryString = request.getQueryString();

    if(queryString == null) {
      return requestURL.toString();
    }
    else {
      return requestURL.append('?').append(queryString).toString();
    }
  }
}
