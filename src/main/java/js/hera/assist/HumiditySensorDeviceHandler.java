package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

public class HumiditySensorDeviceHandler extends DeviceHandler
{
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

  @Override
  public Map<String, Object> execute(Map<String, Object> parameters)
  {
    return parameters;
  }
}
