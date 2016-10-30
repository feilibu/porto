package org.drb.porto.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.drb.porto.db.DBAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Stores a series of (date / value) association for one stock
 *
 */
public class Quotes implements Comparable<Quotes>
{
   private static final Logger LOGGER = LoggerFactory.getLogger(Quotes.class);

   private double[] m_daLow;
   private double[] m_daHigh;
   private double[] m_daOpen;
   private double[] m_daClose;
   private int[] m_naVolume;

   private Calendar m_c = Calendar.getInstance();

   private String m_strName;

   private Date[] m_daDates;
   private String m_strTicker;

   // various indicators
   private Grade m_gGrade;
   private double m_dDelta; // % of increase / decrease over the period
   private double m_dStdDev; // std deviation
   private double m_dMobAvgConsistency; // % of time spent on the same side of the mob avg

   private HashMap<Date, Integer> m_hmByDate = new HashMap<Date, Integer>();

   final public static int HIGH = 1;
   final public static int LOW = 2;
   final public static int OPEN = 3;
   final public static int CLOSE = 4;

   public Quotes()
   {

   }

   /**
    * "natural" order is by descending grade
    */
   @Override
   public int compareTo(Quotes o)
   {
      return GetGrade().compareTo(o.GetGrade());
   }

   private void SetName(String strName)
   {
      m_strName = strName;
   }

   public String GetName()
   {
      return m_strName;
   }

   public void Acquire(String strTicker, Date dStart, Date dEnd) throws SQLException
   {
      m_strTicker = strTicker;
      DBAccess dba = new DBAccess();
      dba.Connect();

      // fetch long name
      String strGetName = "select name from stocks where symbol='" + strTicker + "'";
      ResultSet rsName = dba.ExecuteSQL(strGetName);
      while (rsName.next())
      {
         SetName(rsName.getString(1));
      }
      rsName.close();

      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

      StringBuffer sb = new StringBuffer();
      sb.append("select day, close, open, high, low, volume");
      sb.append(" from quotes where symbol = '");
      sb.append(strTicker);
      sb.append("' AND day >= '");
      sb.append(df.format(dStart));
      sb.append("' AND day <= '");
      sb.append(df.format(dEnd));
      sb.append("' order by day");

      ResultSet rs = dba.ExecuteSQL(sb.toString());
      FillFromResultSet(rs);
      dba.Disconnect();
   }

   private String printDate(long l)
   {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      return df.format(l);
   }

   /**
    * ResultSet always contains Date, value
    */
   private void FillFromResultSet(ResultSet rs) throws SQLException
   {
      ArrayList<Object> vValues = new ArrayList<Object>();
      ArrayList<Date> vDates = new ArrayList<Date>();
      while (rs.next())
      {
         Date d = NormalizeDate(rs.getDate(1).getTime());
         vDates.add(d);

         vValues.add(new Double(rs.getDouble(2)));
         vValues.add(new Double(rs.getDouble(3)));
         vValues.add(new Double(rs.getDouble(4)));
         vValues.add(new Double(rs.getDouble(5)));
         vValues.add(new Integer(rs.getInt(6)));
         if (LOGGER.isDebugEnabled())
            LOGGER.debug(printDate(rs.getDate(1).getTime()) + "\t" + rs.getDouble(2) + "\t" + rs.getDouble(2) + "\t" + rs.getDouble(3) + "\t" + rs.getDouble(4)
               + "\t" + rs.getDouble(5) + "\t" + rs.getDouble(6));
      }
      rs.close();

      int nSize = vDates.size();
      m_daClose = new double[nSize];
      m_daOpen = new double[nSize];
      m_daHigh = new double[nSize];
      m_daLow = new double[nSize];
      m_naVolume = new int[nSize];
      m_daDates = new Date[nSize];
      int n = 0;
      for (int i = 0; i < nSize; i++)
      {
         m_daDates[i] = vDates.get(i);
         m_daClose[i] = GetDoubleValue(vValues, n);
         m_daOpen[i] = GetDoubleValue(vValues, n + 1);
         m_daHigh[i] = GetDoubleValue(vValues, n + 2);
         m_daLow[i] = GetDoubleValue(vValues, n + 3);
         m_naVolume[i] = GetIntValue(vValues, n + 4);
         n += 5;
         m_hmByDate.put(m_daDates[i], new Integer(i));
      }
   }

   private Date NormalizeDate(long time)
   {
      synchronized (m_c)
      {
         m_c.setTime(new Date(time));
         m_c.setTimeZone(TimeZone.getTimeZone("UTC"));
         m_c.add(Calendar.HOUR,12);
         m_c.set(Calendar.HOUR_OF_DAY, 0);
         m_c.set(Calendar.MINUTE, 0);
         m_c.set(Calendar.SECOND, 0);
         m_c.set(Calendar.MILLISECOND, 0);
         Date dNormalized = m_c.getTime();
         return dNormalized;
      }
   }

   public int GetIndexForDate(Date d)
   {
      Integer i = m_hmByDate.get(NormalizeDate(d.getTime()));
      if (i == null)
         return -1;
      return i;
   }

   private double GetDoubleValue(ArrayList<Object> v, int i)
   {
      Double f = (Double) v.get(i);
      return f.doubleValue();
   }

   private int GetIntValue(ArrayList<Object> v, int n)
   {
      Integer i = (Integer) v.get(n);
      return i.intValue();
   }

   public double[] GetValues(int nType)
   {
      switch (nType)
      {
      case HIGH:
         return m_daHigh;
      case LOW:
         return m_daLow;
      case OPEN:
         return m_daOpen;
      case CLOSE:
         return m_daClose;
      }
      return null;
   }

   public int[] GetVolumes()
   {
      return m_naVolume;
   }

   public Date[] GetDates()
   {
      return m_daDates;
   }

   public String GetTicker()
   {
      return m_strTicker;
   }

   public void Release()
   {
      m_daDates = null;
      m_daLow = null;
      m_daHigh = null;
      m_daOpen = null;
      m_daClose = null;
   }

   public void SetGrade(Grade gGrade)
   {
      m_gGrade = gGrade;
   }

   public Grade GetGrade()
   {
      return m_gGrade;
   }

   public void SetVariation(double d)
   {
      m_dDelta = d;
   }

   double GetVariation()
   {
      return m_dDelta;
   }

   public void SetStdDeviation(double d)
   {
      m_dStdDev = d;
   }

   double GetStandardDeviation()
   {
      return m_dStdDev;
   }

   public void SetMobAvgConsistency(double d)
   {
      m_dMobAvgConsistency = d;
   }

   double GetMobAvgConsistency()
   {
      return m_dMobAvgConsistency;
   }

}
