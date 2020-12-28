package js.hera.assist;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
  private static final int TOKEN_EXPIRES = 24 * 3600;

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

    // int startIndex = body.indexOf("&code=") + 6;
    // int endIndex = body.indexOf("&", startIndex);
    // String username = body.substring(startIndex, endIndex);

    Map<String, String> tokenRequest = new HashMap<>();
    for(String param : body.split("&")) {
      String[] parts = param.split("=");
      tokenRequest.put(parts[0], URLDecoder.decode(parts[1], StandardCharsets.UTF_8.toString()));
    }

    String redirectURI = tokenRequest.get("redirect_uri");
    log.debug("redirectURI: %s", redirectURI);
    String client_id = tokenRequest.get("client_id");
    log.debug("client_id: %s", client_id);
    String client_secret = tokenRequest.get("client_secret");
    log.debug("client_secret: %s", client_secret);
    // TODO: validate client and sendUnauthorized(context) on fail

    String grantType = tokenRequest.get("grant_type");
    log.debug("grant_type: %s", grantType);

    Token token = null;
    switch(grantType) {
    case "authorization_code":
      String code = tokenRequest.get("code");
      log.debug("code: %s", code);

      token = new Token();
      token.setCode(code);
      token.setToken_type("bearer");
      token.setExpires_in(TOKEN_EXPIRES);
      token.setAccess_token(code + "123access");
      token.setRefresh_token(UUID.randomUUID().toString());

      TOKENS.put(token.getRefresh_token(), token);
      break;

    case "refresh_token":
      String refreshToken = tokenRequest.get("refresh_token");
      token = TOKENS.get(refreshToken);
      if(token == null) {
        sendUnauthorized(context);
      }
      token.setAccess_token(token.getCode() + "123access");
      break;
    }

    log.debug("Send token: %s", token);
    sendJsonObject(context, token, 200);
  }

  private static Map<String, Token> TOKENS = new HashMap<String, Token>();

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
