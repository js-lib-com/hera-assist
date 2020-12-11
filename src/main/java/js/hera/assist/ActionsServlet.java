package js.hera.assist;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import js.log.Log;
import js.log.LogFactory;
import js.tiny.container.servlet.AppServlet;
import js.tiny.container.servlet.RequestContext;

public class ActionsServlet extends AppServlet
{
  private static final long serialVersionUID = 434837850366960190L;
  private static final Log log = LogFactory.getLog(ActionsServlet.class);

  private final ActionsFulfillment actionsFulfillment = new ActionsFulfillment();

  public ActionsServlet()
  {
    super();
    log.trace("ActionsServlet()");
  }

  @Override
  protected void handleRequest(RequestContext context) throws IOException, ServletException
  {
    log.trace("handleRequest(RequestContext)");
    final HttpServletRequest httpRequest = context.getRequest();
    final HttpServletResponse httpResponse = context.getResponse();

    String requestBody = httpRequest.getReader().lines().collect(Collectors.joining());
    log.debug("Request body: %s", requestBody);

    try {
      CompletableFuture<String> responseFuture = actionsFulfillment.handleRequest(requestBody, headers(httpRequest));

      httpResponse.setStatus(HttpServletResponse.SC_OK);
      httpResponse.setHeader("Access-Control-Allow-Origin", "*");
      httpResponse.setContentType("application/json");

      // future response is a string with JSON format, ready to send back on wire
      String responseBody = responseFuture.get();
      log.debug("Response body: %s", responseBody);
      
      PrintWriter writer = httpResponse.getWriter();
      writer.write(responseBody);
      writer.flush();
    }
    catch(ExecutionException | InterruptedException e) {
      log.error("Failed to handle fulfillment request: %s", e);
      throw new ServletException(e);
    }
  }

  private static Map<String, String> headers(HttpServletRequest httpRequest)
  {
    Map<String, String> headerMap = new HashMap<>();
    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while(headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      String val = httpRequest.getHeader(name);
      headerMap.put(name, val);
    }
    return headerMap;
  }
}
