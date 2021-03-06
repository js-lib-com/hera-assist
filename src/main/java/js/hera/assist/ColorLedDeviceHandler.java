package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class ColorLedDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(ColorLedDeviceHandler.class);

  public ColorLedDeviceHandler()
  {
    log.trace("ColorLedDeviceHandler()");
  }

  public Map<String, Object> execute(String user, String command, Map<String, Object> parameters)
  {
    switch(command) {
    case "action.devices.commands.ColorAbsolute":
      @SuppressWarnings("unchecked")
      Map<String, Object> colorParameters = (Map<String, Object>)parameters.get("color");
      String colorName = (String)colorParameters.get("name");
      log.debug("Set color to %s", colorName);
      int colorRGB = (int)colorParameters.get("spectrumRGB");
      return states(rmi(user, "setColor", Integer.toString(colorRGB), int.class));

    case "action.devices.commands.BrightnessAbsolute":
      double brightness = (int)parameters.get("brightness") / 100.0;
      return states(rmi(user, "setBrightness", Double.toString(brightness), double.class));

    case "action.devices.commands.OnOff":
      boolean state = (boolean)parameters.get("on");
      return states(rmi(user, "setState", state ? "1.0" : "0.0", boolean.class));

    default:
      log.warn("Command |%s| not implemented.", command);
      break;
    }
    return null;
  }

  public Map<String, Object> query(String user)
  {
    return states(rmi(user, "getColor", int.class));
  }

  private static Map<String, Object> states(int colorRGB)
  {
    Map<String, Object> color = new HashMap<>();
    color.put("spectrumRgb", colorRGB);

    Map<String, Object> states = new HashMap<>();
    states.put("color", color);
    return states;
  }

  private static Map<String, Object> states(double value)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("brightness", Math.round(100 * value));
    return states;
  }

  private static Map<String, Object> states(boolean value)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("on", value);
    return states;
  }
}
