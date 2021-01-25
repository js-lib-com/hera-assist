package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class DimmerDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(DimmerDeviceHandler.class);

  public DimmerDeviceHandler()
  {
    log.trace("DimmerDeviceHandler()");
  }

  public Map<String, Object> execute(String user, String command, Map<String, Object> parameters)
  {
    switch(command) {
    case "action.devices.commands.BrightnessAbsolute":
      Integer brightness = (Integer)parameters.get("brightness");
      assert brightness <= 100;
      Long value = Math.round(255.0 * brightness.doubleValue() / 100.0);
      return states(rmi(user, "updateValue", value.toString(), byte.class));

    default:
      log.warn("Command |%s| not implemented.", command);
      break;
    }
    return null;
  }

  public Map<String, Object> query(String user)
  {
    return states(rmi(user, "getValue", byte.class));
  }

  private static Map<String, Object> states(byte value)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("brightness", value);
    return states;
  }
}
