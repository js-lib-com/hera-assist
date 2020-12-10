package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

public class SensorDeviceHandler extends DeviceHandler
{
  @Override
  public Map<String, Object> query()
  {
    Value value = rmi("getValue", Value.class);

    Map<String, Object> states = new HashMap<>();
    states.put("online", true);
    // states.put("humiditySetpointPercent", humidity);
    states.put("humidityAmbientPercent", Math.round(value.humidity));
    // states.put("temperatureSetpointCelsius", temperature);
    states.put("temperatureAmbientCelsius", value.temperature);
    return states;
  }

  @Override
  public Map<String, Object> execute(Map<String, Object> parameters)
  {
    return parameters;
  }

  private static class Value
  {
    double humidity;
    double temperature;
  }
}
