package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class HumiditySensorDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(HumiditySensorDeviceHandler.class);

  public HumiditySensorDeviceHandler()
  {
    log.trace("HumiditySensorDeviceHandler()");
  }

  @Override
  public Map<String, Object> execute(String command, Map<String, Object> parameters)
  {
    log.warn("Command |%s| not implemented.", command);
    return null;
  }

  @Override
  public Map<String, Object> query()
  {
    long humidity = Math.round(rmi("getHumidity", double.class));
    Map<String, Object> states = new HashMap<>();
    states.put("online", true);
    // states.put("humiditySetpointPercent", humidity);
    states.put("humidityAmbientPercent", humidity);
    return states;
  }
}
