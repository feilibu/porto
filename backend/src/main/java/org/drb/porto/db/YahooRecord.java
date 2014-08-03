package org.drb.porto.db;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * Models one record as returned by the yahoo URL
 *  
 *     http://table.finance.yahoo.com/table.csv?a=9&b=10&c=2006&d=9&e=23&f=2006&s=CGE.PA&y=0&g=d&ignore=.csv
 */
class YahooRecord
{
   private Date m_date;
   private double m_dOpen;
   private double m_dHigh;
   private double m_dLow;
   private double m_dClose;
   private int m_nVolume;
   private double m_dAdjClose;
   
   // columns in yahoo reply
   static final private int INDEX_DATE = 0;
   static final private int INDEX_OPEN = 1;
   static final private int INDEX_HIGH= 2;
   static final private int INDEX_LOW = 3;
   static final private int INDEX_CLOSE = 4;
   static final private int INDEX_VOLUME = 5;
   static final private int INDEX_ADJCLOSE = 6;
   
   static final int NBCOLS = 6;
   
   
   // date format can be randomly one or the other
   private SimpleDateFormat m_sdf = new SimpleDateFormat( "dd-MMM-yy", Locale.US );
   private SimpleDateFormat m_sdf2 = new SimpleDateFormat( "yyyy-M-d", Locale.US );
   

   YahooRecord( )
   {

   }

   void SetValues( String[ ] str )
   { 
      m_date = AsDate( str, INDEX_DATE );
      m_dOpen = AsDouble( str, INDEX_OPEN );
      m_dHigh = AsDouble( str, INDEX_HIGH );
      m_dLow = AsDouble( str, INDEX_LOW );
      m_dClose = AsDouble( str, INDEX_CLOSE );
      m_nVolume = AsInt( str, INDEX_VOLUME );
      m_dAdjClose = AsDouble( str, INDEX_ADJCLOSE );
   }

   void Printout( PrintWriter pw )
   {
      pw.println( "\t" + m_date + "\t" + 
                  m_dOpen + "\t" + 
                  m_dHigh + "\t" + 
                  m_dLow + "\t" + 
                  m_dClose + "\t" + 
                  m_nVolume + "\t" + 
                  m_dAdjClose ); 

   }

   private double AsDouble( String str[ ], int nIndex )
   {
      return Double.parseDouble( str[nIndex ] );
   }

   private int AsInt( String str[ ], int nIndex )
   {
      return Integer.parseInt(str[nIndex ] );
   }
   
   private Date AsDate( String str[ ], int nIndex )
   {
      Date d = new Date( );
      try
      {
         d = m_sdf.parse( str[nIndex ] );
      }
      catch( ParseException e )      
      {
         // try again with alternate date format
         try
         {
            d = m_sdf2.parse( str[nIndex ] );
         }
         catch( ParseException e2 )
         {
            System.err.println( "ParseException: " + e2 );
         }
      }
      return d;
   }
   
   Date GetDate( )
   {
      return m_date;
   }
   double GetOpen( )
   {
      return m_dOpen;
   }
   double GetHigh( )
   {
      return m_dHigh;
   }
   double GetLow( )
   {
      return m_dLow;
   }
   double GetClose( )
   {
      return m_dClose;
   }
   
   int GetVolume( )
   {
      return m_nVolume;
   }
   
}

