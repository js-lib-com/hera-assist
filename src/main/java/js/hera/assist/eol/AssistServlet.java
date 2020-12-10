package js.hera.assist.eol;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import js.hera.assist.eol.WebhookResponse.Message;
import js.hera.assist.eol.WebhookResponse.Struct;
import js.hera.assist.eol.WebhookResponse.Text;
import js.log.Log;
import js.log.LogFactory;
import js.tiny.container.servlet.AppServlet;
import js.tiny.container.servlet.RequestContext;

public class AssistServlet extends AppServlet
{
  private static final long serialVersionUID = 7328424628119630535L;
  private static final Log log = LogFactory.getLog(AssistServlet.class);

  public AssistServlet()
  {
    super();
    log.trace("AssistServlet()");
  }

  @Override
  protected void handleRequest(RequestContext context) throws IOException, ServletException
  {
    log.trace("handleRequest(RequestContext)");
    
    System.out.println("HERA Assistant Request.");

    HttpServletResponse response = context.getResponse();
    PrintWriter writer = response.getWriter();
    // writer.println("Hello Google Assistant!");

    WebhookResponse webhookResponse = new WebhookResponse();
    webhookResponse.setFulfillment_text("This is a test fulfillment text from webhook.");

    Text text = new Text();
    text.setText(new String[]
    {
        "This is a text from webhook."
    });

    Message message = new Message();
    message.setText(text);

    webhookResponse.setFulfillment_messages(new Message[]
    {
        message
    });

    Struct payload = new Struct();

    webhookResponse.setPayload(payload);

    // sendJsonObject(context, webhookResponse, 200);

    String projectId = "trivia-c5db6";
    String sessionId = "";
    String contextName = "";

    String assistantResponse = "{\r\n" + "  \"fulfillmentMessages\": [\r\n" + "    {\r\n" + "      \"text\": {\r\n" + "        \"text\": [\r\n"
        + "          \"Text response from webhook\"\r\n" + "        ]\r\n" + "      }\r\n" + "    }\r\n" + "  ],\r\n" + "  \"outputContexts\": [\r\n"
        + "    {\r\n" + "      \"name\": \"projects/%s/agent/sessions/%s/contexts/%s\",\r\n" + "      \"lifespanCount\": 5,\r\n" + "      \"parameters\": {\r\n"
        + "        \"param-name\": \"param-value\"\r\n" + "      }\r\n" + "    }\r\n" + "  ]\r\n" + "}";

    String responseBody = String.format(assistantResponse, projectId, sessionId, contextName);

    response.setContentType("application/json");
    response.setContentLength(responseBody.length());
    response.setStatus(200);
    writer.print(responseBody);
  }
}
