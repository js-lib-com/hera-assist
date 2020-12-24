package js.hera.assist.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.actions.api.smarthome.SyncResponse;

import js.hera.assist.ActionsFulfillment;
import js.hera.assist.DeviceHandler;
import js.hera.assist.HumiditySensorDeviceHandler;
import js.hera.assist.OnOffDeviceHandler;
import js.hera.assist.ThermostatDeviceHandler;

@Ignore
public class ActionsFulfillmentTest
{
  private ActionsFulfillment actions;

  @Before
  public void beforeTest()
  {
    actions = new ActionsFulfillment();
  }

  @Test
  public void deviceHandlers()
  {
    DeviceHandler[] handlers = actions.getDeviceHandlers("iuli");
    assertThat(handlers, notNullValue());
    assertThat(handlers.length, equalTo(4));

    assertThat(handlers[0], notNullValue());
    assertThat(handlers[0], instanceOf(OnOffDeviceHandler.class));
    assertThat(handlers[0].getDeviceName(), equalTo("actuator-4"));

    assertThat(handlers[1], notNullValue());
    assertThat(handlers[1], instanceOf(OnOffDeviceHandler.class));
    assertThat(handlers[1].getDeviceName(), equalTo("actuator-6"));

    assertThat(handlers[2], notNullValue());
    assertThat(handlers[2], instanceOf(HumiditySensorDeviceHandler.class));
    assertThat(handlers[2].getDeviceName(), equalTo("dht-sensor"));

    assertThat(handlers[3], notNullValue());
    assertThat(handlers[3], instanceOf(ThermostatDeviceHandler.class));
    assertThat(handlers[3].getDeviceName(), equalTo("thermostat"));
  }

  @Test
  public void setDevices() throws IOException
  {
    SyncResponse.Payload payload = new SyncResponse.Payload();
    payload.setAgentUserId("1234");

    actions.setDevices(payload, "iuli");

    System.out.println(payload.build());
  }
}
