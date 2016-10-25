package org.drb.porto.interfaces.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.drb.porto.base.DataException;
import org.drb.porto.base.Quotes;
import org.drb.porto.base.Stocks;
import org.drb.porto.utils.Stats;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.Timeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;


/**
 *  todo
 *    - remove week-ends             -> OK
 *    - add mobile average           -> OK but wrong the first N days
 *    - add support for weekly       -> OK
 *    - add buttons / combo / shortcuts to:
 *       - switch to weekly                 -> OK
 *       - change mobile average parameter  -> OK
 *       - change stock                     -> OK
 *     
 *    - fix ugly layout
 *    - add stock lookup / history
 *       
 */


public class Chart
{
   private int m_nMAvgDays = 30;
   private boolean m_bWeekly = true;
   private JFrame m_theFrame;
   private ChartActions m_caActions;
   private ChartPanel m_cpChartPanel;
   private Date m_dStart;
   private Date m_dEnd;
   private Stocks m_theStocks;
   
   private Quotes m_qRefIndice;
   
   private JLabel m_jlTrend;

   private String m_strLooseName;
   // the selection of tickers to be browsed
   private String m_strSelectionName;
   
   
   
   private class ChartActions implements ActionListener
   {
      @Override
      public void actionPerformed( ActionEvent ae )
      {
         try
         {
            DoActionPerformed( ae );
         }
         catch( DataException e )
         {
            JOptionPane.showMessageDialog( m_theFrame, "Data error:" + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE );
            
         }
      }
      
      private void DoActionPerformed( ActionEvent ae ) throws DataException
      {
         String strCommand =  ae.getActionCommand();
         if( strCommand.equals( "Close") )
         {
            OnClose( );   
         }
         else if( strCommand.equals( "Weekly") )
         {
            SetWeekly( true );   
         }
         else if( strCommand.equals( "Daily") )
         {
            SetWeekly( false );
         }
         else if( strCommand.equals( ">>") )
         {
            OnNextStock( );
         }
         else if( strCommand.equals( "<<") )
         {
            OnPrevStock( );
         }
         else if( ((Component)ae.getSource()).getName( ).equals( "MobileAverage" ) )
         {
            int nMob = Integer.parseInt( strCommand );
            SetMobileAverage( nMob );
         }
         else if( ((Component)ae.getSource()).getName( ).equals( "Stock" ) )
         {
            SetLooseName( strCommand );
            OnUpdateChart( );
         }
         else
         {
            System.err.println( "Unsupported command: " + strCommand );
         }
      }
   }
   
   
   /**
    * 
    * A very simple, single-series XYDataset to support
    * the drawing of the mobile average.
    *
    */
   private static class MyXYDataset extends AbstractXYDataset
   {
      static private final long serialVersionUID = 1L;
      private Date[ ] m_daDates;
      private double[ ] m_daValues;
      
      MyXYDataset( Date[ ] daDates, double[ ] daValues )
      {
         m_daDates = daDates;
         m_daValues = daValues;
      }
      
      @Override
      public int getItemCount( int nSeries )
      {
         return m_daDates.length;
      }
      @Override
      public Number getX( int series, int nItem )
      {
         Number nRet = new Long( m_daDates[ nItem ].getTime( ) );
         return nRet;
      }
      
      @Override
      public Number getY( int series, int nItem )
      {
         double d = Double.NaN;
         if( nItem < m_daValues.length )
            d = m_daValues[ nItem ];
         return new Double( d ); 
      }
      @Override
      public int getSeriesCount( )
      {
         return 1;
      }
      @Override
      public Comparable<String> getSeriesKey( int nSeries )
      {
         return "MySeries";
      }
   }
   
   public Chart( )
   {
      Calendar c = Calendar.getInstance( );
      m_dEnd = c.getTime( );
      c.add( Calendar.DATE, -1000 );
      m_dStart = c.getTime( );
      m_strSelectionName = "current";
   }
   
   void OnClose( )
   {
      m_theFrame.dispose( );
   }
   
   void SetWeekly( boolean bWeekly ) throws DataException
   {
      if( bWeekly != m_bWeekly )
      {
         m_bWeekly = bWeekly;
         OnUpdateChart( );
      }
   }
   
