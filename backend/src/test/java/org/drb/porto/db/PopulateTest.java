package org.drb.porto.db;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drb.porto.base.Stocks;

import junit.framework.TestCase;


public class PopulateTest extends TestCase
{

   protected void setUp() throws Exception
   {
      super.setUp();
   }

   
   public void testStockList( )
   {
      Stocks s = new Stocks( );
      String[ ] straList = s.GetTickerList();
      for( int i = 0 ; i < straList.length ; i++ )
         System.out.println( straList[i] );
   }
   
   
/*   
   public void testCatchup( ) throws SQLException
   {
      
      PopulateDB p = new PopulateDB( );
      Date dStart, dEnd;
      
      dEnd = new Date( );
      
      dStart = GetMaxDate( );
      p.SetStartAndEndDate(dStart, dEnd);
      p.DoPopulate( );

   }
*/   
   
   // to add:

   
   
/*   
   public void testPopulate( )
   {
      // CAC40 = ^FCHI
      PopulateDB p = new PopulateDB( );
      p.SetTickerList( new String[ ] {  } );
      Date dStart, dEnd;
      SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd");

      try
      {
         dStart = df.parse( "20000101" );
         dEnd = GetMaxDate(); 
         p.SetStartAndEndDate(dStart, dEnd);
         p.DoPopulate( );
      }
      catch( ParseException e )
      {
         System.err.println( "ParseException: " + e );
      }
     
      
   }
*/   
}
