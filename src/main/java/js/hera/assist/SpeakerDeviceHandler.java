package js.hera.assist;

import java.util.HashMap;
import java.util.Map;

import js.log.Log;
import js.log.LogFactory;

public class SpeakerDeviceHandler extends DeviceHandler
{
  private static final Log log = LogFactory.getLog(SpeakerDeviceHandler.class);

  public SpeakerDeviceHandler()
  {
    log.trace("SpeakerDeviceHandler()");
  }

  @Override
  public Map<String, Object> execute(String command, Map<String, Object> parameters)
  {
    log.trace("execute(String,Map<String, Object>)");
    return query();
  }

  @Override
  public Map<String, Object> query()
  {
    log.trace("query()");
    Map<String, Object> states = new HashMap<>();
    states.put("on", true);
    states.put("currentVolume", 5);
    states.put("isMuted", false);
    states.put("activityState", "ACTIVE");
    states.put("playbackState", "PLAYING");
    return states;
  }
}