   void SetMobileAverage( int nMobDays ) throws DataException
   {
      if( m_nMAvgDays != nMobDays )
      {
         m_nMAvgDays = nMobDays;
         OnUpdateChart( );
      }
   }
   
   
   void OnNextStock( ) throws DataException
   {
      if( m_theStocks == null )
         m_theStocks = new Stocks( );
      String strNext = m_theStocks.GetNextStock( m_strLooseName );
      SetLooseName(strNext );
      OnUpdateChart( );
   }
   
   void OnPrevStock( ) throws DataException
   {
      if( m_theStocks == null )
         m_theStocks = new Stocks( );
      String strNext = m_theStocks.GetPreviousStock( m_strLooseName );
      SetLooseName( strNext );
      OnUpdateChart( );
   }
   
   void OnUpdateChart( ) throws DataException
   {
      JFreeChart jfc = Plot( );
      m_cpChartPanel.setChart( jfc );
   }
   
   public void CreateFrame( JFreeChart c )
   {
      JFrame aFrame = new JFrame( );
      m_theFrame = aFrame;
      m_theFrame.setTitle( "Selection: " + m_strSelectionName );
      JPanel pnlMain = new JPanel( );
      pnlMain.setLayout( new BorderLayout( ) );
      m_caActions = new ChartActions( );
      JPanel pnlButtons = CreateCommandsPanel( m_caActions );
      ChartPanel aPanel = new ChartPanel( c );
      m_cpChartPanel = aPanel;
      
      
      pnlMain.add( aPanel, BorderLayout.CENTER );
      pnlMain.add( pnlButtons, BorderLayout.EAST );
      aFrame.setContentPane( pnlMain );
      aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      aFrame.pack( );
      aFrame.setVisible( true );
   }
   
   JPanel CreateCommandsPanel( ChartActions ca )
   {
      JPanel aPanel = new JPanel( );
      aPanel.setLayout( new BoxLayout( aPanel, BoxLayout.Y_AXIS ) );
      
      // close button
      JButton jbClose = new JButton( "Close");
      jbClose.addActionListener( ca );
      aPanel.add( jbClose );
      
      // daily/weekly radio buttons
      JRadioButton jrbDaily = new JRadioButton( "Daily");
      jrbDaily.setMnemonic(KeyEvent.VK_D);
      jrbDaily.setActionCommand( "Daily" );
      if( ! m_bWeekly )
         jrbDaily.setSelected( true );
      jrbDaily.addActionListener( ca );
      
      JRadioButton jrbWeekly = new JRadioButton( "Weekly");
      jrbWeekly.setMnemonic(KeyEvent.VK_W);
      jrbWeekly.setActionCommand( "Weekly" );
      jrbWeekly.addActionListener( ca );
      if( m_bWeekly )
         jrbWeekly.setSelected( true );
      
      ButtonGroup aGroup = new ButtonGroup( );
      aGroup.add( jrbDaily );
      aGroup.add( jrbWeekly );
      aPanel.add( jrbDaily );
      aPanel.add( jrbWeekly );
      
      JLabel jl = new JLabel( "Mobile Average");
      aPanel.add( jl );
      JTextField txtMobAvg = new JTextField( "" + m_nMAvgDays );
      txtMobAvg.setName( "MobileAverage");
      aPanel.add( txtMobAvg );
      txtMobAvg.addActionListener( ca );
      
      JButton jNext = new JButton( ">>");
      aPanel.add( jNext );
      jNext.addActionListener( ca );

      JButton jPrev = new JButton( "<<");
      aPanel.add( jPrev );
      jPrev.addActionListener( ca );
      
      
      jl = new JLabel( "Stock");
      aPanel.add( jl );
      JTextField txtStock = new JTextField( m_strLooseName );      
      txtStock.setName( "Stock");
      txtStock.addActionListener( ca );
      aPanel.add( txtStock );
      
      m_jlTrend = new JLabel( "" );
      aPanel.add( m_jlTrend );
      
      
      
      return aPanel;
   }
   
