package com.caymo.inHandAppAdc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

public class eBayFileIO {
    private static final String TAG = "eBayFileIO";
    private final String URL_STRING = "http://svcs.ebay.com/services/search/FindingService/v1" +
            "OPERATION-NAME=findItemsByKeywords" +
            "&SERVICE-VERSION=1.0.0" +
            "&SECURITY-APPNAME=inHanda34-8e86-4e05-9e5b-1fdeb7f3cab" +
            "&RESPONSE-DATA-FORMAT=XML" +
            "&REST-PAYLOAD" +
            "&keywords=";
    private final String FILENAME = "eBay_search_results.xml";
    private Context context = null;


    public eBayFileIO(Context context) {
        this.context = context;
    }

    public void downloadFile(String keywordsString) {
        try{
            keywordsString = keywordsString.replaceAll(" ", "%20");

            // get the URL
            // http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=inHanda34-8e86-4e05-9e5b-1fdeb7f3cab&RESPONSE-DATA-FORMAT=XML&REST-PAYLOAD&keywords=computers
            URL url = new URL("http://svcs.ebay.com/services/search/FindingService/v1" +
                    "?OPERATION-NAME=findItemsByKeywords" +
                    "&SERVICE-VERSION=1.0.0" +
                    "&SECURITY-APPNAME=inHanda34-8e86-4e05-9e5b-1fdeb7f3cab" +
                    "&RESPONSE-DATA-FORMAT=XML" +
                    "&REST-PAYLOAD" +
                    "&keywords=" + keywordsString);

            //URL url = new URL(URL_STRING + keywordsString);
            //url = new URL(URL_STRING);

            // get the input stream
            InputStream in = url.openStream();

            // get the output stream
            FileOutputStream out =
                context.openFileOutput(FILENAME, Context.MODE_PRIVATE);

            // read input and write output
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1)
            {
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            out.close();
            in.close();
        }
        catch (IOException e) {
            Log.e(TAG, e.toString());
            Log.i(TAG, FILENAME);
        }
    }

    public eBayURL readFile() {
        try {
            // get the XML reader
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            // set content handler
            eBayURLHandler theRssHandler = new eBayURLHandler();
            xmlreader.setContentHandler(theRssHandler);

            // read the file from internal storage
            FileInputStream in = context.openFileInput(FILENAME);

            // parse the data
            InputSource is = new InputSource(in);
            xmlreader.parse(is);

            // set the feed in the activity
            eBayURL feed = theRssHandler.getFeed();
            return feed;
        } 
        catch (Exception e) {
            Log.e(TAG, e.toString());

            return null;
        }
    }
}