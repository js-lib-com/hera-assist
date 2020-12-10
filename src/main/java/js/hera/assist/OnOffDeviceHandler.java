package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

public class OnOffDeviceHandler extends DeviceHandler
{
  public Map<String, Object> query()
  {
    return states(rmi("getState", boolean.class));
  }

  public Map<String, Object> execute(Map<String, Object> parameters)
  {
    boolean parameter = (boolean)parameters.get("on");
    return states(rmi("setState", parameter ? "1.0" : "0.0", boolean.class));
  }

  private static Map<String, Object> states(boolean value)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("on", value);
    return states;
  }
}
