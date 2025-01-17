/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.avro;

import cascading.avro.serialization.AvroSpecificRecordSerialization;
import cascading.flow.FlowProcess;
import cascading.scheme.Scheme;
import cascading.scheme.SinkCall;
import cascading.scheme.SourceCall;
import cascading.tap.CompositeTap;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.mapred.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class AvroScheme extends Scheme<Configuration, RecordReader, OutputCollector, AvroScheme.SourceContext, AvroScheme.SinkContext> {

    private static final String DEFAULT_RECORD_NAME = "CascadingAvroRecord";
    private static final PathFilter filter = path -> !path.getName().startsWith("_");
    protected Schema schema;

    protected static class SourceContext {

        public AvroWrapper<IndexedRecord> key;
        public Writable value;

        public SourceContext(AvroWrapper<IndexedRecord> key, Writable value) {
            this.key = key;
            this.value = value;
        }
    }

    protected static class SinkContext {

    }

    /**
     * Constructor to read from an Avro source or write to an Avro sink without specifying the schema. If using as a sink,
     * the sink Fields must have type information and currently Map and List are not supported.
     */
    public AvroScheme() {
    }

    /**
     * Create a new Cascading 2.0 scheme suitable for reading and writing data using the Avro serialization format.
     * This is the legacy constructor format. A Fields object and the corresponding types must be provided.
     *
     * @param fields Fields object from cascading
     * @param types  array of Class types
     */
    public AvroScheme(Fields fields, Class<?>[] types) {
        this(CascadingToAvro.generateAvroSchemaFromFieldsAndTypes(DEFAULT_RECORD_NAME, fields, types));
    }

    /**
     * Create a new Cascading 2.0 scheme suitable for reading and writing data using the Avro serialization format.
     * Note that if schema is null, the Avro schema will be inferred from one of the source files (if this scheme
     * is being used as a source). At the moment, we are unable to infer a schema for a sink (this will change soon with
     * a new version of cascading though).
     *
     * @param schema Avro schema, or null if this is to be inferred from source file. Note that a runtime exception will happen
     *               if the AvroScheme is used as a sink and no schema is supplied.
     */
    public AvroScheme(Schema schema) {
        setSchema(schema);

        if (getSchema() != null) {
            setFieldsFrom(getSchema());
        }
    }

    /**
     * Helper method to read in a schema when de-serializing the object
     *
     * @param in The ObjectInputStream containing the serialized object
     * @return Schema The parsed schema.
     */
    protected static Schema readSchema(java.io.ObjectInputStream in) throws IOException {
        final Schema.Parser parser = new Schema.Parser();
        try {
            return parser.parse(in.readObject().toString());
        } catch (ClassNotFoundException cce) {
            throw new RuntimeException("Unable to read schema which is expected to be written as a java string", cce);
        }
    }

    protected void setFieldsFrom(Schema schema) {
        Fields cascadingFields = createFields(schema);
        setSinkFields(cascadingFields);
        setSourceFields(cascadingFields);
    }

    protected Fields createFields(Schema schema) {
        Fields cascadingFields = Fields.NONE;

        for (Field avroField : schema.getFields()) {
            String name = avroField.name();
            cascadingFields = cascadingFields.append(new Fields(name));
        }

        return cascadingFields;
    }

    public Schema getSchema() {
        return schema;
    }

    protected void setSchema(Schema schema) {
        this.schema = schema;
    }

    /**
     * Return the schema which has been set as a string
     *
     * @return String representing the schema
     */
    String getJsonSchema() {
        if (schema == null) {
            return "";
        } else {
            return schema.toString();
        }
    }

    /**
     * Sink method to take an outgoing tuple and write it to Avro.
     *
     * @param flowProcess The cascading FlowProcess object. Should be passed in by cascading automatically.
     * @param sinkCall    The cascading SinkCall object. Should be passed in by cascading automatically.
     * @throws IOException
     */
    @Override
    public void sink(
            FlowProcess<? extends Configuration> flowProcess,
            SinkCall<SinkContext, OutputCollector> sinkCall)
            throws IOException {
        TupleEntry tupleEntry = sinkCall.getOutgoingEntry();

        IndexedRecord record = new Record(schema);
        Object[] objectArray = CascadingToAvro.parseTupleEntry(tupleEntry, schema);
        for (int i = 0; i < objectArray.length; i++) {
            record.put(i, objectArray[i]);
        }
        //noinspection unchecked
        sinkCall.getOutput().collect(new AvroWrapper<IndexedRecord>(record), NullWritable.get());
    }

    /**
     * sinkConfInit is called by cascading to set up the sinks. This happens on the client side before the
     * job is distributed.
     * There is a check for the presence of a schema and an exception is thrown if none has been provided.
     * After the schema check the conf object is given the options that Avro needs.
     *
     * @param flowProcess The cascading FlowProcess object. Should be passed in by cascading automatically.
     * @param tap         The cascading Tap object. Should be passed in by cascading automatically.
     * @param conf        The Hadoop JobConf object. This is passed in by cascading automatically.
     * @throws RuntimeException If no schema is present this halts the entire process.
     */
    @Override
    public void sinkConfInit(
            FlowProcess<? extends Configuration> flowProcess,
            Tap<Configuration, RecordReader, OutputCollector> tap,
            Configuration conf) {

        if (schema == null) {
            throw new RuntimeException("Must provide sink schema");
        }
        // Set the output schema and output format class
        conf.set(AvroJob.OUTPUT_SCHEMA, schema.toString());
        ((JobConf) conf).setOutputFormat(AvroOutputFormat.class);


        // add AvroSerialization to io.serializations
        addAvroSerializations(conf);
    }

    /**
     * This method is called by cascading to set up the incoming fields. If a schema isn't present then it will
     * go and peek at the input data to retrieve one. The field names from the schema are used to name the cascading fields.
     *
     * @param flowProcess The cascading FlowProcess object. Should be passed in by cascading automatically.
     * @param tap         The cascading Tap object. Should be passed in by cascading automatically.
     * @return Fields The source cascading fields.
     */
    @Override
    public Fields retrieveSourceFields(FlowProcess<? extends Configuration> flowProcess, Tap tap) {
        if (schema == null) {
            try {
                schema = getSourceSchema(flowProcess, tap);
            } catch (IOException e) {
                throw new RuntimeException("Can't get schema from data source");
            }
        }

        setSourceFields(createFields(getSchema()));

        return getSourceFields();
    }

    /**
     * Source method to take an incoming Avro record and make it a Tuple.
     *
     * @param flowProcess The cascading FlowProcess object. Should be passed in by cascading automatically.
     * @param sourceCall  The cascading SourceCall object. Should be passed in by cascading automatically.
     * @return boolean true on successful parsing and collection, false on failure.
     * @throws IOException
     */
    @Override
    public boolean source(
            FlowProcess<? extends Configuration> flowProcess,
            SourceCall<SourceContext, RecordReader> sourceCall)
            throws IOException {

        RecordReader<AvroWrapper<IndexedRecord>, Writable> input = sourceCall.getInput();

        AvroWrapper<IndexedRecord> wrapper = sourceCall.getContext().key;
        Writable value = sourceCall.getContext().value;

        if (!input.next(wrapper, value)) {
            return false;
        }

        parseAvroWrapper(sourceCall, wrapper);

        return true;
    }

    protected void parseAvroWrapper(SourceCall<SourceContext, RecordReader> sourceCall, AvroWrapper<IndexedRecord> wrapper) {
        IndexedRecord record = wrapper.datum();
        Tuple tuple = sourceCall.getIncomingEntry().getTuple();
        tuple.clear();

        Object[] split = AvroToCascading.parseRecord(record, schema);

        tuple.addAll(split);
    }

    @Override
    public void sourcePrepare(FlowProcess<? extends Configuration> flowProcess, SourceCall<SourceContext, RecordReader> sourceCall) throws IOException {
        sourceCall.setContext(createSourceContext(sourceCall));
    }

    protected SourceContext createSourceContext(SourceCall<SourceContext, RecordReader> sourceCall) {
        RecordReader<AvroWrapper<IndexedRecord>, Writable> input = sourceCall.getInput();

        return new SourceContext(input.createKey(), input.createValue());
    }

    /**
     * sourceConfInit is called by cascading to set up the sources. This happens on the client side before the
     * job is distributed.
     * There is a check for the presence of a schema and if none has been provided the data is peeked at to get a schema.
     * After the schema check the conf object is given the options that Avro needs.
     *
     * @param flowProcess The cascading FlowProcess object. Should be passed in by cascading automatically.
     * @param tap         The cascading Tap object. Should be passed in by cascading automatically.
     * @param conf        The Hadoop JobConf object. This is passed in by cascading automatically.
     * @throws RuntimeException If no schema is present this halts the entire process.
     */
    @Override
    public void sourceConfInit(
            FlowProcess<? extends Configuration> flowProcess,
            Tap<Configuration, RecordReader, OutputCollector> tap,
            Configuration conf) {

        retrieveSourceFields(flowProcess, tap);
        // Set the input schema and input class
        conf.set(AvroJob.INPUT_SCHEMA, schema.toString());
        ((JobConf) conf).setInputFormat(AvroInputFormat.class);

        // add AvroSerialization to io.serializations
        addAvroSerializations(conf);
    }

    /**
     * This method peeks at the source data to get a schema when none has been provided.
     *
     * @param flowProcess The cascading FlowProcess object for this flow.
     * @param tap         The cascading Tap object.
     * @return Schema The schema of the peeked at data, or Schema.NULL if none exists.
     */
    protected Schema getSourceSchema(FlowProcess<? extends Configuration> flowProcess, Tap tap) throws IOException {

        if (tap instanceof CompositeTap) {
            tap = (Tap) ((CompositeTap) tap).getChildTaps().next();
        }
        final String path = tap.getFullIdentifier(flowProcess);
        Path p = new Path(path);
        final FileSystem fs = p.getFileSystem(flowProcess.getConfigCopy());
        // Get all the input dirs
        List<FileStatus> statuses = new LinkedList<FileStatus>(Arrays.asList(fs.globStatus(p, filter)));
        // Now get all the things that are one level down
        for (FileStatus status : new LinkedList<FileStatus>(statuses)) {
            if (status.isDir())
                for (FileStatus child : Arrays.asList(fs.listStatus(status.getPath(), filter))) {
                    if (child.isDir()) {
                        statuses.addAll(Arrays.asList(fs.listStatus(child.getPath(), filter)));
                    } else if (fs.isFile(child.getPath())) {
                        statuses.add(child);
                    }
                }
        }
        for (FileStatus status : statuses) {
            Path statusPath = status.getPath();
            if (fs.isFile(statusPath)) {
                // no need to open them all
                InputStream stream = null;
                DataFileStream reader = null;
                try {
                    stream = new BufferedInputStream(fs.open(statusPath));
                    reader = new DataFileStream(stream, new GenericDatumReader());
                    return reader.getSchema();
                } finally {
                    if (reader == null) {
                        if (stream != null) {
                            stream.close();
                        }
                    } else {
                        reader.close();
                    }
                }

            }
        }
        // couldn't find any Avro files, return null schema
        return Schema.create(Schema.Type.NULL);
    }

    private void addAvroSerializations(Configuration conf) {
        Collection<String> serializations = conf.getStringCollection("io.serializations");
        if (!serializations.contains(AvroSerialization.class.getName())) {
            serializations.add(AvroSerialization.class.getName());
            serializations.add(AvroSpecificRecordSerialization.class.getName());
        }


        conf.setStrings("io.serializations", serializations.toArray(new String[serializations.size()]));
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(this.schema.toString());
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException {
        this.schema = readSchema(in);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AvroScheme that = (AvroScheme) o;

        if (schema != null ? !schema.equals(that.schema) : that.schema != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "AvroScheme{" +
                "schema=" + schema +
                '}';
    }

    @Override
    public int hashCode() {

        return 31 * getSinkFields().hashCode() +
                (schema == null ? 0 : schema.hashCode());
    }
}


