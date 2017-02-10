package com.sm.demo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnRangeFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
 
 
/**
 * 
 * @author yankai913@gmail.com
 * @date 2014-4-28
 */
public class SimpleClient {
 
    static final String rowKey = "row1";
    static HBaseAdmin hBaseAdmin;
    static Configuration conf;
    static HTablePool hTablePool;
 
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "192.168.220.194");
        hTablePool = new HTablePool(conf, 100);
        try {
            hBaseAdmin = new HBaseAdmin(conf);
        }
        catch (MasterNotRunningException e) {
            e.printStackTrace();
        }
        catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
 
    public static void createTable(String tableName, String[] columns) throws Exception {
        dropTable(tableName);
        HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
        for (String columnName : columns) {
            HColumnDescriptor column = new HColumnDescriptor(columnName);
            column.setMaxVersions(1);
            
            hTableDescriptor.addFamily(column);
        }
        
        hBaseAdmin.createTable(hTableDescriptor);
        System.out.println("create table successed");
    }
 
 
    public static void dropTable(String tableName) throws Exception {
        if (hBaseAdmin.tableExists(tableName)) {
            hBaseAdmin.disableTable(tableName);
            hBaseAdmin.deleteTable(tableName);
        }
        System.out.println("drop table successed");
    }
 
 
    public static HTableInterface getHTable(String tableName) throws Exception {
    	
        return  hTablePool.getTable(tableName);
    }
 
 
    public static void insert(String tableName, Map<String, Long> map,String row) throws Exception {
        HTableInterface hTable = getHTable(tableName);
        byte[] row1 = Bytes.toBytes(row);
        Put p1 = new Put(row1);
        for (String columnName : map.keySet()) {
            byte[] value = Bytes.toBytes(map.get(columnName));
            String[] str = columnName.split(":");
            byte[] family = Bytes.toBytes(str[0]);
            byte[] qualifier = null;
            if (str.length > 1) {
                qualifier = Bytes.toBytes(str[1]);
            }
            p1.addColumn(family, qualifier, value);
        }
        hTable.put(p1);
        Get g1 = new Get(row1);
        Result result = hTable.get(g1);
        System.out.println("Get: " + result);
        System.out.println("insert successed");
    }
 
 
    public static void delete(String tableName, String rowKey) throws Exception {
        HTableInterface hTable = getHTable(tableName);
        List<Delete> list = new ArrayList<Delete>();
        Delete d1 = new Delete(Bytes.toBytes(rowKey));
        list.add(d1);
        hTable.delete(list);
        Get g1 = new Get(Bytes.toBytes(rowKey));
        Result result = hTable.get(g1);
        System.out.println("Get: " + result);
        System.out.println("delete successed");
    }
 
 
    public static void selectOne(String tableName, String rowKey) throws Exception {
        HTableInterface hTable = getHTable(tableName);
        Get g1 = new Get(Bytes.toBytes(rowKey));
        g1 = g1.setFilter(getFilterList());
        Result result = hTable.get(g1);
        foreach(result);
        System.out.println("selectOne end");
    }
    
    public static FilterList getFilterList(){
    	FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
    	SingleColumnValueFilter filter1 = new SingleColumnValueFilter(
    			Bytes.toBytes("column_A"),
    			Bytes.toBytes(""),
    		CompareOp.EQUAL,
    		Bytes.toBytes("AAA")
    		);
    	FamilyFilter filter2 = new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("column_B"))); // ����Ϊ my-family
    
    	ColumnRangeFilter filter3 = new ColumnRangeFilter(Bytes.toBytes("1"), true, Bytes.toBytes("2"), true);
    	RowFilter filter4 = new RowFilter(CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(rowKey)));
    	FirstKeyOnlyFilter filter5 = new FirstKeyOnlyFilter(); 
    	list.addFilter(filter1);
    	list.addFilter(filter2);
    	list.addFilter(filter3);
    	list.addFilter(filter4);
    	list.addFilter(filter5);
    	return list;
    }
 
 
    private static void foreach(Result result) throws Exception {
    	/*byte[] b = result.getValue(Bytes.toBytes("column_B"), Bytes.toBytes("1"));
    	 System.out.println(Bytes.toLong(b));*/
    	 
    	 KeyValue keyValue = result.getColumnLatest(Bytes.toBytes("column_B"), Bytes.toBytes("1"));
    	 
      
            StringBuilder sb = new StringBuilder();
            sb.append(Bytes.toString(keyValue.getRow())).append("\t");
            sb.append(Bytes.toString(keyValue.getFamily())).append("\t");
            sb.append(Bytes.toString(keyValue.getQualifier())).append("\t");
            sb.append(keyValue.getTimestamp()).append("\t");
            sb.append(Bytes.toLong(keyValue.getValue())).append("\t");
            System.out.println(sb.toString());
        
    }
    
    private static void increment(String tableName) throws Exception{
    	HTableInterface hTable = getHTable(tableName);
    	long ret = hTable.incrementColumnValue(Bytes.toBytes("1"), Bytes.toBytes("column_A"), Bytes.toBytes("abcd"), 1l);
    	System.out.println("increment successed"+ret);
    	
    }
 
 
    public static void selectAll(String tableName) throws Exception {
        HTableInterface hTable = getHTable(tableName);
        Scan scan = new Scan();
        //can.setFilter(getFilterList());
        ResultScanner resultScanner = null;
        try {
            resultScanner = hTable.getScanner(scan);
            for (Result result : resultScanner) {
                foreach(result);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (resultScanner != null) {
                resultScanner.close();
            }
        }
        System.out.println("selectAll end");
    }
 
 
    public static void main(String[] args) throws Exception {
        String tableName = "tableTest";
        String[] columns = new String[] { "column_A", "column_B" ,"info"};
      // createTable(tableName, columns);
        Map<String, Long> map = new HashMap<String, Long>();
       
        map.put("column_B:1", 1L);
        
       /* for(int i =0;i<100;i++)
  
        insert(tableName, map,i+"500");*/
        //selectOne(tableName, rowKey);
        selectAll(tableName);
        //increment(tableName);
        //delete(tableName, rowKey);
        //dropTable(tableName);
        
    }
}