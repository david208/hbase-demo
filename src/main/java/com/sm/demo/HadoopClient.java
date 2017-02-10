package com.sm.demo;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

public class HadoopClient {
	
	    static final String rowKey = "row1";
	    static HBaseAdmin hBaseAdmin;
	    static Configuration conf = new Configuration();
	    static Job job ;
	    
	    
	    static{
	    	try {
	    		conf.set("hbase.zookeeper.quorum", "192.168.220.143");
				job= new Job(conf,"TImeSpent");
				job.setInputFormatClass(org.apache.hadoop.mapreduce.lib.input.TextInputFormat.class);
				job.setOutputFormatClass(org.apache.hadoop.mapreduce.lib.output.TextOutputFormat.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	  public static void main(String[] args) {
		  Scan scan = new Scan();
			scan.addColumn(Bytes.toBytes("column_B"), Bytes.toBytes("1"));
	}
	    
	   

}
