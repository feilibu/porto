package org.drb.porto.analysis;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.drb.porto.base.Grade;
import org.drb.porto.base.Quotes;
import org.drb.porto.utils.Stats;


class WeinsteinGrade extends Grade
{
   private double m_dTrend;
   private double m_dVolIncrease;
   private double m_dResistanceMalus;
   private double m_dRelativePrice;
   
   // look for start of phase 2 for 60 days
   private int m_nNbDaysAboveMA = 60;
   // confirm phase 1 for the previous 180 days
   private int m_nNbDaysBeforePhase2 = 180;
   // mobile average parameter
   private int m_nMAvgDays = 30 * 7;
   // averages the volumes on that many days to detect volume increase 
   private int m_nVolumeAvgRange = 4 * 7;
   // volumes must be 2 times superior to their past average
   private int m_nVolumeFactor = 4;  

   private Quotes m_qRefIndice;
   

   
   WeinsteinGrade( )
   {
      
   }
   
   public void AppendHeader( StringBuffer sb )
   {
     sb.append( "Trend" );
     sb.append( GetSeparator( ) );
     sb.append( "Vol. Increase" );
     sb.append( GetSeparator( ) );
     sb.append( "Res. Malus" );
     sb.append( GetSeparator( ) );
     sb.append( "Rel. Price" );   
   }
   
   public void AppendRecord( StringBuffer sb )
   {
      sb.append( m_dTrend );
      sb.append( GetSeparator( ) );
      sb.append( m_dVolIncrease );
      sb.append( GetSeparator( ) );
      sb.append( m_dResistanceMalus );
      sb.append( GetSeparator( ) );
      sb.append( m_dRelativePrice );
   }

   // use only dEnd (ignores dStart and nHorizonDays)
   public Date GetDataAcquisitionStartDate( Date dStart, Date dEnd )
   {
      // compute dates
      Calendar c = Calendar.getInstance();
      c.setTime( dEnd );
      c.add( Calendar.DATE, -(m_nNbDaysAboveMA + m_nNbDaysBeforePhase2 + m_nMAvgDays ) );
      Date dMAvgStart = c.getTime( );
      return dMAvgStart;
   }

   
   void SetTrend( double d )
   {
      m_dTrend = d;
   }
   
   void SetResistanceMalus( double d )
   {
      m_dResistanceMalus = d;
   }
   
   void SetVolumeIncrease( double d )
   {
      m_dVolIncrease = d;
   }
   
   public double ComputeSyntheticGrade( )
   {
      if( m_dResistanceMalus != 0 )
         return m_dRelativePrice * m_dVolIncrease / m_dResistanceMalus;
      return Double.MIN_VALUE;
   }
   
   void SetRelativePrice( double d )
   {
      m_dRelativePrice = d;
   }
   

   public void ComputeGrade( Quotes q, String strTicker, Date dStart, Date dEnd )
   {
      Date [ ] daDays = q.GetDates();
      if( daDays.length > 0 )
      {
         double[ ] daCloseValues = q.GetValues( Quotes.CLOSE );
         double[ ] daMAvg = Stats.ComputeMobileAvg(daCloseValues, m_nMAvgDays );

         // detection of phase 2 start (crossing of MA)
         int nCrossingIndex = ComputeMAvgCrossingIndex( q, daCloseValues, daMAvg );

         if( nCrossingIndex > -1 )
         {
            // detection of volumes increase
            double dVolIncr = DetectVolumeIncrease( q, nCrossingIndex );
            if( dVolIncr > 0 )
            {
               double dTrend = ComputeMAvgTrend( daMAvg ); 
               double[ ] daRelPrice = ComputeMansfieldRelativePrice(q);

               double dMalus = ComputeResistanceMalus( daCloseValues );

               SetRelativePrice( daRelPrice[ daRelPrice.length -1 ] );
               SetResistanceMalus( dMalus );
               SetTrend( dTrend );
               SetVolumeIncrease( dVolIncr );

            }            
         }
      }
   }
   private double ComputeResistanceMalus( double[ ] daCloseValues )
   {
      int nSize = daCloseValues.length;
      double dLast = daCloseValues[ nSize - 1 ] * 1.1;
      double dMax = dLast;
      int nIndexLatestRes = -1;
      
      for( int i = daCloseValues.length - 2 ; i>= 0 ; i-- )
      {
         if( daCloseValues[ i ] > dLast  )
         {
            if( nIndexLatestRes == -1 )
               nIndexLatestRes = i;
            dMax = daCloseValues[ i ];     
         }
      }
      double dRes = 1;
      if( nIndexLatestRes > -1 )
      {
         // malus increases if resistance is recent
         dRes /= (nIndexLatestRes / nSize);
         // malus increases with resistance relative value
         dRes *= dMax / daCloseValues[ nSize - 1 ]; 
      }
      return dRes;
   }
   
