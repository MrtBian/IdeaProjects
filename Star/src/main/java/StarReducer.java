import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;


public class StarReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        super.reduce(key, values, context);
        String fileList = new String();
        for(Text value : values) {
            fileList += value.toString() + ";";
        }
        Text result = new Text();
        result.set(fileList);
        context.write(key, result);
    }

    public void cleanup(Context context) throws IOException,
            InterruptedException {

    }

}
