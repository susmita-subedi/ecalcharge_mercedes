package com.android.susmita.ecalcharge_mercedez_android;


import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.ServiceException;

public class GetApxData {

    public static final String LABEL_NET_VOLUME = "Net Volume";
    public static final String LABEL_NET_PRICE = "Price";
    public static final Integer LABEL_COUNT = 24;


    public Double[] getAPXprice(String spreadsheetKey, String date) {

        Map<String, Double[]> mapAPXdata = this.getAPXdata(spreadsheetKey, date);
        Double[] price = mapAPXdata.get(LABEL_NET_PRICE);

        return price;
    }

    public Map<String, Double[]> getAPXdata(String spreadsheetKey, String date) {

        Map<String, Double[]> mapAPXdata = new HashMap<>();

        try {

            //https://docs.google.com/spreadsheets/d/1FLCb7llLWZUPB-96DsWgOaiy9NN2_6galDebuTwkxEs/edit?usp=sharing
            //https://docs.google.com/spreadsheets/d/1dSyhVOt8sEpmopzNe-Vo5Fm-qIJX1Fa5E-HAZOFodj0/edit?usp=sharing

            String urlString = "https://spreadsheets.google.com/feeds/list/" + spreadsheetKey + "/default/public/values";

            SpreadsheetService service = new SpreadsheetService("org.GoogleSpreadsheetParser");

            // turn the string into a URL
            URL url = new URL(urlString);

            // You could substitute a cell feed here in place of
            // the list feed
            ListFeed feed = service.getFeed(url, ListFeed.class);

            for (ListEntry entry : feed.getEntries()) {

                CustomElementCollection elements = entry.getCustomElements();

                String value = elements.getValue("Date");

                if(value.equalsIgnoreCase(date)) {

                    Map<String, Double[]> mapData = getData(entry);
                    mapAPXdata.putAll(mapData);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return mapAPXdata;
    }

    private Map<String, Double[]> getData(ListEntry entry) {

        if(entry == null)
            return null;

        Double[] dbl_data = new Double[LABEL_COUNT + 1];
        String label = "";
        Map<String, Double[]> mapData = new HashMap<>();

        int count = 0;
        dbl_data[0] = 0.00;
        for (String tag : entry.getCustomElements().getTags()) {

            String value = entry.getCustomElements().getValue(tag);

            if(count == 0) {

                if(value.startsWith(LABEL_NET_VOLUME)) {

                    label = LABEL_NET_VOLUME;
                    count++;
                }
                else if(value.startsWith(LABEL_NET_PRICE)) {

                    label = LABEL_NET_PRICE;
                    count++;
                }

                continue;
            }

            double dblValue = 0.00;
            try {

                if(count != 0 && value != null && value.length() >0)
                    dblValue = Double.parseDouble(value);
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            dbl_data[count++] = dblValue;
        }

        mapData.put(label, dbl_data);
        return mapData;
    }

    public void printAPXdata(Map<String, Double[]> mapAPXdata) {
        for(String label : mapAPXdata.keySet()) {

            Double[] data = mapAPXdata.get(label);
            System.out.println(">>>>>>>>>" + label + "<<<<<<<<<");

            int count = 0;
            for(Double value : data) {

                System.out.println("[" + count++ + "]  -  " + value);
            }

            System.out.println("------------------------------");
        }
    }

    public static void main(String[] args) {

        //https://docs.google.com/spreadsheets/d/1dSyhVOt8sEpmopzNe-Vo5Fm-qIJX1Fa5E-HAZOFodj0/edit?usp=sharing

        GetApxData parser = new GetApxData();

        String spreadsheetKey = "1dSyhVOt8sEpmopzNe-Vo5Fm-qIJX1Fa5E-HAZOFodj0";
        String date = "08/19/2017";

        Map<String, Double[]> mapAPXdata = parser.getAPXdata(spreadsheetKey, date);

        parser.printAPXdata(mapAPXdata);
    }

}
