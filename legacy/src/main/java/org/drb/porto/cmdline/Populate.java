package org.drb.porto.cmdline;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.Date;

import org.drb.porto.db.PopulateDB;

public class Populate implements Command
{
   @Override
   public void run(String[] stra)
   {
      PopulateDB aPopulate = new PopulateDB( );
      if( InterpretCmdLine( aPopulate, stra ) )
         aPopulate.DoPopulate();
   }


   /**
    * Interprets command lines, sets parameters to the PopulateDB object, return true if ok to proceed, false if
    * should abort
    */
   private static boolean InterpretCmdLine( PopulateDB aPopulate, String[ ] stra )
   {
      int c;
      LongOpt[] loaOpts = new LongOpt[4];
      loaOpts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
      loaOpts[1] = new LongOpt("ticker", LongOpt.OPTIONAL_ARGUMENT, null, 't');
      loaOpts[2] = new LongOpt("startdate", LongOpt.OPTIONAL_ARGUMENT, null, 's');
      loaOpts[3] = new LongOpt("enddate", LongOpt.OPTIONAL_ARGUMENT, null, 'e');

      Getopt g = new Getopt("populate", stra, "ht:s:e:", loaOpts);
      g.setOpterr(false); // We'll do our own error handling

      while ((c = g.getopt()) != -1)
      {
         switch (c)
         {
         case 't':
            System.out.println("ticker = " + g.getOptarg( ));
            aPopulate.SetTickerList( new String[ ] { g.getOptarg( ) } );
            break;
         case 'e':
            Date dEnd = GetOptUtils.InterpretDate( g.getOptarg( ) );
            if( dEnd == null )
               return false;
            aPopulate.SetEndDate( dEnd );
            break;
         case 's':
            Date dStart =  GetOptUtils.InterpretDate( g.getOptarg( ) );
            if( dStart == null )
               return false;
            aPopulate.SetStartDate( dStart );
            break;
         case 'h':
            System.out.println( "USAGE: Populate [--ticker=<ticker>] [--startdate=YYYMMDD] [--enddate=YYYYMMDD]");
            return false;
         default:
            GetOptUtils.Fallback( c, g, loaOpts );
            return false;
         }
      }
      return true;
   }

}
