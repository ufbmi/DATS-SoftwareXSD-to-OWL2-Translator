package edu.ufl.bmi.misc;

import java.io.*;
import java.lang.StringBuilder;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonArray;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnalyzeMdcObjectStructure {

    static Properties p;
 
     public static void main(String[] args) {
        FileReader fr = null;
        LineNumberReader lnr = null;
        FileOutputStream fos = null;
        FileWriter fw = null;
        FileWriter devOut = null;

        initialize(args);

        try {
            JsonArray jo = null;
            JsonParser jp = new JsonParser();
            String softwareMetadataLocation = p.getProperty("software_info");
          
            fr = new FileReader(softwareMetadataLocation);
            lnr = new LineNumberReader(fr);
            JsonElement je = jp.parse(fr);

            /*
            	The file is an array of JSON objects, one per digital object
            */
            jo = (JsonArray) je;

            /*
            //Code used by Levander to validate that the input data (software metadata)
            //was identical between new version and old version...

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Writer fOut = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("orig-software"), "UTF-8"));
            fOut.write(gson.toJson((JsonElement) jo));
            fOut.close();
            */

            Iterator<JsonElement> i;
            i = jo.iterator();
            System.out.println(jo.size());

            int cDataset=0;
            int cSoftware=0;
            int cDataFormat=0;
            int cDatasetWithOrg=0;
            int cOther=0;

            HashSet<String> allSoftwareAttributes = new HashSet<String>();
            HashSet<String> allDatasetAttributes = new HashSet<String>();
            HashSet<String> allFormatAttributes = new HashSet<String>();
            // this outer loop for iterator "i" processes one software/data service at a time
            while (i.hasNext()) {
            	/* 
            		Get the next element in the array, which is the JSON object that represents
            			a digital object.
            	*/
                JsonElement ei = i.next();
                JsonObject jo2 = (JsonObject) ei;

				/*
					Get the type attribute, and check its value.  If it's neither software, 
						nor data service, skip it.
				*/
				JsonElement typeElem = jo2.get("type");
				String typeFragment = null;
				if (typeElem.isJsonPrimitive()) {
					JsonPrimitive typeValue = (JsonPrimitive)typeElem;
					String type = typeValue.getAsString();
                    JsonElement contentElem = jo2.get("content");
                    JsonObject contentObject = (JsonObject)contentElem;
					if (type.equals("edu.pitt.isg.mdc.dats2_2.Dataset")) {
                        cDataset++;
                    } else if (type.equals("edu.pitt.isg.mdc.dats2_2.DataStandard")) {
                        cDataFormat++;
                    } else if (type.equals("edu.pitt.isg.mdc.dats2_2.DatasetWithOrganization")) {
						cDatasetWithOrg++;
                    } else if (type.startsWith("edu.pitt.isg.mdc.v1_0")) {
                        cSoftware++;
                    } else {
                        System.err.println("Don't understand type: " + type);
                        cOther++;
                    }
				} else {
					// else it's an error!
					System.err.println("Bad JSON - type element should be primitive.");
				}


            }    

            System.out.println(cSoftware + " software applications.");
            System.out.println(cDataset + " datasets.");
            System.out.println(cDataFormat + " formats.");
            System.out.println(cDatasetWithOrg + " datasets with org.");    
            System.out.println(cOther + " other types of object.");    

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void initialize(String[] args) {
        FileReader fr = null;

        try (FileReader pr = new FileReader(args[0]);) {

            /* 
                The properties tell this program where to find various resources, including
                    where to find a mapping from short text "handles" to needed IRIs.
            */
            p = new Properties();
            p.load(pr);


        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (fr != null) fr.close();
            } catch (IOException ioe) {
                //just eat it, eat it, don't you make me repeat it!
                //Strangely, this is the correct thing to do in this situation: yay, java!
            }
        }
    }

}
