package org.drb.porto.base;

import java.util.Date;

/**
 * 
 * Abstract class for "Grade". Grades can:
 *   - be sorted on
 *   - print extended information for each record
 *   - print the corresponding header
 *
 */
abstract public class Grade implements Comparable<Grade>
{
   private String m_strSep = ";";
   
   private double m_dSyntheticGrade;
   private boolean m_bComputed;
   
   abstract public void AppendHeader( StringBuffer sb );
   abstract public void AppendRecord( StringBuffer sb );
   abstract public void ComputeGrade( Quotes q, String strTicker, Date dStart, Date dEnd );
   abstract public double ComputeSyntheticGrade( );
   
   public String GetSeparator( )
   {
      return m_strSep;
   }

   public int compareTo( Grade o )
   {
      if( o == this )
         return 0;
      double dOther = o.GetSyntheticGrade( );
      return dOther > GetSyntheticGrade( ) ? 1 : -1;
   }
   
   public Date GetDataAcquisitionStartDate( Date dStart, Date dEnd )
   {
      return dStart;
   }

   double GetSyntheticGrade( )
   {
      if( ! m_bComputed )
      {
         m_dSyntheticGrade = ComputeSyntheticGrade( );
         m_bComputed = true;
      }
      return m_dSyntheticGrade;
   }
}
