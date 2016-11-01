package org.drb.porto.base;

public class Series
{
   private double[ ] m_daValues;
   private String m_strName;
   public Series( int nSize )
   {
      m_daValues = new double[ nSize ];
   }
   public Series( double daValues[ ] )
   {
      m_daValues = daValues;
   }
   public double[ ] GetValues( )
   {
      return m_daValues;
   }
   public int GetSize( )
   {
      return m_daValues.length;      
   }
   
   void SetName( String str )
   {
      m_strName = str;
   }
   
   String GetName( )
   {
      return m_strName;
   }
   
}
