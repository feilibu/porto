package org.drb.porto.cmdline;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.Date;

import org.drb.porto.db.RectifyDB;

public class RectifyTicker implements Command
{
   @Override
   public void run(String[] stra)
   {
      RectifyDB aRDB = new RectifyDB( );
      if( InterpretCmdLine( aRDB, stra ) )
         aRDB.DoRectify();
   }


   /**
    * Interprets command lines, sets parameters to the PopulateDB object, return true if ok to proceed, false if
    * should abort
    */
   private static boolean InterpretCmdLine( RectifyDB aRDB, String[ ] stra )
   {
      int c;
      LongOpt[] loaOpts = new LongOpt[4];
      loaOpts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
      loaOpts[1] = new LongOpt("ticker", LongOpt.REQUIRED_ARGUMENT, null, 't');
      loaOpts[2] = new LongOpt("newfirstday", LongOpt.REQUIRED_ARGUMENT, null, 'n');
      loaOpts[3] = new LongOpt("factor", LongOpt.REQUIRED_ARGUMENT, null, 'f');

      Getopt g = new Getopt("rectify", stra, "ht:n:f:", loaOpts);
      g.setOpterr(false); // We'll do our own error handling

      while ((c = g.getopt()) != -1)
      {
         switch (c)
         {
         case 't':
            aRDB.SetTicker( g.getOptarg( ) );
            break;
         case 'n':
            Date dNew = GetOptUtils.InterpretDate( g.getOptarg( ) );
            if( dNew == null )
               return false;
            aRDB.SetNewDate( dNew );
            break;
         case 'f':
            float fFactor = Float.parseFloat( g.getOptarg( ) );
            aRDB.SetFactor( fFactor );
            break;
         case 'h':
            System.out.println( "USAGE: Rectify --ticker=<ticker> --newdate=YYYMMDD --factor=<factor>");
            return false;
         default:
            GetOptUtils.Fallback( c, g, loaOpts );
            return false;
         }
      }
      return true;
   }
}
