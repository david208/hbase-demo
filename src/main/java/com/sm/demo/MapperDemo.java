package com.sm.demo;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapFileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

public class MapperDemo {
	
	public static class Map extends TableMapper<Text, LongWritable>{
		public static enum Counter{Rows,SHAKESPEAEAN};
		
		private boolean containsShakespeare(String msg){
			return (msg.equals("abcd"));
			
		}
		
		@Override
		protected void map(ImmutableBytesWritable key, Result result,
				Mapper<ImmutableBytesWritable, Result, Text, LongWritable>.Context context)
						throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			byte[] a = key.get();
			System.out.println("key:"+Bytes.toString(a));
			byte[] b = result.getValue(Bytes.toBytes("column_B"), Bytes.toBytes("1"));
			String msg = Bytes.toString(b);
				context.getCounter(Counter.Rows).increment(1);
				
			context.write(new Text("a"), new LongWritable(Bytes.toLong(b)));
			System.out.println("Counter.SHAKESPEAEAN"+context.getCounter(Counter.SHAKESPEAEAN).getValue());
			System.out.println("Counter.Rows"+context.getCounter(Counter.Rows).getValue());
		}
	}
	
	public static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
		
		@Override
		protected void reduce(Text key, Iterable<LongWritable> arg1,
				Reducer<Text, LongWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			long result = 0;
			for(LongWritable a : arg1){
				System.out.println("a.get()"+a.get());
				result += a.get();
			}     
			context.write(key, new LongWritable(result));
			System.out.println("result"+result);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.quorum", "192.168.220.194");
		Job job = new Job(configuration,"Tiwbase");
		job.setJarByClass(MapperDemo.class);
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes("column_B"), Bytes.toBytes("1"));
		TableMapReduceUtil.initTableMapperJob("tableTest", scan, Map.class, Text.class, LongWritable.class, job);
		job.setReducerClass(MyReducer.class);
		
		job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
		//job.setOutputFormatClass(NullOutputFormat.class);
      
		job.setNumReduceTasks(1);
		org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job,new Path("/abc"));
		
		System.exit(job.waitForCompletion(true)?0:1);
	}

}
