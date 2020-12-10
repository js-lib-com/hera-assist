package js.hera.assist;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;

public abstract class Device
{
  private static final Log log = LogFactory.getLog(Device.class);


  public abstract Map<String, Object> query();

  public abstract Map<String, Object> execute(Map<String, Object> parameters);

  private int id;
  private String type;
  private List<String> traits;
  private Name name;
  private Info info;
  private String roomHint;
  private boolean reportState;

  private String hostName;
  private String deviceName;

  public int getId()
  {
    return id;
  }

  public String getType()
  {
    return type;
  }

  public List<String> getTraits()
  {
    return traits;
  }

  public Name getName()
  {
    return name;
  }

  public Info getInfo()
  {
    return info;
  }

  public String getRoomHint()
  {
    return roomHint;
  }

  public boolean isReportState()
  {
    return reportState;
  }

  protected String rmi(String command, String... parameter)
  {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(String.format("http://%s.local/js/hera/dev/HostSystem/invoke.rmi", hostName));
      log.debug("URL: %s", url);

      String parameters;
      if(parameter.length == 1) {
        parameters = String.format("[\"%s\",\"%s\",\"%s\"]", deviceName, command, parameter[0]);
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
        return "0.0";
      }

      return Strings.load(connection.getInputStream());
    }
    catch(Throwable t) {
      log.error(t);
    }
    finally {
      if(connection != null) {
        connection.disconnect();
      }
    }
    return "0.0";
  }

  public static class Name
  {
    private String name;
    private List<String> defaultNames;
    private List<String> nicknames;

    public String getName()
    {
      return name;
    }

    public List<String> getDefaultNames()
    {
      return defaultNames;
    }

    public List<String> getNicknames()
    {
      return nicknames;
    }
  }

  public static class Info
  {
    private String manufacturer;
    private String model;
    private String hwVersion;
    private String swVersion;

    public String getManufacturer()
    {
      return manufacturer;
    }

    public String getModel()
    {
      return model;
    }

    public String getHwVersion()
    {
      return hwVersion;
    }

    public String getSwVersion()
    {
      return swVersion;
    }
  }
}