   /**
    *  Sets the Mavg trend (on the latest 4 points = 1 month on a weekly graph)
    *  in % / year 
    */
   void SetTrend( double dTrend )
   {
      if( m_jlTrend != null )
      {
         DecimalFormat nf = new DecimalFormat( "#0.0" );
         String strFmt = nf.format( dTrend );
         m_jlTrend.setText( "Trend " + strFmt + " %/year");
      }
   }
   
   public void SetLooseName( String strLooseName )
   {
      m_strLooseName = strLooseName;
   }
   
   public void SetStartDate( Date dStart )
   {
      m_dStart = dStart;
   }
   
   public void SetEndDate( Date dEnd )
   {
      m_dEnd = dEnd;
   }
   
   
   public void SetSelectionName( String s )
   {
      m_strSelectionName = s;
   }
   
   public JFreeChart Plot(  ) throws DataException
   {
      if( m_theStocks == null )
      {
         m_theStocks = new Stocks( m_strSelectionName );
         String[ ] straList = m_theStocks.GetTickerList();
         if( straList.length == 0 )
            throw new DataException( "No ticker found for selection;" + m_strSelectionName ); 
      }
      if( m_strLooseName == null )
         m_strLooseName = m_theStocks.GetTickerList()[ 0 ];
      
      String strQuote = m_theStocks.LookupName( m_strLooseName );
      m_strLooseName = strQuote;
      
      Quotes q = new Quotes( );
      try
      {
         q.Acquire( strQuote, m_dStart, m_dEnd);
      }
      catch( SQLException e )
      {
         System.err.println( "SQLException: " + e );
      }
            
      OHLCDataItem[ ] theItems = null;
      
      if( m_bWeekly )
         theItems = CreateWeeklyDataItems( q );
      else
         theItems = CreateDailyDataItems( q );

      OHLCDataset theData =  new DefaultOHLCDataset( "Candlesticks", theItems );
      JFreeChart jfc = ChartFactory.createCandlestickChart( q.GetName( ), m_strLooseName, "Price", theData, false );
      ArrangeGraph( q, jfc );
      AddMobileAverage( q, jfc );
      AddReferenceIndice( q, jfc );
      q.Release();
      return jfc;      
   }


   private void AddReferenceIndice( Quotes q, JFreeChart jfc )
   {
      if( m_qRefIndice == null )
      {
         m_qRefIndice = new Quotes(  );
         try
         {
            m_qRefIndice.Acquire( "^FCHI", m_dStart, m_dEnd );
         }
         catch( SQLException e )
         {
            System.err.println( "Caught SQL Exception while fetching reference indice:" + e );
         }         
      }
      
      XYPlot xyp = jfc.getXYPlot();
      Date[ ] daIndiceDate = m_qRefIndice.GetDates();
      double[ ] daIndice = m_qRefIndice.GetValues( Quotes.CLOSE );
      Date[ ] daDates = GetDateValues( xyp.getDataset() );
      double[ ] daValues = GetYValues( xyp.getDataset() );
      
      double [ ] daZeroLine = new double[ daIndice.length ];
      double dAvg = Stats.ComputeAvg(daValues);      
      Arrays.fill( daZeroLine, dAvg );
      daIndice = Stats.ComputeMansfieldRelativePrice(daValues, daDates, daIndice, daIndiceDate );
      
      // normalize to display in the middle of the graph
      for( int i = 0 ; i < daValues.length ; i++ )
      {
         daIndice[ i ] = dAvg + dAvg/10 * daIndice[ i ];
      }
         
      
      MyXYDataset aDataset = new MyXYDataset( daDates, daIndice );
      XYItemRenderer aRenderer = new XYLineAndShapeRenderer( true, false );
      xyp.setDataset( 2, aDataset );      
      xyp.setRenderer( 2, aRenderer );
      
      MyXYDataset zeroLineDataset = new MyXYDataset( daDates, daZeroLine );
      XYItemRenderer zeroLineRenderer = new XYLineAndShapeRenderer( true, false );
      xyp.setDataset( 3, zeroLineDataset );      
      xyp.setRenderer( 3, zeroLineRenderer );     
   }
   
   
   private OHLCDataItem[ ] CreateDailyDataItems( Quotes q )
   {
      Date[ ] daDates = q.GetDates();
      int nSize = daDates.length;
      double daLow[ ] = q.GetValues( Quotes.LOW );
      double daHigh[ ] = q.GetValues( Quotes.HIGH );
      double daOpen[ ] = q.GetValues( Quotes.OPEN );
      double daClose[ ] = q.GetValues( Quotes.CLOSE );
      int naVolume[ ] = q.GetVolumes( );
      OHLCDataItem[ ] theItems = new OHLCDataItem[ nSize ];
      for( int i = 0 ; i < nSize ; i++ )
      {
         theItems[ i ] = new OHLCDataItem( daDates[ i ], 
                                           daOpen[ i ], 
                                           daHigh[ i ], 
                                           daLow[ i ], 
                                           daClose[ i ], 
                                           naVolume[ i ] );
      }
      return theItems;
   }
   
