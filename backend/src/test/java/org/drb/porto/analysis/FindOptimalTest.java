package org.drb.porto.analysis;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.drb.porto.base.Stocks;
import org.drb.porto.utils.Stats;

import junit.framework.TestCase;


public class FindOptimalTest extends TestCase
{

   protected void setUp() throws Exception
   {
      super.setUp();
   }
   
   public void testComputeStats( )
   {
      double dCmpPrec = 0.00001;
      double[ ] faValues = { 0, 1, 2, 3, 4, 5 };
      
      double dAverage = Stats.ComputeAvg( faValues );
      assertEquals( 2.5, dAverage, dCmpPrec );
      double dStdDev = Stats.ComputeStdDev(faValues );
      assertEquals( 1.70782512, dStdDev, dCmpPrec );
      
      double[ ] daExpRes = { 0, 0.5, 1, 2, 3, 4 };
      double[ ] daMobAvg = Stats.ComputeMobileAvg( faValues, 3 );
      for( int i = 0 ; i < daExpRes.length ; i++ )
      {
         assertEquals( daMobAvg[ i ], daExpRes[ i ], dCmpPrec );
      }
   }
   
   // todo: rewrite this as a "FindOptimal" subclass
   public void testFindActualBest( )
   {
      /*
      Stocks s = new Stocks( );
      Date dStart = null, dEnd = null;
      SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd");

      try
      {
         dStart = df.parse( "20060920" );
         dEnd = new Date( ); //df.parse( "20060920" );
      }
      catch( ParseException e )
      {
         System.err.println( "ParseException: " + e );
      }

      Quotes[ ] qa = null;
      try
      {
         String[ ] straTickers = s.GetTickerList();
         qa = new Quotes[ straTickers.length ];
         for( int i = 0 ; i < straTickers.length ; i++ )
         {
            Quotes q = new Quotes( );
            q.Acquire( straTickers[ i ], dStart, dEnd);
            SimpleGrade sg = new SimpleGrade( );
            q.SetGrade( sg );
            double[ ] daClose = q.GetValues( Quotes.CLOSE );
            sg.SetGrade( Double.NaN );
            if( daClose.length > 0 )
            {
              double d1 = daClose[ 0 ];
              double d2 = daClose[ daClose.length - 1 ];
              sg.SetGrade( (d2 - d1) /d1 );
            }
            qa[ i ] = q;
         }
      }
      catch( SQLException ex )
      {
         System.err.println( "Caught SQLException: " + ex );
      }
      Arrays.sort( qa );
      for( int i = 0 ; i < qa.length ; i++ )
      {
         StringBuffer sb = new StringBuffer( );
         sb.append( qa[ i ].GetTicker() );
         sb.append( "\t" );
         sb.append( qa[ i ].GetName() );
         sb.append( "\t" );
         qa[ i ].GetGrade( ).AppendRecord( sb );
         
         System.err.println( sb );
      }
      */
   }
   
   
   public void testWeinstein( )
   {
      FindOptimal fo = new FindWeinsteinOptimal( );
      Stocks s = new Stocks( );
      
      Date dStart = null, dEnd = null;
      SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd");

      try
      {
         dStart = df.parse( "20000115" );
         dEnd = new Date( ); // df.parse( "20070120" );
      }
      catch( ParseException e )
      {
         System.err.println( "ParseException: " + e );
      }

      try
      {
         fo.SetStartDate( dStart );
         fo.SetEndDate( dEnd );
         fo.SetHorizonDays( 50 );
         fo.Run( s );
      }
      catch( SQLException ex )
      {
         System.err.println( "Caught SQLException: " + ex );
      }
     
      fo.DisplayResult();
   }

   public void testHighestGrowth( )
   {
      // highest growth over the past 3 months
      int nDaysCount = 60;
      // starting from now
      Date dEnd = new Date( );
      
      FindOptimal fo = new HighestGrowth( nDaysCount );
      Stocks s = new Stocks( );
      Calendar c = Calendar.getInstance( );
      c.setTime( dEnd );
      c.add( Calendar.DATE, - nDaysCount );
      Date dStart = c.getTime( ); 
      
      try
      {
         fo.SetHorizonDays( nDaysCount );
         fo.SetStartDate( dStart );
         fo.SetEndDate( dEnd );
         fo.Run( s );
      }
      catch( SQLException ex )
      {
         System.err.println( "Caught SQLException: " + ex );
      }
     
      fo.DisplayResult();
   }
}
