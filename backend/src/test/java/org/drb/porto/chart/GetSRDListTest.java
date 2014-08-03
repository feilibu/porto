package org.drb.porto.chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.drb.porto.base.StockEntry;
import org.drb.porto.base.Stocks;
import org.drb.porto.db.GetSRDList;
import org.junit.Test;

public class GetSRDListTest
{
   @Test
   public void testUpdateStocks() throws IOException
   {
      GetSRDList getSRDList = new GetSRDList();

      // get database contents
      StockEntry[] sea = Stocks.GetAllStockEntries();
      System.out.println("Retrieved;" + sea.length + ";entries from database");
      HashMap<String, StockEntry> hmDatabase = new HashMap<String, StockEntry>();
      for (StockEntry se : sea)
         hmDatabase.put(se.GetIsin(), se);

      // get latest list from euronext
      HashMap<String, StockEntry> hmEuronext = getSRDList.DoGetSRDList();
      System.out.println("Retrieved;" + hmEuronext.size() + ";entries from euronext website");

      // analyze differences
      ArrayList<String> alDelete = new ArrayList<String>();
      ArrayList<StockEntry> alUpdate = new ArrayList<StockEntry>();
      ArrayList<StockEntry> alAdd = new ArrayList<StockEntry>();

      // update, add
      for (Entry<String, StockEntry> anEntry : hmEuronext.entrySet())
      {
         StockEntry se = hmDatabase.get(anEntry.getKey());
         if (se == null)
            alAdd.add(anEntry.getValue());
         else if ((!se.GetLabel().equals(anEntry.getValue().GetLabel()) || (!se.GetTicker().equals(anEntry.getValue().GetTicker()))))
            alUpdate.add(anEntry.getValue());
      }

      // delete
      for (String anEntry : hmDatabase.keySet())
      {
         if (hmEuronext.get(anEntry) == null)
            alDelete.add(anEntry);
      }

      Stocks.UpdateStockEntries(alAdd, alUpdate, alDelete);
   }
}
