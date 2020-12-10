package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

public class ThermostatDeviceHandler extends DeviceHandler
{
  @Override
  public Map<String, Object> query()
  {
    return states(rmi("getState", State.class));
  }

  @Override
  public Map<String, Object> execute(Map<String, Object> parameters)
  {
    Number setpoint = (Number)parameters.get("thermostatTemperatureSetpoint");
    return states(rmi("updateSetpoint", setpoint.toString(), State.class));
  }

  private static Map<String, Object> states(State state)
  {
    Map<String, Object> states = new HashMap<>();
    states.put("online", true);
    states.put("thermostatMode", "heat");
    states.put("thermostatTemperatureSetpoint", state.setpoint);
    states.put("thermostatTemperatureAmbient", state.temperature);
    return states;
  }

  private static class State
  {
    double setpoint;
    double temperature;
  }
}
