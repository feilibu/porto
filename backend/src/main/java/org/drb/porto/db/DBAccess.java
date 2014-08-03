package org.drb.porto.db;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;


/*
 * Wraps the database access
 */
public class DBAccess
{
   private boolean m_bConnected;
   private Connection m_theConnection;
   
   
   
   public DBAccess( )
   {
   }
   
   private void LogError( String strMsg )
   {
      System.err.println( strMsg );
   }
   
   public void Connect( )
   {
      boolean bOK = true;
      
       
      String strDriver = "com.mysql.jdbc.Driver";
      String strURL = "jdbc:mysql://localhost/porto";
      String strUser = "nims";
      String strPassword = "nims";
         
      try 
      {
         Class.forName( strDriver );
      } 
      catch(ClassNotFoundException e) 
      {
         LogError( "Cannot instantiate driver " + strDriver + ": " + e );
         bOK = false;
      }
      
      Connection aConnection = null;
      
      if( bOK )
      {
         try 
         {
            aConnection =  DriverManager.getConnection(strURL,strUser,strPassword);
         } 
         catch (SQLException e) 
         {
            LogError( "Cannot connect to URL " + strURL + ":" + e );
            bOK = false;
         }
      }
      m_bConnected = bOK;
      m_theConnection = aConnection;
   }
   
   public boolean IsConnected( )
   {
      return m_bConnected;
   }
   
   PreparedStatement PrepareInsert( ) throws SQLException
   {
      String strSQL = "insert into quotes( symbol, name, low, high, open, close, volume, day )";
      strSQL += "values ( ?,?,?,?,?,?,?,? )";
      
      return m_theConnection.prepareStatement( strSQL );
   }

   public PreparedStatement PrepareInsertStocks( ) throws SQLException
   {
      String strSQL = "insert into stocks ( isin, symbol, name )";
      strSQL += "values ( ?,?,? )";
      
      return m_theConnection.prepareStatement( strSQL );
   }


   public PreparedStatement PrepareUpdateStocks( ) throws SQLException
   {
      String strSQL = "update stocks set symbol = ?, name= ? where isin = ?";
      
      return m_theConnection.prepareStatement( strSQL );
   }
   
   public PreparedStatement PrepareRectifyTicker( float fFactor, Date dStartNew, String strSymbol) throws SQLException
   {
      String strSQL = "update quotes set " + 
         "high = high * " + fFactor + 
         ", low = low * " + fFactor + 
         ", open = open * " + fFactor + 
         ", close = close * " + fFactor +
         " where symbol = ? and day < ?" ;
      
      
      PreparedStatement ps = m_theConnection.prepareStatement( strSQL );
      ps.setString( 1, strSymbol );
      ps.setDate( 2, new java.sql.Date( dStartNew.getTime( ) ));
      return ps;
   }

   
   boolean InsertRow( PreparedStatement s, String strTicker, double dLow, double dHigh, double dOpen, double dClose, int nVolume, Date dDay  ) throws SQLException
   {
      int n = 1;
      s.setString( n++, strTicker );
      s.setString( n++, strTicker );
      s.setDouble( n++, dLow );
      s.setDouble( n++, dHigh );
      s.setDouble( n++, dOpen );
      s.setDouble( n++, dClose );
      s.setInt( n++, nVolume );
      s.setDate( n++, new java.sql.Date( dDay.getTime( ) ) );
      return s.execute();
   }
   
   
   void EndInsert( PreparedStatement s ) throws SQLException
   {
      s.close( );
   }
   
   
   public void Disconnect( )
   {
      if( IsConnected( ) )
      {
         try
         {
            m_theConnection.close( );
            m_theConnection = null;
            m_bConnected = false;           
         }
         catch( SQLException e )
         {
            LogError( "Cannot close connection:" + e );
         }
      }
   }
   
   Statement CreateStatement( ) throws SQLException
   {
      return m_theConnection.createStatement();
   }
   
   
   public ResultSet ExecuteSQL( String strSQL ) throws SQLException
   {
      Statement s = m_theConnection.createStatement();
      ResultSet rs = s.executeQuery( strSQL );
      return rs;
   }
   
   ResultSet GetAllKeys( ) throws SQLException
   {
      return ExecuteSQL( "select symbol, day from quotes" );
   }
   
   public Date GetMaxDate( ) throws SQLException
   {
      assert m_theConnection != null ;
      ResultSet rs = ExecuteSQL( "select max( day ) from quotes" );
      Date dRet = null;
      while( rs.next( ) )
      {
         java.sql.Date d = rs.getDate( 1 );
         dRet = new Date( d.getTime() );
      }
      rs.close( );
      return dRet;
   }
   
   
   
}
