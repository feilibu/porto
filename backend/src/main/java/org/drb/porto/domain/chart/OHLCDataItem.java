package org.drb.porto.domain.chart;

import java.util.Date;

public class OHLCDataItem
{
   private Date date;
   private double o, h, l, c;
   private long volume;

   public OHLCDataItem(Date date, double o, double h, double l, double c, long volume)
   {
      this.date = date;
      this.o = o;
      this.h = h;
      this.l = l;
      this.c = c;
      this.volume = volume;
   }

   public Date getDate()
   {
      return date;
   }

   public double getL()
   {
      return l;
   }

   public long getVolume()
   {
      return volume;
   }

   public double getO()
   {
      return o;
   }

   public double getC()
   {
      return c;
   }

   public double getH()
   {
      return h;
   }
}
