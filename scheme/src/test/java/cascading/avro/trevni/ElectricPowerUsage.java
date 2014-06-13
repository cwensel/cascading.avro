/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package cascading.avro.trevni;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class ElectricPowerUsage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ElectricPowerUsage\",\"namespace\":\"cascading.avro.trevni\",\"fields\":[{\"name\":\"addressCode\",\"type\":\"string\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"devicePowerEventList\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"DevicePowerEvent\",\"fields\":[{\"name\":\"power\",\"type\":\"double\"},{\"name\":\"deviceType\",\"type\":\"int\"},{\"name\":\"deviceId\",\"type\":\"int\"},{\"name\":\"status\",\"type\":\"int\"}]}}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.CharSequence addressCode;
  @Deprecated public long timestamp;
  @Deprecated public java.util.List<cascading.avro.trevni.DevicePowerEvent> devicePowerEventList;

  /**
   * Default constructor.
   */
  public ElectricPowerUsage() {}

  /**
   * All-args constructor.
   */
  public ElectricPowerUsage(java.lang.CharSequence addressCode, java.lang.Long timestamp, java.util.List<cascading.avro.trevni.DevicePowerEvent> devicePowerEventList) {
    this.addressCode = addressCode;
    this.timestamp = timestamp;
    this.devicePowerEventList = devicePowerEventList;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return addressCode;
    case 1: return timestamp;
    case 2: return devicePowerEventList;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: addressCode = (java.lang.CharSequence)value$; break;
    case 1: timestamp = (java.lang.Long)value$; break;
    case 2: devicePowerEventList = (java.util.List<cascading.avro.trevni.DevicePowerEvent>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'addressCode' field.
   */
  public java.lang.CharSequence getAddressCode() {
    return addressCode;
  }

  /**
   * Sets the value of the 'addressCode' field.
   * @param value the value to set.
   */
  public void setAddressCode(java.lang.CharSequence value) {
    this.addressCode = value;
  }

  /**
   * Gets the value of the 'timestamp' field.
   */
  public java.lang.Long getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the value of the 'timestamp' field.
   * @param value the value to set.
   */
  public void setTimestamp(java.lang.Long value) {
    this.timestamp = value;
  }

  /**
   * Gets the value of the 'devicePowerEventList' field.
   */
  public java.util.List<cascading.avro.trevni.DevicePowerEvent> getDevicePowerEventList() {
    return devicePowerEventList;
  }

  /**
   * Sets the value of the 'devicePowerEventList' field.
   * @param value the value to set.
   */
  public void setDevicePowerEventList(java.util.List<cascading.avro.trevni.DevicePowerEvent> value) {
    this.devicePowerEventList = value;
  }

  /** Creates a new ElectricPowerUsage RecordBuilder */
  public static cascading.avro.trevni.ElectricPowerUsage.Builder newBuilder() {
    return new cascading.avro.trevni.ElectricPowerUsage.Builder();
  }
  
  /** Creates a new ElectricPowerUsage RecordBuilder by copying an existing Builder */
  public static cascading.avro.trevni.ElectricPowerUsage.Builder newBuilder(cascading.avro.trevni.ElectricPowerUsage.Builder other) {
    return new cascading.avro.trevni.ElectricPowerUsage.Builder(other);
  }
  
  /** Creates a new ElectricPowerUsage RecordBuilder by copying an existing ElectricPowerUsage instance */
  public static cascading.avro.trevni.ElectricPowerUsage.Builder newBuilder(cascading.avro.trevni.ElectricPowerUsage other) {
    return new cascading.avro.trevni.ElectricPowerUsage.Builder(other);
  }
  
  /**
   * RecordBuilder for ElectricPowerUsage instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ElectricPowerUsage>
    implements org.apache.avro.data.RecordBuilder<ElectricPowerUsage> {

    private java.lang.CharSequence addressCode;
    private long timestamp;
    private java.util.List<cascading.avro.trevni.DevicePowerEvent> devicePowerEventList;

    /** Creates a new Builder */
    private Builder() {
      super(cascading.avro.trevni.ElectricPowerUsage.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(cascading.avro.trevni.ElectricPowerUsage.Builder other) {
      super(other);
    }
    
    /** Creates a Builder by copying an existing ElectricPowerUsage instance */
    private Builder(cascading.avro.trevni.ElectricPowerUsage other) {
            super(cascading.avro.trevni.ElectricPowerUsage.SCHEMA$);
      if (isValidValue(fields()[0], other.addressCode)) {
        this.addressCode = data().deepCopy(fields()[0].schema(), other.addressCode);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[1].schema(), other.timestamp);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.devicePowerEventList)) {
        this.devicePowerEventList = data().deepCopy(fields()[2].schema(), other.devicePowerEventList);
        fieldSetFlags()[2] = true;
      }
    }

    /** Gets the value of the 'addressCode' field */
    public java.lang.CharSequence getAddressCode() {
      return addressCode;
    }
    
    /** Sets the value of the 'addressCode' field */
    public cascading.avro.trevni.ElectricPowerUsage.Builder setAddressCode(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.addressCode = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'addressCode' field has been set */
    public boolean hasAddressCode() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'addressCode' field */
    public cascading.avro.trevni.ElectricPowerUsage.Builder clearAddressCode() {
      addressCode = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'timestamp' field */
    public java.lang.Long getTimestamp() {
      return timestamp;
    }
    
    /** Sets the value of the 'timestamp' field */
    public cascading.avro.trevni.ElectricPowerUsage.Builder setTimestamp(long value) {
      validate(fields()[1], value);
      this.timestamp = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'timestamp' field has been set */
    public boolean hasTimestamp() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'timestamp' field */
    public cascading.avro.trevni.ElectricPowerUsage.Builder clearTimestamp() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'devicePowerEventList' field */
    public java.util.List<cascading.avro.trevni.DevicePowerEvent> getDevicePowerEventList() {
      return devicePowerEventList;
    }
    
    /** Sets the value of the 'devicePowerEventList' field */
    public cascading.avro.trevni.ElectricPowerUsage.Builder setDevicePowerEventList(java.util.List<cascading.avro.trevni.DevicePowerEvent> value) {
      validate(fields()[2], value);
      this.devicePowerEventList = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'devicePowerEventList' field has been set */
    public boolean hasDevicePowerEventList() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'devicePowerEventList' field */
    public cascading.avro.trevni.ElectricPowerUsage.Builder clearDevicePowerEventList() {
      devicePowerEventList = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    public ElectricPowerUsage build() {
      try {
        ElectricPowerUsage record = new ElectricPowerUsage();
        record.addressCode = fieldSetFlags()[0] ? this.addressCode : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.timestamp = fieldSetFlags()[1] ? this.timestamp : (java.lang.Long) defaultValue(fields()[1]);
        record.devicePowerEventList = fieldSetFlags()[2] ? this.devicePowerEventList : (java.util.List<cascading.avro.trevni.DevicePowerEvent>) defaultValue(fields()[2]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
