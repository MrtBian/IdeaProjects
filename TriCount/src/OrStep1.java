import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class OrStep1 {
	public static class Mapper1 extends Mapper<Object, Text, Text, Text> {
        private Text edges = new Text();
		public void map(Object key,Text value,Context context)throws IOException,InterruptedException{
			StringTokenizer itr=new StringTokenizer(value.toString());
			while(itr.hasMoreTokens()){
				Text t=new Text("#");
				String str1=itr.nextToken().toString();
				String str2=itr.nextToken().toString();
				if(str1.compareTo(str2)<0){
					edges.set(str1+":"+str2);
					context.write(edges,t);
				}
				else if(str1.compareTo(str2)>0){
					edges.set(str2+":"+str1);
					context.write(edges,t);
				}
			}
		}
	}
    public static class NewCombiner extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            context.write(key, new Text("#"));
        }
    }
    public static class Reducer1 extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key,Iterable<Text> values,Context context)throws IOException,InterruptedException{
			context.write(key,new Text("#"));
		}
    }
    
    public static void main(String[] args) throws Exception{
        
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if(otherArgs.length != 2) {
            System.err.println("Usage: OrStep1 <in> <out>");
            System.exit(2);
        }
        @SuppressWarnings("deprecation")
		Job job = new Job(conf, "OTC1");
        job.setCombinerClass(NewCombiner.class);
        job.setJarByClass(OrStep1.class);
        
        job.setMapperClass(Mapper1.class);
        job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
        job.setReducerClass(Reducer1.class);
         
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.waitForCompletion(true);
    }
}
