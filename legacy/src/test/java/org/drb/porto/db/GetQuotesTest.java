package org.drb.porto.db;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



import junit.framework.TestCase;


public class GetQuotesTest extends TestCase
{

   protected void setUp() throws Exception
   {
      super.setUp();
   }
   
   public void testThreeQuotes( )
   {
      GetQuotes gq = new GetQuotes( );
      String[ ] straSymbols = new String[ ] { "AGF.PA", "AI.PA", "CGE.PA" };
      SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd");
      Date dEnd = null;
      Date dStart = null;
      try
      {
         dStart = df.parse( "20060101" );
         dEnd = df.parse( "20060102" );
      }
      catch( ParseException e )
      {
         System.err.println( "ParseException: " + e );
      }
      
      QuotationBlock[ ] aqb = gq.DoGetQuotes( straSymbols, dStart, dEnd );
      PrintWriter pw = new PrintWriter( System.err );
      for( int i = 0 ; i < aqb.length ; i++ )
      {
         aqb[ i ].Printout( pw );
      }
      pw.close( );
   }

}
