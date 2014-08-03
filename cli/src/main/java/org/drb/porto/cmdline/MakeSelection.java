package org.drb.porto.cmdline;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.drb.porto.analysis.SelectStocks;
import org.drb.porto.base.Stocks;

//
//  cmd line interface for SelectStocks
//
public class MakeSelection implements Command
{
   @Override
   public void run(String[] stra)
   {
      SelectStocks ss = new SelectStocks( );
      if( InterpretCmdLine( ss, stra ) )
      {
         try
         {
            makeSelection(ss);
         }
         catch( SQLException e )
         {
            System.err.println( "Could not make selection" + e );
         }
         finally
         {
            ss.close();
         }
      }
   }

   private void makeSelection(SelectStocks ss) throws SQLException
   {
      Stocks s = new Stocks();
      Calendar c = Calendar.getInstance();
      c.add(Calendar.DATE, -1500);
      ss.Run(s);
      ss.DisplayResult();
   }

   /**
    * Interprets command lines, sets parameters to the PopulateDB object, return true if ok to proceed, false if
    * should abort
    */
   private boolean InterpretCmdLine(SelectStocks ss, String[] stra)
   {
      int c;
      LongOpt[] loaOpts = new LongOpt[4];
      loaOpts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
      loaOpts[1] = new LongOpt("nbdays", LongOpt.OPTIONAL_ARGUMENT, null, 'd');
      loaOpts[2] = new LongOpt("startdate", LongOpt.OPTIONAL_ARGUMENT, null, 's');
      loaOpts[3] = new LongOpt("enddate", LongOpt.OPTIONAL_ARGUMENT, null, 'e');

      Getopt g = new Getopt("populate", stra, "ht:s:e:", loaOpts);
      g.setOpterr(false); // We'll do our own error handling

      while ((c = g.getopt()) != -1)
      {
         switch (c)
         {
         case 'd':
            ss.SetHorizonDays( Integer.parseInt( g.getOptarg( ) ) );
            break;
         case 'e':
            Date dEnd = GetOptUtils.InterpretDate( g.getOptarg( ) );
            ss.SetEndDate( dEnd );
            break;
         case 's':
            Date dStart =  GetOptUtils.InterpretDate( g.getOptarg( ) );
            ss.SetStartDate( dStart );
            break;
         case 'h':
            System.out.println( "USAGE: MakeSelection [--nbdays=<nb days>] [--startdate=YYYMMDD] [--enddate=YYYYMMDD]");
            return false;
         default:
            GetOptUtils.Fallback( c, g, loaOpts );
            return false;
         }
      }
      return true;
   }

}