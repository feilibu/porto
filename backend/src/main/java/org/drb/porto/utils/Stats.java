package org.drb.porto.utils;
import java.util.Date;
import java.util.HashMap;


public class Stats
{
   public static double ComputeAvg( double[ ] daValues )
   {
      assert daValues != null;
      int nSize = daValues.length;
      assert nSize > 0;
      return ComputeAvg( daValues, 0, nSize );
   }

   public static double ComputeAvg( double[ ] daValues, int nStartIndex, int nSize )
   {
      assert daValues != null;
      assert nSize > 0;
      assert nStartIndex >= 0;
      assert (nStartIndex + nSize) <= daValues.length;
      
      
      double dAvg = 0;
      for( int i = 0 ; i < nSize ; i++ )
      {
         dAvg += daValues[ nStartIndex + i ];
      }
      return dAvg / nSize;
   }
   
   
   /**
    * "N" - standard deviation (stdevp in gnumeric)
    */
   static public double ComputeStdDev( double[ ] daValues )
   {
      assert daValues != null;
      int nSize = daValues.length;
      assert nSize > 0;
      
      double dAvg = ComputeAvg( daValues );
      double dStdDev = 0;
      for( int i = 0 ; i < nSize ; i++ )
      {
         double dDiff = daValues[ i ] - dAvg;
         dStdDev += dDiff * dDiff; 
      }
      return Math.sqrt( dStdDev /nSize ); 
   }
   

   //todo: there is much faster!!   
   static public double[ ] ComputeMobileAvg( double[ ] faValues, int nHorizonDays )
   {
      int nSize = faValues.length;
      double[] daRes = new double[ nSize ];
      for( int i = 0 ; i < nSize ; i++ )
      {
         /*
          * Example with nHorizonDays=3, current index = 1
          * 
          *      Index   Value
          *      0        x0     <- nStartIndex 
          *      1        x1     <- i 
          *      2        x2     
          *      3        x3       
          *      4        x4
          *      5        x5
          */
         int nStartIndex = Math.max( i - nHorizonDays + 1, 0 );
         int nSpan = i - nStartIndex + 1;
         daRes[ i ] = ComputeAvg( faValues, nStartIndex, nSpan );   
      }
      return daRes;
   }
   
   /**
    * 
    * Computes Mansfield's relative price from two couple of arrays (prices, dates)
    * The dates should be over the same period, but small discrepancies are tolerated
    * Within each pair, the arrays must be of same size
    * 
    * The returned result is an array of same size as the values
    * 
    */
   static public double[ ] ComputeMansfieldRelativePrice( double[ ] daValues, Date[ ] daDates, double[ ] daReferenceValues, Date[ ] daReferenceDates )
   {
      HashMap<Date,Double> ht = new HashMap<Date,Double>( );
      for( int i = 0 ; i < daReferenceValues.length ; i++ )
      {
         ht.put( daReferenceDates[ i ], new Double( daReferenceValues[ i ] ) );
      }

      double[ ] daIndice = new double[ daValues.length ];
      for( int i = 0 ; i < daValues.length ; i++ )
      {
         Double d = ht.get( daDates[ i ] );
         if( d != null )
         {
            double dIndice = d.doubleValue(); 
            daIndice[ i ] = dIndice;
         }
         else 
            daIndice[ i ] = Double.NaN;
      }
      
      double dBasePrice = ComputeMansfieldBasePrice( daValues, daIndice );
      
      for( int i = 0 ; i < daValues.length ; i++ )
      {
         daIndice[ i ] = ((daValues[ i ] / daIndice[ i ] / dBasePrice) - 1);
      }
      
      return daIndice;
   }
   
   private static double ComputeMansfieldBasePrice( double[ ] daValues, double[ ] daIndice )
   {
      double dSum = 0;
      int nSize = 0;
      for( int i = 0 ; i < daValues.length; i++ )
      {
         if( ! Double.isNaN( daValues[ i ] ) && ! Double.isNaN( daIndice[ i ] ) )
         {
           dSum += daValues[ i ] / daIndice[ i ];
           nSize++;
         }
      }
      return dSum / nSize;
   }
}
