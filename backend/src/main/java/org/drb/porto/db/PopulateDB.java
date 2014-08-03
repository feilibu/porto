package org.drb.porto.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import org.drb.porto.base.Stocks;



/**
 * 
 * Populates the database with a list of tickers 
 *
 */
public class PopulateDB
{
   private Date m_dStart;
   private Date m_dEnd;
   
   
   private String[ ] m_straTickerList;
   
   
   /**
    * Represents a unique key in the database
    */
   private static class QuoteKey
   {
      private String m_strTicker;
      private Date m_dDay;
      QuoteKey( )
      {
      
      }
      
      void SetTicker( String strTicker )
      {
         m_strTicker = strTicker;
      }
      void SetDay( Date dDay )
      {
         m_dDay = dDay;
      }
      
      private boolean SafeEquals( String str1, String str2 )
      {
         boolean bRet = false;
         if( str1 == null )
         {
            if( str2 == null )
               bRet = true;
         }
         else
            bRet = str1.equals( str2 );
         return bRet;
      }
      
      private boolean SafeEquals( Date d1, Date d2 )
      {
         boolean bRet = false;
         if( d1 == null )
         {
            if( d2 == null )
               bRet = true;
         }
         else
            bRet = d1.equals( d2 );
         return bRet;
      }
      
      public int hashCode( )
      {
         int nHash = 424242;
         if( m_dDay != null )
            nHash ^= m_dDay.hashCode();
         if( m_strTicker != null )
            nHash ^= m_strTicker.hashCode( );
         return nHash;   
      }
      
      public boolean equals( Object o )
      {
         boolean bRet = false;
         if( o instanceof QuoteKey )
         {
            QuoteKey qk = (QuoteKey) o;
            if( SafeEquals( m_strTicker, qk.m_strTicker ) && SafeEquals( m_dDay, qk.m_dDay ) )
               bRet = true;
         }
         return bRet;
      }
   }
   
   public PopulateDB( )
   {
      // default dates: from today - 1000 days to today
      m_dEnd = new Date( );
      Calendar c = Calendar.getInstance( );
      c.add( Calendar.DATE, -1000 );
      m_dStart = c.getTime( );
   }
   
   
   public void SetTickerList( String[ ] stra )
   {
      m_straTickerList = stra;
   }
   
   public void SetStartDate( Date dStart )
   {
      if( dStart == null )
         throw new IllegalArgumentException( "dStart MUST be non-null ");
      m_dStart = dStart;
   }
   public void SetEndDate( Date dEnd )
   {
      if( dEnd == null )
         throw new IllegalArgumentException( "dEnd MUST be non-null ");
      m_dEnd = dEnd;
   }

   private QuotationBlock[ ] FetchQuotes( String[ ] stra )
   {
      GetQuotes gq = new GetQuotes( );
      QuotationBlock[ ] qba = new QuotationBlock[ 0 ];
      try
      {
         qba = gq.DoGetQuotes( stra, m_dStart, m_dEnd );
      }
      catch( Exception e )
      {
         // carrying on
      }
      return qba;
   }
   
   /**
    * 
    * Gets the already stored couples (ticker, day) in order not to 
    * store them a second time
    * 
    * Return null on error
    */
   private HashSet<QuoteKey> GetStoredKeys( )
   {
      HashSet<QuoteKey> hm = null;
      DBAccess dba = new DBAccess( );
      try
      {
         dba.Connect( );
         if( dba.IsConnected( ) )
         {
            ResultSet rs = dba.GetAllKeys( );
            hm = new HashSet<QuoteKey>( );
            while( rs.next() )
            {
               QuoteKey qk = new QuoteKey( );
               qk.SetTicker( rs.getString( 1 ) );
               qk.SetDay( rs.getDate( 2 ) );
               hm.add(qk);
            }
            rs.close();
         }
         dba.Disconnect();
         dba = null;
      }
      catch( SQLException e )
      {
         System.err.println( "Could not retrieve database keys:" + e );
      }
      finally
      {
         if( dba != null && dba.IsConnected( ) )
            dba.Disconnect( );
      }
      return hm;
   
   }
   
   public void DoPopulate(  )
   {
      if( m_dStart == null || m_dEnd == null )
         throw new IllegalStateException( "Must call SetStartAndEndDate before DoPopulate" );
      
      if( m_straTickerList == null )
      {
         Stocks theStocks = new Stocks( );
         m_straTickerList = theStocks.GetTickerList();
      }   
      String[] stra = m_straTickerList;      
      
      // gets the records already in database
      HashSet<QuoteKey> hmKeys = GetStoredKeys( );
      if( hmKeys != null )
      {
         String[ ] straSlice = new String[ 1 ];
         for( int i = 0 ; i < stra.length ; i++ )
         {
            straSlice[ 0 ] = stra[ i ];
            // extracts the quote records
            QuotationBlock[ ] qba = FetchQuotes( straSlice );

            // and store in database
            StoreData( qba, hmKeys );
         }
      }
      System.out.println( );
   }
   
   private void StoreData( QuotationBlock[ ] qba, HashSet<QuoteKey> keys )
   {
      // store into database
      int nRecords = 0;
      int nRecordsSkipped = 0;
      try
      {
         DBAccess dba = new DBAccess( );
         dba.Connect( );
         if( dba.IsConnected() )
         {
            PreparedStatement s = dba.PrepareInsert();
            for( int nTicker = 0 ; nTicker < qba.length ; nTicker++ )
            {
               QuotationBlock qb = qba[nTicker];
               for( int nDate = 0 ; nDate < qb.GetRecordCount( ) ; nDate++ )
               {
                  YahooRecord yr = qb.GetRecord( nDate );
                  QuoteKey qk = new QuoteKey( );
                  qk.SetDay( yr.GetDate( ) );
                  qk.SetTicker( qb.GetTicker( ) );
                  if( ! keys.contains( qk ) )
                  {
                     dba.InsertRow(s, 
                                qb.GetTicker( ),
                                yr.GetLow( ),
                                yr.GetHigh( ),
                                yr.GetOpen(),
                                yr.GetClose(),
                                yr.GetVolume(),
                                yr.GetDate() );
                     nRecords++;
                  }
                  else
                  {
                     nRecordsSkipped++;   
                  }
               }
            }
            dba.EndInsert(s);
            dba.Disconnect();
         }
      }
      catch( SQLException sqlE )
      {
         System.err.println( "SQL Exception while inserting rows:" + sqlE );
      }
      System.out.println( "" + nRecords + " inserted. " + nRecordsSkipped + " were already in database." );
   }  
   
}
