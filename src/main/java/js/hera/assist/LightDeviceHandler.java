package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class LightDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(LightDeviceHandler.class);

  public LightDeviceHandler()
  {
    log.trace("OnOffDeviceHandler()");
  }

  public Map<String, Object> execute(String user, String command, Map<String, Object> parameters)
  {
    switch(command) {
    case "action.devices.commands.OnOff":
      boolean parameter = (boolean)parameters.get("on");
      return states(rmi(user, "setState", parameter ? "1.0" : "0.0", boolean.class));

    default:
      log.warn("Command |%s| not implemented.", command);
      break;
    }
    return null;
  }

  public Map<String, Object> query(String user)
  {
    return states(rmi(user, "getState", boolean.class));
  }

  private static Map<String, Object> states(boolean value)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("on", value);
    return states;
  }
}
