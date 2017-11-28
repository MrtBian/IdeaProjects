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
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
 
public class OrTriangleCount {    
    
	public static class Mapper3 extends Mapper<Object, Text, Text, Text> {
    	public void map(Object key,Text value,Context context)throws IOException,InterruptedException{
			StringTokenizer itr=new StringTokenizer(value.toString());
			while(itr.hasMoreTokens()){
				Text A=new Text(itr.nextToken().toString());
				Text B=new Text(itr.nextToken().toString());
				context.write(A,B);
			}
		}    
    }

    public static class Reducer3 extends Reducer<Text, Text, Text, Text> {
        private static int result=0;
		public void reduce(Text key,Iterable<Text> values,Context context)throws IOException,InterruptedException{
			int exist_flag=0;
			int count=0;
			for(Text value:values){
				if(value.toString().equals("n")){
					count++;
				}
				else if(value.toString().equals("e")){
					exist_flag=1;
				}
			}
			if(exist_flag==1){
				result+=count;
			}
		}
		public void cleanup(Context context)throws IOException,InterruptedException{
			context.write(new Text("All triangle count : "),new Text(Integer.toString(result)));
		}
    }

    public static void main(String[] args) throws Exception{
        
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if(otherArgs.length != 2) {
            System.err.println("Usage: OrTriangleCount <in> <out>");
            System.exit(2);
        }
        @SuppressWarnings("deprecation")
		Job job = new Job(conf, "OTC3");
        job.setJarByClass(OrTriangleCount.class);
        
        job.setMapperClass(Mapper3.class);
        job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
        job.setReducerClass(Reducer3.class);
         
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
         
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.waitForCompletion(true);
    }
}
