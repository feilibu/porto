package org.drb.porto.cmdline;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class GetOptUtils
{

   static void Fallback( int c, Getopt g, LongOpt[ ] loaOpts )
   {
      String arg;
      switch (c)
      {
      case 1:
         System.out.println("I see you have return in order set and that " +
               "a non-option argv element was just found " +
               "with the value '" + g.getOptarg() + "'");
         break;
         //
      case 2:
         arg = g.getOptarg();
         System.out.println("I know this, but pretend I didn't");
         System.out.println("We picked option " +
               loaOpts[g.getLongind()].getName() +
               " with value " + 
               ((arg != null) ? arg : "null"));
         break;
      case ':':
         System.out.println("Missing argument for option " + g.getOptopt());
         break;
      case '?':
         System.out.println("Invalid option '" + (char)(g.getOptopt()) +"'" );
         break;
         //
      default:
         System.out.println("getopt() returned " + (char) c);
         break;
      }
   }
   
   static Date InterpretDate( String str )
   {
      Date d = null;
      if( str == null || str.length( ) != 8 )
      {
         System.err.println( "Incorrect date format: " + str );
         return null;
      }
      DateFormat df = new SimpleDateFormat( "yyyyMMdd" );
      try
      {
         d = df.parse( str );
         return d;
      }
      catch( ParseException e )
      {
         System.err.println( "Incorrect date format:" + str + ":" + e );
         return null;
      }
   }

}
