package org.drb.porto.domain.chart;

import java.util.Date;

public class XYDataset
{
   private Date[] m_daDates;
   private double[] m_daValues;

   XYDataset(Date[] daDates, double[] daValues)
   {
      m_daDates = daDates;
      m_daValues = daValues;
   }

   public int getItemCount(int nSeries)
   {
      return m_daDates.length;
   }

   public Number getX(int series, int nItem)
   {
      Number nRet = new Long(m_daDates[nItem].getTime());
      return nRet;
   }

   public Number getY(int series, int nItem)
   {
      double d = Double.NaN;
      if (nItem < m_daValues.length)
         d = m_daValues[nItem];
      return new Double(d);
   }

   public int getSeriesCount()
   {
      return 1;
   }

   public Comparable<String> getSeriesKey(int nSeries)
   {
      return "MySeries";
   }

   public double getYValue(int series, int item)
   {
      return (Double)getY(series, item);
   }

   public long getXValue(int series, int item)
   {
      return (Long)getX(series,item);
   }

}