   /**
    * 
    * Computes on the fly the weekly values (low, high, open, close, volume)
    * 
    */
   private OHLCDataItem[ ] CreateWeeklyDataItems( Quotes q )
   {
      Date[ ] daDates = q.GetDates();
      int nSize = daDates.length;
      double daLow[ ] = q.GetValues( Quotes.LOW );
      double daHigh[ ] = q.GetValues( Quotes.HIGH );
      double daOpen[ ] = q.GetValues( Quotes.OPEN );
      double daClose[ ] = q.GetValues( Quotes.CLOSE );
      int naVolume[ ] = q.GetVolumes( );
      Calendar c = Calendar.getInstance();
      Date dPrec = null;
      int nDOWPrec = -1;
      long lVolume = 0;
      double dOpen = -1, dHigh = Double.MIN_VALUE, dLow = Double.MAX_VALUE, dClose = 0;
      ArrayList<OHLCDataItem> vItems = new ArrayList<OHLCDataItem>( );
      
      for( int i = 0 ; i < nSize ; i++ )
      {
         Date d = daDates[ i ];
         c.setTime( d );
         if( dOpen == -1 )
            dOpen = daOpen[ i ];
         dHigh = Math.max( dHigh, daHigh[ i ] );
         dLow = Math.min( dLow, daLow[ i ] );
         dClose = daClose[ i ];
         lVolume += naVolume[ i ];

         int nDOW = c.get( Calendar.DAY_OF_WEEK );
         if( nDOW < nDOWPrec || i == nSize - 1 )
         {
            vItems.add( new OHLCDataItem( dPrec, 
                  dOpen, 
                  dHigh, 
                  dLow, 
                  dClose, 
                  lVolume ) );
            if( i == nSize - 1 )
            {
               System.err.println( dPrec + ", " + dOpen + ", " + dHigh + ","  + dLow  + ", " +  dClose + "/" + lVolume );
            }
            lVolume = 0;
            dOpen = daOpen[ i ];
            dClose = -1;
            dHigh = daHigh[ i ];
            dLow = daLow[ i ];
         }
         dPrec = d;
         nDOWPrec = nDOW;
      }
      OHLCDataItem[ ] theItems = vItems.toArray( new OHLCDataItem[ 0 ] );
      return theItems;
   }

   
   private double[ ] GetYValues( XYDataset aDataset )
   {
      int nSize = aDataset.getItemCount( 0 );
      double[ ] daRet = new double[ nSize ];
      for( int i = 0 ; i < nSize ; i++ )
         daRet[ i ] = aDataset.getYValue( 0, i );
      return daRet;
   }
   
