package com.sm.demo.bdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;

public class ExampleDatabasePut {

    private static File myDbEnvPath = new File("D:\\cache");
//    private static File vendorsFile = new File("./inventory.txt");


    private DataAccessor da;

    // Encapsulates the environment and data store.
    private static MyDbEnv myDbEnv = new MyDbEnv();
    
    private static void usage() {
        System.out.println("ExampleDatabasePut [-h <env directory>]");
        System.out.println("      [-i <inventory file>]");
        System.out.println("      [-v <vendors file>]");
        System.exit(-1);
    } 
    
    public static void main(String args[]) {
        ExampleDatabasePut edp = new ExampleDatabasePut();
        try {
            edp.run(args);
        } catch (DatabaseException dbe) {
            System.err.println("ExampleDatabasePut: " + dbe.toString());
            dbe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
            e.printStackTrace();
        } finally {
            myDbEnv.close();
        }
        System.out.println("All done.");
    } 
    
    private void run(String args[])
            throws DatabaseException {
            // Parse the arguments list
          //  parseArgs(args);

            myDbEnv.setup(myDbEnvPath,  // Path to the environment home 
                          false);       // Environment read-only?

            // Open the data accessor. This is used to store
            // persistent objects.
            da = new DataAccessor(myDbEnv.getEntityStore());

            System.out.println("loading vendors db....");
            loadVendorsDb();


        } 
    
    private void loadVendorsDb()
            throws DatabaseException {

        // loadFile opens a flat-text file that contains our data
        // and loads it into a list for us to work with. The integer
        // parameter represents the number of fields expected in the
        // file.
//        List vendors = loadFile(vendorsFile, 8);

        // Now load the data into the store.
        for (int i = 0; i < 500; i++) {
            Product theVendor = new Product();
            theVendor.setId(i);
            theVendor.setName("abc"+i);
            theVendor.setAmt(13244l);
           

            // Put it in the store.
            da.inventoryBySku.put(theVendor);
        }
        EntityCursor<Product> cursor = da.inventoryBySku.entities();
        for(Product p :cursor){
        	System.out.println(p);
        }
        cursor.close();
    } 
    
    private List loadFile(File theFile, int numFields) {
        List<String[]> records = new ArrayList<String[]>();
        try {
            String theLine = null;
            FileInputStream fis = new FileInputStream(theFile);
            BufferedReader br = 
                new BufferedReader(new InputStreamReader(fis));
            while((theLine=br.readLine()) != null) {
                String[] theLineArray = theLine.split("#");
                if (theLineArray.length != numFields) {
                    System.out.println("Malformed line found in " + 
                        theFile.getPath());
                    System.out.println("Line was: '" + theLine);
                    System.out.println("length found was: " + 
                        theLineArray.length);
                    System.exit(-1);
                }
                records.add(theLineArray);
            }
            // Close the input stream handle
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println(theFile.getPath() + " does not exist.");
            e.printStackTrace();
            usage();
        } catch (IOException e)  {
            System.err.println("IO Exception: " + e.toString());
            e.printStackTrace();
            System.exit(-1);
        }
        return records;
    }
    
}
    
    