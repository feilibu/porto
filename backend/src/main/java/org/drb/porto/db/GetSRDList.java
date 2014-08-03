package org.drb.porto.db;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.drb.porto.base.StockEntry;


/**
 *
 * todo:  update database with contents of Excel sheet
 *
 */
public class GetSRDList
{
   private final static int ISIN_COL = 1;
   private final static int MNEMO_COL = 4;
   private final static int LABEL_COL = 3;




   public HashMap<String, StockEntry> DoGetSRDList() throws IOException
   {
      String strURL = "http://www.euronext.com/fic/000/057/252/572528.xls";

      HashMap<String,StockEntry> hm = new HashMap<String, StockEntry>( );
      InputStream is = null;
      try
      {
         URL url = new URL( strURL );

         URLConnection urlConn = url.openConnection();
         urlConn.setDoInput(true);
         urlConn.setUseCaches(false);

         is = urlConn.getInputStream();
         Workbook wb = Workbook.getWorkbook(is);

         Sheet aSheet = wb.getSheet( 0 );

         int nCol = 2;
         int nRow = 7;
         while( true )
         {
            Cell aCell = aSheet.getCell( nCol, nRow );
            if( aCell.getContents( ).length( ) > 0 )
            {
               StockEntry se = new StockEntry(
                     aSheet.getCell(  MNEMO_COL, nRow ).getContents( ) + ".PA",
                     aSheet.getCell(  LABEL_COL, nRow ).getContents( ),
                     aSheet.getCell(  ISIN_COL, nRow ).getContents( ) );
               hm.put( se.GetIsin(), se );
            }
            else
               break;
            nRow++;
         }
      }
      catch (MalformedURLException mue)
      {
         assert false;
      }
      catch( BiffException be )
      {
         System.err.println( "Cannot load excel sheet " + be );
      }
      finally
      {
         if( is != null )
            is.close( );
      }
      return hm;
   }
}
