package org.drb.porto.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;


public class RectifyDB
{
   private float m_fFactor = Float.NaN;
   private String m_strTicker;
   private Date m_dDate;
   
   public void SetNewDate( Date d )
   {
      m_dDate = d;
   }
   
   public void SetFactor( float f )
   {
      m_fFactor = f;
   }
   
   public void SetTicker( String strTicker )
   {
      m_strTicker = strTicker;
   }
   
   public void DoRectify( )
   {
      if( Float.isNaN( m_fFactor ) )
         throw new IllegalArgumentException( "Must set factor" );
      if( m_strTicker == null )
         throw new IllegalArgumentException( "Must set ticker" );
      if( m_dDate == null )
         throw new IllegalArgumentException( "Must set date" );
         
      DBAccess dba = new DBAccess( );
      try
      {
         dba.Connect( );
         if( dba.IsConnected( ) )
         {
            PreparedStatement ps = dba.PrepareRectifyTicker( m_fFactor, m_dDate, m_strTicker );
            ps.execute();
         }
         dba.Disconnect();
         dba = null;
      }
      catch( SQLException e )
      {
         System.err.println( "Cannot execute sql statement:" + e );
      }
   }   
}
