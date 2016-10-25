package org.drb.porto.domain.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OHLCDataset
{
   private String title;
   private List<OHLCDataItem> items;
   
   public OHLCDataset(String string, OHLCDataItem[] theItems)
   {
      title = string;
      items = new ArrayList<OHLCDataItem>(Arrays.asList(theItems));
   }
   
   String getTitle()
   {
      return title;
   }
   
   List<OHLCDataItem> getItems()
   {
      return items;
   }

}
