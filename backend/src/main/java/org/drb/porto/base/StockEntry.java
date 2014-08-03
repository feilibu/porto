package org.drb.porto.base;

public class StockEntry
{
   private String m_strLabel;
   private String m_strTicker;
   private String m_strIsin;

   public StockEntry( String strTicker, String strLabel, String strIsin )
   {
      m_strLabel = strLabel;
      m_strTicker = strTicker;
      m_strIsin = strIsin;
   }
   
   public String GetTicker( )
   {
      return m_strTicker;
   }
   
   public    String GetLabel( )
   {
      return m_strLabel;
   }
   
   public String GetIsin( )
   {
      return m_strIsin;
   }
}
