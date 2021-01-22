package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class RollerBlindsDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(RollerBlindsDeviceHandler.class);

  public RollerBlindsDeviceHandler()
  {
    log.trace("RollerBlindsDeviceHandler()");
  }

  @Override
  public Map<String, Object> execute(String user, String command, Map<String, Object> parameters)
  {
    switch(command) {
    case "action.devices.commands.OpenClose":
      double percent = ((int)parameters.get("openPercent") / 100.0);
      return states(rmi(user, "open", Double.toString(percent), double.class));
      
    default:
      log.warn("Command |%s| not implemented.", command);
      break;
    }
    return null;
  }

  @Override
  public Map<String, Object> query(String user)
  {
    return states(rmi(user, "getState", double.class));
  }

  private static Map<String, Object> states(double percent)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("openPercent", 100 * percent);
    return states;
  }
}
