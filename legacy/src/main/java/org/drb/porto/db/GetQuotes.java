package org.drb.porto.db;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


/*
 * Fills in a QuotationBlock structure with all quote records 
 * for a given list of tickers, a start date and an end date
 */
class GetQuotes
{
      
   private YahooRecord[ ] DoGetQuoteSlice( String sSymbol, Date dStartDay, Date dEndDay )
   {
      // s = symbol
      // n = name
      // l1 = last quote
      Calendar cStart = Calendar.getInstance( );
      cStart.setTime( dStartDay );
      int nA = cStart.get( Calendar.MONTH);
      int nB = cStart.get( Calendar.DAY_OF_MONTH );
      int nC = cStart.get( Calendar.YEAR );
      Calendar cEnd  = Calendar.getInstance( );
      cEnd.setTime( dEndDay );
      
      
      int nD = cEnd.get( Calendar.MONTH);
      int nE = cEnd.get( Calendar.DAY_OF_MONTH );
      int nF = cEnd.get( Calendar.YEAR );
      
      //
      //  Daily quotes: http://quote.yahoo.com/d/<returned file name>?s=<symbol>&f=snl1ohgv
      //  format info:
      //        s  = symbol
      //        n  = name
      //        l1 = close (latest)
      //        o  = open
      //        h  = high
      //        g  =  low
      //        v  = volumne (but doesn't seem to work ???)
      // 
      //  Historical
      //  http://ichart.finance.yahoo.com/table.csv?a=10&b=10&c=2006&d=10&e=22&f=2006&s=AGF.PA&y=0&g=d&ignore=.csv
      
      StringBuffer sb = new StringBuffer( );
      sb.append( "http://ichart.finance.yahoo.com/table.csv?" );
      sb.append( "a=" );
      sb.append( nA );
      sb.append( "&b=" );
      sb.append( nB );
      sb.append( "&c=" );
      sb.append( nC );
      sb.append( "&d=" );
      sb.append( nD );
      sb.append( "&e=" );
      sb.append( nE );
      sb.append( "&f=" );
      sb.append( nF );
      sb.append( "&s=" );
      sb.append( sSymbol );
      sb.append( "&y=0&g=d&ignore=.csv" );
      InputStream is = FetchFromURL( sb.toString() );
      YahooRecord[ ] ayr = null;
      if( is != null )
      {
         ayr = ParseInputStream( is );
      }
      return ayr;
   }
      
   /**
    * 
    * 
    *  Sets calendar c to the next end date:
    *     should be dStart + 100 days, but never after dEnd
    *     If no more dates, return null 
    *
    */
   private Date GetNextEndDate( int nBlockFactor, Date dStart, Date dEnd )
   {
      Calendar c = Calendar.getInstance( );
      c.setTime( dStart );
      c.add( Calendar.DATE, nBlockFactor );
      Date dNewEnd = c.getTime( );
      if( dNewEnd.after( dEnd ) )
         dNewEnd = dEnd;
      if( dNewEnd.equals( dStart ) || dNewEnd.before( dStart ) )
         dNewEnd = null;
      return dNewEnd;
   }
                 
   /*
    * Loops on tickers and fetches one array of YahooRecords for each
    */
   QuotationBlock[ ] DoGetQuotes( String[ ] saSymbols, Date dStartDay, Date dEndDay )
   {
      int nSize = saSymbols.length;
      QuotationBlock[ ] aqb = new QuotationBlock[ nSize ];
             
      for( int i = 0 ; i < nSize ; i++ )
      {
         System.out.print( "Fetching quotes for " + saSymbols[ i ] + " from " + dStartDay +  " to " + dEndDay );
         // slice in blocks
         int nBlockFactor = 200;

         // cuts the query in slices of no more than 200 days
         ArrayList<YahooRecord> v = new ArrayList<YahooRecord>( );
         Calendar cStart = Calendar.getInstance( );
         Date dStart = dStartDay;
         Date dEnd;
         while( null != ( dEnd = GetNextEndDate( nBlockFactor, dStart, dEndDay ) ) )
         {
            YahooRecord[ ] ayr = DoGetQuoteSlice( saSymbols[i], dStart, dEnd );
            if( ayr != null )
            {
               for( int j = ayr.length-1 ; j >= 0 ; j-- )
                  v.add( ayr [j ] );
            }
            cStart.setTime( dEnd );
            cStart.add( Calendar.DATE, 1 );
            dStart = cStart.getTime( );
            System.out.print( "." );
         }
         
         YahooRecord[ ] ayrFinal = v.toArray( new YahooRecord[ 0 ] );
         QuotationBlock qb = new QuotationBlock( saSymbols[i], ayrFinal );
         aqb[i ] = qb;
         System.out.println( "done." );
      }
      
      return aqb;
   }
   
   private InputStream FetchFromURL( String strURL )
   {
      InputStream is = null;
      try
      {
         URL anURL = new URL( strURL );
         is = anURL.openStream( );
      }
      catch( IOException e )
      {
         LogError( "openStream failed on URL " + strURL + ":" + e.getMessage() );  
      }
      return is;
   }
   
   private YahooRecord[ ] ParseInputStream( InputStream is ) 
   {
      ArrayList<YahooRecord> v = new ArrayList<YahooRecord>( );
      try
      {
         BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
         String strLine;
         br.readLine( ); // skip header
         //SimpleDateFormat sdf = new SimpleDateFormat( "dd-MMM-YY" );
         Pattern p = Pattern.compile( "," );
         while( null != (strLine = br.readLine( ) ) )
         {
            String[ ] straCols = p.split( strLine );
            YahooRecord yr = new YahooRecord( );           
            yr.SetValues( straCols );
            v.add( yr );
         }
      }
      catch( IOException e )
      {
         LogError( "Cannot retrieve InputStream " );
      }
      YahooRecord[ ] ayr = v.toArray( new YahooRecord[ 0 ] );
      return ayr;
   }
   
   private void LogError( String str )
   {
      System.err.println( str );
   }   
}
