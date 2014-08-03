package org.drb.porto.analysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.drb.porto.base.Grade;
import org.drb.porto.base.Quotes;
import org.drb.porto.utils.Stats;

public class SelectStocks extends FindOptimal
{

   public SelectStocks( )
   {}

   @Override
   Grade CreateGrade()
   {
      return new SimplePhase2Grade( );
   }

   private static class SimplePhase2Grade extends Grade
   {
      private int m_nWeeklyMobAvg = 30;
      private int m_nMonthlyMobAvg = 10;

      private int m_nNbWeeksAbove = 5;
      private int m_nNbMonthsAbove = 2;

      private boolean m_bIsAboveWeekly;
      private boolean m_bIsAboveMonthly;
      private boolean m_bIsGrowingWeekly;
      private boolean m_bIsGrowingMonthly;
      private boolean m_bIsAboveHistoricalHigh;
      private double variance;
      private boolean m_bOk;

      SimplePhase2Grade( )
      {

      }

      @Override
      public void AppendHeader(StringBuffer sb)
      {
         sb.append( "Is above (weekly)");
         sb.append( "\t");
         sb.append( "Is above (monthly)");
         sb.append( "\t");
         sb.append( "Is growing (weekly)");
         sb.append( "\t");
         sb.append( "Is growing (monthly)");
         sb.append( "\t");
         sb.append( "Is historical high");
         sb.append( "\t");
         sb.append("Volatility");
         sb.append("\t");
      }

      @Override
      public void AppendRecord(StringBuffer sb)
      {
         sb.append( m_bIsAboveWeekly );
         sb.append( "\t");
         sb.append(  m_bIsAboveMonthly );
         sb.append( "\t");
         sb.append( m_bIsGrowingWeekly );
         sb.append( "\t");
         sb.append(  m_bIsGrowingMonthly );
         sb.append( "\t");
         sb.append(  m_bIsAboveHistoricalHigh );
         sb.append("\t");
         sb.append("" + variance);
      }

      @Override
      public void ComputeGrade(Quotes q, String strTicker, Date dStart, Date dEnd)
      {
         m_bOk = false;
         try
         {
            double[ ] daWeek = ConvertToWeek( q, dStart, dEnd, Quotes.HIGH );
            double[ ] daMonth = ConvertToMonth( q, dStart, dEnd, Quotes.HIGH );

            double[ ] daMobAvgWeekly = Stats.ComputeMobileAvg( daWeek, m_nWeeklyMobAvg );
            double[ ] daMobAvgMonthly = Stats.ComputeMobileAvg( daMonth, m_nMonthlyMobAvg );

            m_bIsAboveWeekly =  IsAbove( daWeek, daMobAvgWeekly, m_nNbWeeksAbove );
            m_bIsAboveMonthly =  IsAbove( daMonth, daMobAvgMonthly, m_nNbMonthsAbove );
            m_bIsGrowingWeekly =  IsGrowing(  daMobAvgWeekly );
            m_bIsGrowingMonthly =  IsGrowing( daMobAvgMonthly );
            m_bIsAboveHistoricalHigh = IsAboveHistoricalHigh( daWeek, m_nNbWeeksAbove * 2 );
            variance = computeVolatilityIndex(q, dStart, dEnd, Quotes.HIGH);
            m_bOk = true;
         }
         catch( AnalysisException e )
         {
            throw new RuntimeException("Cannot evaluate grades", e);
         }
      }

      private double computeVolatilityIndex(Quotes q, Date start, Date end, int high) throws AnalysisException
      {
         double[] values = getQuoteValues(q, start, end, high);
         double[] returns = computeReturns(values);
         StandardDeviation stddev = new StandardDeviation();
         return stddev.evaluate(returns);
      }

      private double[] computeReturns(double[] values)
      {
         if (values.length == 0)
            return new double[0];
         double[] ret = new double[values.length - 1];
         for (int i = 0; i < ret.length; i++)
         {
            ret[i] = values[i + 1] / values[i];
         }
         return ret;
      }

      private boolean IsAboveHistoricalHigh( double[ ] daWeek, int nGracePeriod ) throws AnalysisException
      {
         if( daWeek.length < nGracePeriod )
            throw new AnalysisException( "Observation period should be larger than grace period" );

         // take the max of the grace period
         double dMax = 0;
         for( int i = daWeek.length - nGracePeriod ; i < daWeek.length ; i++ )
            dMax = Math.max( dMax, daWeek[ i ] );

         // return true if this is higher than all values before the grace period
         for( int i = 0 ; i < daWeek.length - nGracePeriod ; i++ )
            if( dMax < daWeek[ i ] )
               return false;
         return true;
      }

      private class QuotesIterator
      {
         private Quotes quotes;
         private Date end;
         private Calendar cal;

         QuotesIterator(Quotes q, Date start, Date end)
         {
            quotes = q;
            this.end = end;
            cal = Calendar.getInstance();
            cal.setTime(start);
            checkValid(cal, start, end);
         }

         private void checkValid(Calendar cal, Date start, Date end)
         {
            if (cal.getTime().getTime() >= end.getTime())
            {
               throw new IllegalArgumentException("Start date;" + start + ";is after end date;" + end);
            }
         }

         boolean hasNext()
         {
            return cal.getTime().getTime() < end.getTime();
         }

         void next()
         {
            cal.add(Calendar.DATE, 1);
         }

