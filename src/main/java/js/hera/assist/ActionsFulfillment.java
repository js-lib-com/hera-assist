package js.hera.assist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.actions.api.smarthome.DisconnectRequest;
import com.google.actions.api.smarthome.ExecuteRequest;
import com.google.actions.api.smarthome.ExecuteRequest.Inputs.Payload.Commands;
import com.google.actions.api.smarthome.ExecuteResponse;
import com.google.actions.api.smarthome.QueryRequest;
import com.google.actions.api.smarthome.QueryResponse;
import com.google.actions.api.smarthome.SmartHomeApp;
import com.google.actions.api.smarthome.SyncRequest;
import com.google.actions.api.smarthome.SyncResponse;
import com.google.actions.api.smarthome.SyncResponse.Payload;
import com.google.home.graph.v1.DeviceProto.DeviceInfo;
import com.google.home.graph.v1.DeviceProto.DeviceNames;

import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;

public class ActionsFulfillment extends SmartHomeApp
{
  private static final Log log = LogFactory.getLog(ActionsFulfillment.class);

  private final Map<String, DeviceHandler[]> deviceHandlers = new HashMap<>();

  public ActionsFulfillment()
  {
    super();
    log.trace("ActionsHandler()");

    for(String user : new String[]
    {
        "iuli", "vasy"
    }) {
      JSONArray jsonDevices = loadDevices(user);
      DeviceHandler[] userDeviceHandlers = new DeviceHandler[jsonDevices.length()];

      for(int i = 0; i < jsonDevices.length(); ++i) {
        JSONObject jsonDevice = jsonDevices.getJSONObject(i);
        try {
          userDeviceHandlers[i] = (DeviceHandler)Class.forName(jsonDevice.getString("handlerClass")).newInstance();
          userDeviceHandlers[i].setHostName(jsonDevice.getString("hostName"));
          userDeviceHandlers[i].setDeviceName(jsonDevice.getString("deviceName"));
        }
        catch(InstantiationException | IllegalAccessException | ClassNotFoundException | JSONException e) {
          log.error(e);
        }
      }

      deviceHandlers.put(user, userDeviceHandlers);
    }
  }

  // test
  public DeviceHandler[] getDeviceHandlers(String user)
  {
    return deviceHandlers.get(user);
  }

  @Override
  public SyncResponse onSync(SyncRequest syncRequest, Map<?, ?> headers)
  {
    log.trace("onSync(SyncRequest,Map<?, ?>)");
    String user = getUser(headers);

    SyncResponse syncResponse = new SyncResponse();
    syncResponse.setRequestId(syncRequest.requestId);

    SyncResponse.Payload payload = new SyncResponse.Payload();
    payload.setAgentUserId(user + "1234");
    setDevices(payload, user);

    syncResponse.setPayload(payload);
    return syncResponse;
  }

  public void setDevices(SyncResponse.Payload payload, String user)
  {
    JSONArray jsonDevices = loadDevices(user);
    Payload.Device[] devices = new Payload.Device[jsonDevices.length()];

    for(int i = 0; i < devices.length; ++i) {
      JSONObject jsonDevice = jsonDevices.getJSONObject(i);

      JSONObject jsonName = jsonDevice.getJSONObject("name");
      DeviceNames deviceName = DeviceNames.newBuilder() //
          .addAllDefaultNames(strings(jsonName.getJSONArray("defaultNames"))) //
          .setName(jsonName.getString("name")) //
          .addAllNicknames(strings(jsonName.getJSONArray("nicknames"))) //
          .build();

      JSONObject jsonInfo = jsonDevice.getJSONObject("info");
      DeviceInfo deviceInfo = DeviceInfo.newBuilder() //
          .setManufacturer(jsonInfo.getString("manufacturer")) //
          .setModel(jsonInfo.getString("model")) //
          .setHwVersion(jsonInfo.getString("hwVersion")) //
          .setSwVersion(jsonInfo.getString("swVersion")) //
          .build();

      Payload.Device.Builder deviceBuilder = new Payload.Device.Builder() //
          .setId(Integer.toString(jsonDevice.getInt("id"))) //
          .setType(jsonDevice.getString("type")) //
          .setTraits(strings(jsonDevice.getJSONArray("traits"))) //
          .setName(deviceName) //
          .setWillReportState(jsonDevice.getBoolean("reportState")) //
          .setRoomHint(jsonDevice.getString("roomHint")) //
          .setDeviceInfo(deviceInfo) //
          .setAttributes(jsonDevice.getJSONObject("attributes"));

      devices[i] = deviceBuilder.build();
    }

    payload.setDevices(devices);
  }