   private Date[ ] GetDateValues( XYDataset aDataset )
   {
      int nSize = aDataset.getItemCount( 0 );
      Date[ ] daRet = new Date[ nSize ];
      for( int i = 0 ; i < nSize ; i++ )
         daRet[ i ] = new Date( (long) aDataset.getXValue( 0, i ) );
      return daRet;
   }
   /**
    * 
    * Assumes the candlestick graph is already setup
    * Adds the mobile average to it
    */
   private void AddMobileAverage( Quotes q, JFreeChart jfc )
   {
      XYPlot xyp = jfc.getXYPlot();
      double [] daValues = GetYValues( xyp.getDataset() );
      Date[ ] daDates = GetDateValues( xyp.getDataset() );
      double [] daMAvg = Stats.ComputeMobileAvg( daValues, m_nMAvgDays );
      
      // creates dataset and renderer...
      MyXYDataset aDataset = new MyXYDataset( daDates, daMAvg );
      XYItemRenderer aRenderer = new XYLineAndShapeRenderer( true, false );
      // ... and adds them to the plot
      xyp.setDataset(1, aDataset );      
      xyp.setRenderer( 1, aRenderer );

      
      double dTrend = Double.NaN;
      if( daValues.length >= 4 )   
      {
         double d1 = daMAvg[ daValues.length - 5 ];
         double d2 = daMAvg[ daValues.length - 1 ];
         System.err.println( "N = " + d2 + ", N-1=" + d1 );
         dTrend = 100 * ( d2 - d1 ) / d2;
         dTrend *= 52;
         dTrend /= 4;
      }
      SetTrend( dTrend );
   }
   
   /**
    * Sets correct candlestick width and lower bound
    * Removes week-ends from time axis
    */
   private void ArrangeGraph( Quotes q, JFreeChart jfc )
   {
      // sets the lower bound of y axis to 90 % of lower value
      double[ ] daValues = q.GetValues( Quotes.LOW );
      double dMin = Double.MAX_VALUE;
      for( int i = 0 ; i < daValues.length ; i++ )
      {
         dMin = Math.min( dMin, daValues[ i ] );      
      }
      XYPlot xyp = jfc.getXYPlot();
      NumberAxis na = (NumberAxis) xyp.getRangeAxis( );
      na.setLowerBound( dMin * 0.9 );
      
      // remove the week-ends from the time axis
      DateAxis da = (DateAxis)xyp.getDomainAxis( );
      Timeline tl = null;
      if( m_bWeekly )      
      {
         XYDataset xyd = xyp.getDataset( );
         tl = GetWeeklyTimeline( GetDateValues( xyd ) );
      }
      else
         tl = GetDailyTimeline();
      da.setTimeline( tl );
      
      // arrange candlestick width
      CandlestickRenderer cr = (CandlestickRenderer) xyp.getRenderer();
      cr.setCandleWidth( 4 );
   }
   
   Timeline GetDailyTimeline( )
   {
      return SegmentedTimeline.newMondayThroughFridayTimeline();
   }
   
   Timeline GetWeeklyTimeline( Date[ ] daDates  )
   {
      return new FreeTimeline( daDates );
   }
   
   
   private static class FreeTimeline implements Timeline
   {  
      private Date[ ] m_daDate;
      
      FreeTimeline( Date[] daDate )
      {
         m_daDate = daDate;
      }
      
      //Returns true if a range of dates are contained in the timeline.
      @Override
      public boolean containsDomainRange(Date fromDate, Date toDate)
      {
         int nFirst = 0;
         int nLast = m_daDate.length - 1;
         if( nLast <= 0 )
            return false;
         if( fromDate.after( m_daDate[ nFirst ] ) && toDate.before( m_daDate[ nLast ] ) )
            return true;
         return false;
      }
       
      
      // Returns true if a range of values are contained in the timeline.
      @Override
      public boolean  containsDomainRange(long fromMillisecond, long toMillisecond)
      {
         return containsDomainRange( new Date( fromMillisecond ), new Date( toMillisecond) );
      }
      
      // Returns true if a date is contained in the timeline values.
      @Override
      public boolean containsDomainValue(Date date)
      {
         // assumes the array of dates is sorted
         return (-1 != Arrays.binarySearch( m_daDate, date ) );
      }
      
      //    Returns true if a value is contained in the timeline values.
      @Override
      public boolean containsDomainValue(long millisecond)
      {
         return containsDomainValue( new Date( millisecond ) );
      } 
      
      // Translates a value relative to this timeline into a domain value.
      @Override
      public long toMillisecond(long timelineValue)
      {
         return timelineValue; 
      }
       
      // Translates a date into a value on this timeline.
      @Override
      public long toTimelineValue(Date date)
      {
         return date.getTime( );
      }

      @Override
      public long toTimelineValue(long milliseconds)
      {
         return milliseconds; 
      }
   }
   
}