         int getCurrentIndex()
         {
            Date d = cal.getTime( );
            int n = quotes.GetIndexForDate(d);
            return n;
         }

         Calendar getCurrentCal()
         {
            return cal;
         }
      }

      private double[] getQuoteValues(Quotes q, Date start, Date end, int quoteType) throws AnalysisException
      {
         QuotesIterator qi = new QuotesIterator(q, start, end);
         List<Double> al = new ArrayList<Double>();

         while (qi.hasNext())
         {
            int nIndex = qi.getCurrentIndex();
            // this date is a weekday, for which we have a quote
            if (nIndex != -1)
            {
               double d = getQuoteValue(q, quoteType, nIndex);
               al.add(d);
            }
            qi.next();
         }
         return listToArray(al);
      }

      private double[] Aggregate(int nCalFlag, Quotes q, Date dStart, Date dEnd, int nQuoteType) throws AnalysisException
      {
         QuotesIterator qi = new QuotesIterator(q, dStart,dEnd);
         HashMap<Integer,Integer> hmTimeKeyToIndex = new HashMap<Integer,Integer>( );
         List<Double> al = new ArrayList<Double>();

         while (qi.hasNext())
         {
            int nIndex = qi.getCurrentIndex();
            Calendar c = qi.getCurrentCal();
            // this date is a weekday, for which we have a quote
            if( nIndex != -1 )
            {
               int nKey = getKeyForDate(nCalFlag, c);
               int iIndex = getPrecValueIndex(al, hmTimeKeyToIndex, nKey);
               double dPrecValue = al.get(iIndex);
               double dNewValue;
               dNewValue = getQuoteValue(q, nQuoteType, nIndex);
               if (!Double.isNaN(dPrecValue))
                  dNewValue = Math.max(dPrecValue, dNewValue);
               al.set( iIndex, dNewValue );
            }
            qi.next();
         }
         return listToArray(al);
      }

      private double[] listToArray(List<Double> al)
      {
         double[] daArray;
         daArray = new double[ al.size( ) ];
         for( int i = 0 ; i < daArray.length ; i++ )
            daArray[ i ] = al.get( i );
         return daArray;
      }

      private int getKeyForDate(int nCalFlag, Calendar c)
      {
         // get "key" (month or week)
         int nNumberWithinYear = c.get( nCalFlag );
         int nYear = c.get( Calendar.YEAR );
         int nKey = nYear * 100 + nNumberWithinYear;
         return nKey;
      }

      private int getPrecValueIndex(List<Double> al, Map<Integer, Integer> hmTimeKeyToIndex, int nKey)
      {
         Integer iIndex = hmTimeKeyToIndex.get(nKey);
         if (iIndex == null)
         {
            iIndex = al.size();
            hmTimeKeyToIndex.put(nKey, iIndex);
            al.add(Double.NaN);
         }
         return iIndex;
      }

      private double getQuoteValue(Quotes q, int nQuoteType, int nIndex) throws AnalysisException
      {
         double dNewValue = Double.NaN;
         switch (nQuoteType)
         {
         case Quotes.HIGH:
            dNewValue = q.GetValues(Quotes.HIGH)[nIndex];
            break;
         case Quotes.LOW:
            dNewValue = q.GetValues(Quotes.LOW)[nIndex];
            break;
         default:
            throw new AnalysisException("Only supported: HIGH and LOW");
         }
         return dNewValue;
      }

      private double[ ] ConvertToWeek( Quotes q, Date dStart, Date dEnd, int nQuoteType ) throws AnalysisException
      {
         return Aggregate( Calendar.WEEK_OF_YEAR, q, dStart, dEnd, nQuoteType );
      }

      private double[ ] ConvertToMonth( Quotes q, Date dStart, Date dEnd, int nQuoteType )  throws AnalysisException
      {
         return Aggregate( Calendar.MONTH, q, dStart, dEnd, nQuoteType );
      }

      private boolean IsAbove( double[ ] daValues, double[ ] daMobAvg, int nNbPeriod ) throws AnalysisException
      {
         if( daMobAvg.length < nNbPeriod )
            throw new AnalysisException( "Period size must be smaller than mobile average array size" );
         if( daMobAvg.length < daValues.length )
            throw new AnalysisException( "Values array size must be larger than mobile average array size" );
         for( int i = daMobAvg.length - nNbPeriod ; i < daMobAvg.length ; i++ )
         {
            // consider that both arrays END at the same time
            // => index in daValues is daValues.length - (daMobAvg.length - i)
            int j =  daValues.length - daMobAvg.length + i;
            if( daValues[ j ] < daMobAvg[ i ] )
               return false;
         }
         return true;
      }

      private boolean IsGrowing( double[ ] daMobAvg ) throws AnalysisException
      {
         if( daMobAvg.length < 2 )
            throw new AnalysisException( "Cannot evaluate slope for array of size < 2 ");
         return daMobAvg[ daMobAvg.length - 1 ] > daMobAvg[ daMobAvg.length - 2 ];
      }


      @Override
      public double ComputeSyntheticGrade()
      {
         if( m_bOk && m_bIsAboveWeekly && m_bIsAboveMonthly && m_bIsGrowingWeekly &&
               m_bIsGrowingMonthly && m_bIsAboveHistoricalHigh )
            return 1 / variance;
         else
            return 0;
      }
   }

   public void close()
   {
   }
}

