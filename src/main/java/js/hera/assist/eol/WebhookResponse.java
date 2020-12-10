package js.hera.assist.eol;

import java.util.HashMap;
import java.util.Map;

public class WebhookResponse
{
  private String fulfillment_text = "";
  private Message fulfillment_messages[];
  private String source = "";
  private Struct payload;
  private Context output_contexts;
  private EventInput followup_event_input;
  private SessionEntityType session_entity_types[];

  public String getFulfillment_text()
  {
    return fulfillment_text;
  }

  public void setFulfillment_text(String fulfillment_text)
  {
    this.fulfillment_text = fulfillment_text;
  }

  public Message[] getFulfillment_messages()
  {
    return fulfillment_messages;
  }

  public void setFulfillment_messages(Message[] fulfillment_messages)
  {
    this.fulfillment_messages = fulfillment_messages;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  public Struct getPayload()
  {
    return payload;
  }

  public void setPayload(Struct payload)
  {
    this.payload = payload;
  }

  public Context getOutput_contexts()
  {
    return output_contexts;
  }

  public void setOutput_contexts(Context output_contexts)
  {
    this.output_contexts = output_contexts;
  }

  public EventInput getFollowup_event_input()
  {
    return followup_event_input;
  }

  public void setFollowup_event_input(EventInput followup_event_input)
  {
    this.followup_event_input = followup_event_input;
  }

  public SessionEntityType[] getSession_entity_types()
  {
    return session_entity_types;
  }

  public void setSession_entity_types(SessionEntityType[] session_entity_types)
  {
    this.session_entity_types = session_entity_types;
  }

  public static class Text
  {
    private String text[];

    public String[] getText()
    {
      return text;
    }

    public void setText(String[] text)
    {
      this.text = text;
    }
  }

  public static class Message
  {
    private Text text;

    public Text getText()
    {
      return text;
    }

    public void setText(Text text)
    {
      this.text = text;
    }
  }

  public static class ListValue
  {
    private Value values[];

    public Value[] getValues()
    {
      return values;
    }

    public void setValues(Value[] values)
    {
      this.values = values;
    }
  }

  public static class Value
  {
    private Object null_value = null;
    private double number_value;
    private String string_value;
    private boolean bool_value;
    private Struct struct_value;
    private ListValue list_value;

    public Object getNull_value()
    {
      return null_value;
    }

    public void setNull_value(Object null_value)
    {
      this.null_value = null_value;
    }

    public double getNumber_value()
    {
      return number_value;
    }

    public void setNumber_value(double number_value)
    {
      this.number_value = number_value;
    }

    public String getString_value()
    {
      return string_value;
    }

    public void setString_value(String string_value)
    {
      this.string_value = string_value;
    }

    public boolean isBool_value()
    {
      return bool_value;
    }

    public void setBool_value(boolean bool_value)
    {
      this.bool_value = bool_value;
    }

    public Struct getStruct_value()
    {
      return struct_value;
    }

    public void setStruct_value(Struct struct_value)
    {
      this.struct_value = struct_value;
    }

    public ListValue getList_value()
    {
      return list_value;
    }

    public void setList_value(ListValue list_value)
    {
      this.list_value = list_value;
    }
  }

  public static class Struct
  {
    private Map<String, Value> fields = new HashMap<>();

    public void put(String name, Value value)
    {
      fields.put(name, value);
    }
  }

  public static class Context
  {

  }

  public static class EventInput
  {

  }

  public static class SessionEntityType
  {

  }
}
