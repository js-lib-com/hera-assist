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
    case "action.devices.commands.OnOff":
      boolean parameter = (boolean)parameters.get("on");
      return states(rmi(user, "setState", parameter ? "1.0" : "0.0", State.class));
      
    case "action.devices.commands.BrightnessAbsolute":
      Integer brightness = (Integer)parameters.get("brightness");
      assert brightness <= 100;
      int value = (int)Math.round(255.0 * brightness.doubleValue() / 100.0);
      return states(rmi(user, "setValue", Integer.toString(value), State.class));

    default:
      log.warn("Command |%s| not implemented.", command);
      break;
    }
    return null;
  }

  public Map<String, Object> query(String user)
  {
    return states(rmi(user, "getState", State.class));
  }

  private static Map<String, Object> states(State state)
  {
    Map<String, Object> states = new HashMap<>();
    if(state == null) {
      states.put("online", false);
      return states;
    }

    states.put("online", true);
    states.put("on", state.active);
    assert state.value < 256;
    states.put("brightness", (int)Math.round(100.0 * state.value.doubleValue() / 256.0));
    return states;
  }

  private static class State
  {
    boolean active;
    Integer value;
  }
}
