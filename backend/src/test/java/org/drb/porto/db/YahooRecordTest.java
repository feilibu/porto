package org.drb.porto.db;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;


public class YahooRecordTest extends TestCase
{   
   public void testParseDate( ) throws Exception
   {
      String strDate = "20-Oct-06";
      Locale l = Locale.US;
      SimpleDateFormat sdf = new SimpleDateFormat( "dd-MMM-yy", l );
      try
      {
         Date d = sdf.parse( strDate );
         System.err.println( d );
      }
      catch( ParseException e )
      {
         System.err.println( e );
      }
        
   }

}
