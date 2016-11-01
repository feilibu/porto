package org.drb.porto.analysis;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.drb.porto.base.Grade;
import org.drb.porto.base.Quotes;
import org.drb.porto.base.Stocks;
import org.drb.porto.utils.Stats;



/**
 *
 * todo: do not truncate values before start of considered
 * period: it must be used for computation of mob. avg.
 *
 *
 */
abstract class FindOptimal
{
   private Quotes[ ] m_qaList;
   private int m_nDaysHorizon;
   private Date m_dStart;
   private Date m_dEnd;

   FindOptimal( )
   {
      m_nDaysHorizon = 1000;
      Calendar c = Calendar.getInstance( );
      m_dEnd = c.getTime( );
      c.add( Calendar.DATE, -m_nDaysHorizon );
      m_dStart = c.getTime( );
   }
   int ComputeNbDaysAbove( double[ ] daValues, double[ ] daMobAvg )
   {
      return ComputeNbDaysAbove( daValues, daMobAvg, 0, daValues.length - 1 );
   }

   /**
    *
    * Computes nb of times when daValues[ i ] >= daMobAvg[ i ]
    * starts at index nStart, ends at index nEnd (inclusive)
    *
    */
   int ComputeNbDaysAbove( double[ ] daValues, double[ ] daMobAvg, int nStart, int nEnd )
   {
      int nTotalSize = daValues.length;
      assert nTotalSize == daMobAvg.length;
      assert nStart >= 0;
      assert nEnd < nTotalSize;
      assert nStart <= nEnd;

      int n = 0;
      for( int i = nStart ; i <= nEnd; i++ )
      {
         if( daValues[ i ] >= daMobAvg[ i ] )
            n++;
      }
      return n;
   }

   double GetGrade( double[ ] daValues, Quotes q )
   {
      if( daValues.length < 1 )
         return Double.NaN;

      // compute std deviation
      double dStdDev = Stats.ComputeStdDev( daValues );
      q.SetStdDeviation( dStdDev );

      // compute mobile avg on nHorizonDays
      double[ ] daMobileAvg = Stats.ComputeMobileAvg( daValues, m_nDaysHorizon );

      // indicators:
      //   x1: nb days above mob avg.      (as high as possible)
      //   x2: std dev                     (as low as possible)
      //   x3: % of increase on the period (as high as possible)
      double x1 = ((double) ComputeNbDaysAbove( daValues, daMobileAvg )) / daValues.length;
      double x3 = Double.NaN;
      {
         x3 = (daValues[ daValues.length - 1 ] - daValues[ 0 ]) / daValues[ 0 ];
      }
      q.SetMobAvgConsistency( x1 );
      q.SetVariation( x3 );

      return x1  * x3;
   }

   public void SetStartDate( Date d )
   {
      m_dStart = d;
   }

   public void SetEndDate( Date d )
   {
      m_dEnd = d;
   }

   public void SetHorizonDays( int nDays )
   {
      m_nDaysHorizon = nDays;
   }
   public void Run( Stocks s ) throws SQLException
   {
      Run( s, m_dStart, m_dEnd, m_nDaysHorizon );
   }

   private void Run( Stocks s, Date dStart, Date dEnd, int nHorizonDays ) throws SQLException
   {
      m_nDaysHorizon = nHorizonDays;
      // for each stock, compute a grade
      String[] straTickers = s.GetTickerList();
      int nSize = straTickers.length;
      m_qaList = new Quotes[ nSize ];
      DoRun( straTickers, dStart,dEnd, nHorizonDays, m_qaList );
   }


   protected void DoRun( String[ ] stra, Date dStart, Date dEnd, int nHorizonDays, Quotes[ ] qa ) throws SQLException
   {
      // fetch data from database
      int nSize = stra.length;
      for( int i = 0 ; i < nSize ; i++ )
      {
         String strTicker = stra[ i ];
         Quotes q = new Quotes( );
         Grade g = CreateGrade( );
         Date dRealStart = g.GetDataAcquisitionStartDate( dStart, dEnd );
         q.Acquire( strTicker, dRealStart, dEnd);
         g.ComputeGrade(  q, strTicker, dStart, dEnd );
         q.SetGrade( g );
         q.Release();
         qa[ i ] = q;
      }
   }

   abstract Grade CreateGrade( );

   public void DisplayResult( )
   {
      // for now
      // if (m_qaList.length == 0)
      // return;

      String strSep = ";";
      Arrays.sort( m_qaList );
      StringBuffer sbH = new StringBuffer( "Ticker" + strSep + "Name" + strSep );
      m_qaList[ 0 ].GetGrade( ).AppendHeader( sbH );
      System.out.println( sbH );
      for( int i = 0 ; i < m_qaList.length ; i++ )
      {
         StringBuffer sb = new StringBuffer( );
         sb.append( m_qaList[ i ].GetTicker() );
         sb.append( strSep );
         sb.append( m_qaList[ i ].GetName() );
         sb.append( strSep );
         m_qaList[ i ].GetGrade( ).AppendRecord(sb);

         System.out.println( sb.toString( ) );
      }
   }
}
