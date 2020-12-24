package js.hera.assist;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import js.json.Json;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;
import js.util.Strings;
import js.util.Types;

public abstract class DeviceHandler
{
  private static final Log log = LogFactory.getLog(Device.class);

  public abstract Map<String, Object> execute(String user, String command, Map<String, Object> parameters);

  public abstract Map<String, Object> query(String user);

  private static Map<String, String> HERA_HOSTS = new HashMap<>();
  static {
    HERA_HOSTS.put("iuli", "iulianrotaru.go.ro:1988");
    HERA_HOSTS.put("vasy", "vasyhome.asuscomm.com:8080");
  }

  private final Json json;

  private String hostName;
  private String deviceName;

  public DeviceHandler()
  {
    json = Classes.loadService(Json.class);
  }

  public void setHostName(String hostName)
  {
    this.hostName = hostName;
  }

  public String getHostName()
  {
    return hostName;
  }

  public void setDeviceName(String deviceName)
  {
    this.deviceName = deviceName;
  }

  public String getDeviceName()
  {
    return deviceName;
  }

  protected <T> T rmi(String user, String command, Class<T> returnType)
  {
    return rmi(user, command, null, returnType);
  }

  @SuppressWarnings("unchecked")
  protected <T> T rmi(String user, String command, String parameter, Class<T> returnType)
  {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(String.format("http://%s/hera/rest/invoke", HERA_HOSTS.get(user)));
      log.debug("URL: %s", url);

      String parameters;
      if(parameter != null) {
        parameters = String.format("[\"%s\",\"%s\",\"%s\"]", deviceName, command, parameter);
      }
      else {
        parameters = String.format("[\"%s\",\"%s\"]", deviceName, command);
      }
      log.debug("Parameters: %s", parameters);

      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoInput(true);
      connection.setConnectTimeout(8000);
      connection.setReadTimeout(8000);

      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Content-Length", Integer.toString(parameters.length()));

      PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"), true);
      writer.append(parameters).flush();

      int responseCode = connection.getResponseCode();
      log.debug("Response code: %d", responseCode);
      if(responseCode < 200 || responseCode >= 300) {
        return (T)Types.getEmptyValue(returnType);
      }

      String value = Strings.load(connection.getInputStream());
      log.debug("Value: %s", value);
      if(value == null || value.equals("null")) {
        return (T)Types.getEmptyValue(returnType);
      }
      return json.parse(value, returnType);
    }
    catch(Throwable t) {
      log.error(t);
    }
    finally {
      if(connection != null) {
        connection.disconnect();
      }
    }
    return (T)Types.getEmptyValue(returnType);
  }
}
