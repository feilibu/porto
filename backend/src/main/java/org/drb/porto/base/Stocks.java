package org.drb.porto.base;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drb.porto.db.DBAccess;


public class Stocks
{
   private String[] m_straTickers;
   private String[] m_straNames;
   
   private String m_strSelectionName;
   
   private HashMap<String,Integer>  m_hTickers;

   public final static String NO_SELECTION = "all";
   
   public Stocks( )
   {  
      this( "current" );
   }

   public Stocks( String strSelectionName )
   {
      if( strSelectionName != null )
         m_strSelectionName = strSelectionName;
      else
      {
         m_strSelectionName = "current";
      }     
   }
   
   /*
    * Returns the ticker for the first match of this "strName"
    */
   public String LookupName( String strName )
   {
      if( m_hTickers == null )
         DoGetTickerList();
      if( null != m_hTickers.get( strName ) )
         return strName;
      // look for a loose match
      Pattern p = Pattern.compile( strName.toUpperCase() );
      for( int i = 0 ; i < m_straNames.length ; i++ )
      {
         String strUpper = m_straNames[ i ].toUpperCase( );
         Matcher m = p.matcher( strUpper );
         if( m.lookingAt() )
            return m_straTickers[ i ];      
      }
      return "";      
   }
   
   
   
   public String[] GetTickerList( )
   {
      if( m_straTickers == null )
         DoGetTickerList( );
      return m_straTickers;
   }
   
   private void DoGetTickerList( )
   {
      // make the array of symbols 
      ArrayList<String> vTickers = new ArrayList<String>( );
      ArrayList<String> vNames = new ArrayList<String>( );
      m_hTickers = new HashMap<String,Integer>( );
      StockEntry[ ] sea = GetAllStockEntries( m_strSelectionName );
      int n = 0;
      for( StockEntry se : sea )
      {
         String strTicker = se.GetTicker() ;
         String strName = se.GetLabel();
         vTickers.add( strTicker );
         vNames.add( strName );
         m_hTickers.put( strTicker, new Integer( n ) );
         n++;
      }
      m_straTickers = vTickers.toArray( new String[ 0 ] );
      m_straNames = vNames.toArray( new String[ 0 ] );
   }

   
   public static void UpdateStockEntries( ArrayList<StockEntry> alAdd, ArrayList<StockEntry> alUpdate, ArrayList<String> alDelete )
   {
      DBAccess dba = new DBAccess( );
      dba.Connect();
      if( ! dba.IsConnected() )
         throw new RuntimeException( "Cannot connect to database" );
      try
      {
         // add
         int nAdd = 0;
         PreparedStatement psAdd = dba.PrepareInsertStocks( );
         for( StockEntry se : alAdd )
         {
            psAdd.clearParameters();
            psAdd.setString( 1, se.GetIsin() );
            psAdd.setString( 2, se.GetTicker() );
            psAdd.setString( 3, se.GetLabel() );
            psAdd.execute();
            nAdd++;
         }
         psAdd.close( );
         
         // modify
         int nUpdate = 0;
         PreparedStatement psUpdate = dba.PrepareUpdateStocks( );
         for( StockEntry se : alUpdate )
         {
            psUpdate.setString( 1, se.GetTicker() );
            psUpdate.setString( 2, se.GetLabel() );
            psUpdate.setString( 3, se.GetIsin() );
            psUpdate.execute( );
            nUpdate++;
         }
         psUpdate.close( );
         System.out.println( "" + nAdd + " added;" + nUpdate + ";updated" );
      }
      catch( SQLException e )
      {
         System.out.println( "Cannot execute SQL statement" + e );
      }
      
   }
   
   public static StockEntry[ ] GetAllStockEntries( )
   {
      return GetAllStockEntries( NO_SELECTION );
   }

   public static StockEntry[ ] GetAllStockEntries( String strSelection )
   {
      ArrayList<StockEntry> al = new ArrayList<StockEntry>( );
      DBAccess dba = new DBAccess( );
      dba.Connect();
      if( ! dba.IsConnected() )
         throw new RuntimeException( "Cannot connect to database" );
      String strSQL = "select symbol,stocks.name,isin from stocks ";
      if( ! NO_SELECTION.equals( strSelection ) )
      {
         strSQL += ",selection_name,selection ";
         strSQL += " where stocks.symbol = selection.ticker and ";
         strSQL += "  selection.idf_selection_name = selection_name.id and ";
         strSQL += "  selection_name.name = '" + strSelection + "'";
      }
      strSQL += " order by name";
      try
      {
         ResultSet rs = dba.ExecuteSQL(strSQL);
         while( rs.next( ) )
         {
            al.add( new StockEntry( rs.getString( 1 ), rs.getString( 2 ), rs.getString( 3 ) ) );
         }
         rs.close( );
      }
      catch( SQLException e )
      {
         System.err.println( "While fetching symbols : " + e );
      }
      return al.toArray( new StockEntry[ 0 ] );
   }
   
   public String GetNextStock( String str )
   {
      if( m_straTickers == null )
         DoGetTickerList( );
      Integer i = (Integer) m_hTickers.get( str );
      
      String strRet = str;
      if( i != null )
         if( i.intValue( ) < m_straTickers.length - 1 )
         return m_straTickers[ i.intValue( ) + 1 ];
      return strRet;
   }
   
   public String GetPreviousStock( String str )
   {
      if( m_straTickers == null )
         DoGetTickerList( );
      Integer i = (Integer) m_hTickers.get( str );
      
      String strRet = str;
      if( i != null )
         if( i.intValue( ) > 0 )
         return m_straTickers[ i.intValue( ) - 1 ];
      return strRet;
   }
   
   
}
