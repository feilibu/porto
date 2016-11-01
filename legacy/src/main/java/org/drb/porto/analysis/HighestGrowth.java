package org.drb.porto.analysis;

import java.util.Date;

import org.drb.porto.base.Grade;
import org.drb.porto.base.Quotes;


public class HighestGrowth extends FindOptimal
{
   private int m_nDaysCount;
   
   HighestGrowth( int nDays )
   {
      m_nDaysCount = nDays;
   }
   
   public Grade CreateGrade( )
   {
      return new GrowthGrade( m_nDaysCount );
   }
   
   static private class GrowthGrade extends Grade
   {
      private double m_dGrowth;
      private int m_nDaysCount;
      GrowthGrade( int nDaysCount )
      {
         m_nDaysCount = nDaysCount;
      }
      
      public void AppendHeader( StringBuffer sb )
      {
         sb.append( "Growth" );  
      }
      public void AppendRecord( StringBuffer sb )
      {
         sb.append( m_dGrowth );
      }
      
      public double ComputeSyntheticGrade( )
      {
         return m_dGrowth;
      }
      
      /**
       *   Returns the growth in %
       */
      public void ComputeGrade( Quotes q, String strTicker, Date dStart, Date dEnd )
      {
         m_dGrowth = Float.MIN_VALUE;
         double[] daValues = q.GetValues( Quotes.HIGH );
            
         int n = daValues.length;
         // 
         if( n >= (m_nDaysCount * 0.7) )
         {
            m_dGrowth = (daValues[n-1] - daValues[ 0 ]) / daValues[ 0 ];
         }
      }
   }
}
