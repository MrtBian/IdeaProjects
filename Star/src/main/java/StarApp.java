import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.io.Text;


public class StarApp {

    public static void main(String [] args) throws Exception{

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Star");
        job.setJarByClass(StarApp.class);
        job.setMapperClass(StarMapper.class);
       //  job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(StarReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
