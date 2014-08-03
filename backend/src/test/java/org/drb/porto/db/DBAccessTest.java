package org.drb.porto.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drb.porto.base.Quotes;

import junit.framework.TestCase;


public class DBAccessTest extends TestCase
{

   public void testGetOneStockQuotesFromDatabase( )
   {
      Quotes q = new Quotes( );
      Date dStart = null, dEnd = null;
      SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd");
      String strStock = "AGF.PA";

      try
      {
         dStart = df.parse( "20070101" );
         dEnd = new Date( ); // df.parse( "20061231" );
      }
      catch( Exception e )
      {
         assert false;   
      }
      try
      {
         q.Acquire( strStock, dStart, dEnd );
      }
      catch( SQLException e )
      {
         System.err.println( "SQLException while acquiring quotes: " + e );
      }
      
      double[ ] faValues = q.GetValues( Quotes.CLOSE );
      Date[ ] daDates = q.GetDates( );
      
      System.out.println( "Stock: " + strStock );
      for( int i = 0 ; i < faValues.length ; i++ )
      {
         System.out.println( daDates[ i ] + "\t" + faValues[ i ] );
      }
      
   }
   
   public void testExecuteSelect( ) throws SQLException
   {
      try
      {
         DBAccess db = new DBAccess( );
         db.Connect( );
         String strStatement = "select * from quotes limit 10";
         Statement aStatement = db.CreateStatement( );
         ResultSet rs = aStatement.executeQuery( strStatement );
         ResultSetMetaData rsm = rs.getMetaData();

         while( rs.next() )
         {
            for( int i = 0 ; i < rsm.getColumnCount() ; i++ )
            {
               System.out.print( rs.getString( i + 1 ) + ", " );   
            }
            System.out.println( );
         }
         db.Disconnect( );
      }
      catch( SQLException e )
      {
         System.err.println( e );
      }
   }
   
   public void testExecuteInsert( ) throws SQLException
   {
      DBAccess dbAccess = new DBAccess( );
      dbAccess.Connect( );
      if(dbAccess.IsConnected() )
      {
         PreparedStatement s = dbAccess.PrepareInsert( );
         dbAccess.InsertRow( s, "toto", 42, 43, 44, 45, 123456, new Date( ) );
         dbAccess.InsertRow( s, "titi", 52, 53, 54, 55, 523456, new Date( ) );
         dbAccess.InsertRow( s, "tutu", 62, 63, 64, 65, 623456, new Date( ) );
         dbAccess.EndInsert( s );
         dbAccess.Disconnect();
      }
   }
   
   public void testGetAllKeys( ) throws SQLException
   {
      DBAccess dbAccess = new DBAccess( );
      dbAccess.Connect( );
      ResultSet rs = dbAccess.GetAllKeys( );
      System.err.println( "Ticker\tDay" );
      int n = 0;
      while( rs.next( ) )
      {
         String strTicker = rs.getString( 1 );            
         Date dDay = rs.getDate( 2 );
         if( n < 10 )
            System.err.println( strTicker + "\t" + dDay );
         n++;
      }
      rs.close();
      dbAccess.Disconnect();
   }
   
   
}
