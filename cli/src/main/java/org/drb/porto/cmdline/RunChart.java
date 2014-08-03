package org.drb.porto.cmdline;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.Date;

import org.drb.porto.chart.Chart;
import org.jfree.chart.JFreeChart;

public class RunChart implements Command
{

   @Override
   public void run(String str[]) throws Exception
   {
      Chart aChart = new Chart( );

      if( InterpretCmdLine( aChart, str ) )
      {
         JFreeChart cp = aChart.Plot( );
         aChart.CreateFrame( cp );
      }
   }

   /**
    * Interprets command lines, sets parameters to the PopulateDB object, return true if ok to proceed, false if
    * should abort
    */
   private static boolean InterpretCmdLine( Chart aChart, String[ ] stra )
   {
      int c;
      LongOpt[] loaOpts = new LongOpt[4];
      loaOpts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
      loaOpts[1] = new LongOpt("name", LongOpt.OPTIONAL_ARGUMENT, null, 'n');
      loaOpts[2] = new LongOpt("startdate", LongOpt.OPTIONAL_ARGUMENT, null, 's');
      loaOpts[3] = new LongOpt("enddate", LongOpt.OPTIONAL_ARGUMENT, null, 'e');
      loaOpts[3] = new LongOpt("selection", LongOpt.OPTIONAL_ARGUMENT, null, 'S');

      Getopt g = new Getopt("runchart", stra, "ht:s:e:", loaOpts);
      g.setOpterr(false); // We'll do our own error handling

      while ((c = g.getopt()) != -1)
      {
         switch (c)
         {
         case 'n':
            aChart.SetLooseName( g.getOptarg( ) );
            break;
         case 'e':
            Date dEnd = GetOptUtils.InterpretDate( g.getOptarg( ) );
            aChart.SetEndDate( dEnd );
            break;
         case 's':
            Date dStart =  GetOptUtils.InterpretDate( g.getOptarg( ) );
            aChart.SetStartDate( dStart );
            break;
         case 'S':
            aChart.SetSelectionName( g.getOptarg( ) );
            break;
         case 'h':
            System.out.println( "USAGE: RunChart [--name=<loose name>] [--selection=<sel name>] [--startdate=YYYMMDD] [--enddate=YYYYMMDD]");
            return false;
         default:
            GetOptUtils.Fallback( c, g, loaOpts );
            return false;
         }
      }
      return true;
   }


}
