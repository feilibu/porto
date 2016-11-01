package org.drb.porto.db;

import java.io.PrintWriter;



/**
 * Association of a ticker + list of quotation records
 */
class QuotationBlock
{
   private String m_strTicker;
   private YahooRecord[ ] m_ayr;
   QuotationBlock( String strTicker, YahooRecord[ ] ayr )
   {
      m_strTicker = strTicker;
      m_ayr = ayr;
   }
   
   YahooRecord GetRecord( int i )
   {
      return m_ayr[ i ];
   }
   
   int GetRecordCount( )
   {
      return m_ayr.length;
   }
   
   String GetTicker( )
   {
      return m_strTicker;
   }

   
   void Printout( PrintWriter pw )
   {
      pw.println( "**" + m_strTicker + "**" );
      for( int i = 0 ; i < m_ayr.length ; i++ )
      {
         m_ayr[ i ].Printout( pw );
      }
   }
}