   private double ComputeMAvgTrend( double[ ] daMAvg )
   {
      int nSize = daMAvg.length;
      // computes the trend over the past 4 weeks
      return (daMAvg[ nSize - 5 * 4 ] - daMAvg[ nSize - 1 ]) / daMAvg[ nSize - 1 ];
   }

   private double DetectVolumeIncrease( Quotes q, int nCrossingIndex )
   {
      // detection of volume increase in the area
      //  1- computes mob avg of the volumes over 5 weeks
      Date[ ] daDates = q.GetDates();
      int [] naVolumes = q.GetVolumes();
      double[] daVolumes = IntToDouble( naVolumes );
      double[] daVolAvg = Stats.ComputeMobileAvg(daVolumes, m_nVolumeAvgRange );
      // 2- finds volumes = 2 times mob avg in a radius of 5 weeks around
      // the crossing point
      int nStart = nCrossingIndex - m_nVolumeAvgRange;
      if( nStart <= 0 )
         nStart = 0;
      int nEnd = nCrossingIndex + m_nVolumeAvgRange;
      if( nEnd > daDates.length - 1 )
         nEnd = daDates.length - 1;
      double dMax = 0;
      int nMaxIndex = -1;
      // finds max
      for( int i = nEnd ; i >= nStart ; i-- )
      {
         double dDiff = daVolumes[ i ] - daVolAvg[ i ]; 
         if( dMax < dDiff )
         {
            dMax = dDiff;
            nMaxIndex = i;
         }
      }
      //System.out.println( q.GetName() + ":Mob avg crossed on the " + daDates[ nCrossingIndex ] );

      if( nMaxIndex > -1 )
      {
         // make sure max is > 2 * avg
         if( daVolumes[ nMaxIndex ] > daVolAvg[ nMaxIndex ] * m_nVolumeFactor )
         {
            //System.out.println( "\t=> " + q.GetName( ) + ":High volumes on the " + daDates[ nMaxIndex ] + ":" + daVolumes[ nMaxIndex ] + ":" + daVolAvg[ nMaxIndex ] );
         }
         
         return daVolumes[ nMaxIndex ] / daVolAvg[ nMaxIndex ];
      }
      return -1;      
   }
   
   
   private int ComputeMAvgCrossingIndex( Quotes q, double[ ] daCloseValues, double[ ] daMAvg )
   {
      int nCrossingIndex = -1;
      
      // computes indexes for phase 1 and phase 2
      Date[ ] daDates = q.GetDates();
      int nSize = daDates.length; 
      if( nSize == 0 )
      {
         System.err.println( "Warning: " + q.GetName() + " does not have enough quotes info" );
      }
      else
      {
         // stock MUST be above MAvg
         if( daCloseValues[ nSize - 1 ] > daMAvg[ nSize - 1 ] )
         {
            int nIndex = nSize - 1;
            while( nIndex >= 0 && daCloseValues[ nIndex ] > daMAvg[ nIndex ])
            {
               nIndex--;               
            }
            if( nIndex >= 0 )
               nCrossingIndex = nIndex;
         }
      }
      return nCrossingIndex;
   }
   
   private double[ ] ComputeMansfieldRelativePrice( Quotes q )
   {
      Date[ ] daDates = q.GetDates( );
      Date dStart = daDates[ 0 ];
      Date dEnd = daDates[ daDates.length - 1 ];
      if( m_qRefIndice == null )
      {
         m_qRefIndice = new Quotes( );
         try
         {
            m_qRefIndice.Acquire( "^FCHI", dStart, dEnd);
         }
         catch( SQLException e )
         {
            System.err.println( "Error while fetching reference values:" + e );   
         }
      }
      double[ ] daValues = q.GetValues( Quotes.CLOSE );
      double [ ] daReferenceValues = m_qRefIndice.GetValues( Quotes.CLOSE );
      Date[ ] daReferenceDates = m_qRefIndice.GetDates();
      return Stats.ComputeMansfieldRelativePrice(daValues, daDates, daReferenceValues, daReferenceDates);
   }
   
   private double[] IntToDouble( int[] na )
   {
      double[] da = new double[ na.length ];
      for( int i = 0 ; i < na.length ; i++ )
         da[ i ] = na[ i ];
      return da;
   }
   

}
