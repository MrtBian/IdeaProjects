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

public class OrStep2 {
	public static class Mapper2 extends Mapper<Object, Text, Text, Text> {
    	public void map(Object key,Text value,Context context)throws IOException,InterruptedException{
			StringTokenizer itr=new StringTokenizer(value.toString());
			while(itr.hasMoreTokens()){
				String []word=itr.nextToken().toString().split(":");
				itr.nextToken();
				Text A=new Text(word[0]);
				Text B=new Text(word[1]);
				context.write(A,B);
			}
		}         
    }

    public static class Reducer2 extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key,Iterable<Text> values,Context context)throws IOException,InterruptedException{
			Text edges=new Text();
			Text exist=new Text("e");
			ArrayList<String> T= new ArrayList<String>();
			for(Text val:values)
			{
				String value=val.toString();
				T.add(value);
				edges.set(key.toString()+":"+value);
				context.write(edges,exist);
			}
			Text atob=new Text();
			Text need=new Text("n");
			for(int i=0;i<T.size();i++){
				String a=T.get(i);
				for(int j=i+1;j<T.size();j++){
					String b=T.get(j);
					if(a.compareTo(b)<0){
						atob.set(a+":"+b);
						context.write(atob,need);
					}
					else if(a.compareTo(b)>0){
						atob.set(b+":"+a);
						context.write(atob,need);
					}
				}
			}
		}
    }
    public static void main(String[] args) throws Exception{
        
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if(otherArgs.length != 2) {
            System.err.println("Usage: OrStep2 <in> <out>");
            System.exit(2);
        }
        @SuppressWarnings("deprecation")
		Job job = new Job(conf, "OTC2");
        job.setJarByClass(OrStep2.class);
        
        job.setMapperClass(Mapper2.class);
        job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
        job.setReducerClass(Reducer2.class);
         
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        job.waitForCompletion(true);
    }
}