  private static JSONArray loadDevices(String user)
  {
    try {
      return new JSONArray(Strings.load(ActionsFulfillment.class.getResourceAsStream("/devices/" + user + ".json")));
    }
    catch(JSONException | IOException e) {
      log.error(e);
      return new JSONArray();
    }
  }

  private static List<String> strings(JSONArray jsonArray)
  {
    List<String> strings = new ArrayList<>(jsonArray.length());
    for(int i = 0; i < jsonArray.length(); ++i) {
      strings.add(jsonArray.getString(i));
    }
    return strings;
  }

  @Override
  public QueryResponse onQuery(QueryRequest queryRequest, Map<?, ?> headers)
  {
    log.trace("onQuery(SyncRequest,Map<?, ?>)");
    String user = getUser(headers);

    QueryRequest.Inputs.Payload.Device[] devices = ((QueryRequest.Inputs)queryRequest.getInputs()[0]).payload.devices;
    QueryResponse res = new QueryResponse();
    res.setRequestId(queryRequest.requestId);
    res.setPayload(new QueryResponse.Payload());

    Map<String, Map<String, Object>> deviceStates = new HashMap<>();
    for(QueryRequest.Inputs.Payload.Device device : devices) {
      try {
        Map<String, Object> deviceState = new HashMap<>();
        deviceState.put("status", "SUCCESS");
        DeviceHandler deviceHandler = deviceHandlers.get(user)[Integer.parseInt(device.getId())];
        deviceState.putAll(deviceHandler.query(user));
        deviceStates.put(device.getId(), deviceState);
      }
      catch(Exception e) {
        log.error("QUERY FAILED: %s", e);
        Map<String, Object> failedDevice = new HashMap<>();
        failedDevice.put("status", "ERROR");
        failedDevice.put("errorCode", "deviceOffline");
        deviceStates.put(device.getId(), failedDevice);
      }
    }
    res.payload.setDevices(deviceStates);
    return res;
  }

  @Override
  public ExecuteResponse onExecute(ExecuteRequest executeRequest, Map<?, ?> headers)
  {
    log.trace("onExecute(SyncRequest,Map<?, ?>)");
    String user = getUser(headers);

    List<ExecuteResponse.Payload.Commands> commandsResponse = new ArrayList<>();
    List<String> successfulDevices = new ArrayList<>();
    Map<String, Object> states = new HashMap<>();

    Commands[] commands = ((ExecuteRequest.Inputs)executeRequest.getInputs()[0]).getPayload().getCommands();
    for(Commands command : commands) {
      for(Commands.Devices device : command.getDevices()) {
        DeviceHandler deviceHandler = deviceHandlers.get(user)[Integer.parseInt(device.getId())];
        Commands.Execution execution = command.getExecution()[0];

        log.debug("Execute command |%s| with parameter |%s| on device |%s|.", execution.getCommand(), execution.getParams(), device.getId());
        Map<String, Object> resultStates = deviceHandler.execute(user, execution.getCommand(), execution.getParams());

        // after successful execution add device id
        if(resultStates != null) {
          states.putAll(resultStates);
          successfulDevices.add(device.getId());
        }
      }
    }

    ExecuteResponse.Payload.Commands successfulCommands = new ExecuteResponse.Payload.Commands();
    successfulCommands.status = "SUCCESS";
    successfulCommands.setStates(states);
    successfulCommands.ids = successfulDevices.toArray(new String[] {});
    commandsResponse.add(successfulCommands);

    ExecuteResponse executeResponse = new ExecuteResponse();
    executeResponse.setRequestId(executeRequest.getRequestId());
    ExecuteResponse.Payload payload = new ExecuteResponse.Payload(commandsResponse.toArray(new ExecuteResponse.Payload.Commands[] {}));
    executeResponse.setPayload(payload);

    return executeResponse;
  }

  @Override
  public void onDisconnect(DisconnectRequest request, Map<?, ?> headers)
  {
    log.trace("onDisconnect(SyncRequest,Map<?, ?>)");
  }

  private static String getUser(Map<?, ?> headers)
  {
    String token = (String)headers.get("authorization");
    // Bearer iuli123access
    String username = token.substring(7, 11);
    log.debug("User Name: %s", username);
    return username;
  }
}
