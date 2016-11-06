package org.drb.porto.web.rest.v1;

import org.drb.porto.base.Quotes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/v1/stock/{ticker}")
public class GetStockData
{
    @RequestMapping(method= RequestMethod.GET,produces = "text/javascript")
    public String getStockData(
            @PathVariable(name="ticker") String ticker,
            @RequestParam(name="start") String startDate,
            @RequestParam(name="end") String endDate,
            @RequestParam(name="callback") String callback) throws IOException {
        Quotes q = new Quotes();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -3);
        Date dEnd = parseDate(startDate, new Date());
        Date dStart = parseDate(endDate, c.getTime());

        try {
            q.Acquire(ticker, dStart, dEnd);
        } catch (SQLException e) {
            return "[\"Error\": \"" + e + "\"]";
        }
        return callback + "([" + convertToGoogleQuotes(q) + "]);";
    }


    private String convertToGoogleQuotes(Quotes q)
    {
        StringBuilder sb = new StringBuilder();
        // [1256601600000,28.81,28.97,28.06,28.20,189137473],
        Date[] allDates = q.GetDates();
        double[] ohlcO = q.GetValues(Quotes.OPEN);
        double[] ohlcH = q.GetValues(Quotes.HIGH);
        double[] ohlcL = q.GetValues(Quotes.LOW);
        double[] ohlcC = q.GetValues(Quotes.CLOSE);
        int[] volumes = q.GetVolumes();
        for(int n = 0 ; n < allDates.length ; n++) {
            appendRecord(sb, allDates[n], ohlcO[n], ohlcH[n], ohlcL[n], ohlcC[n], volumes[n], n);
            if(n<allDates.length-1)
                sb.append(",");
            sb.append("\n");
        }
        return sb.toString();
    }

    private void appendRecord(StringBuilder sb, Date date, double o, double h, double l, double c, int volume, int n) {
        sb.append("[");
        sb.append(date.getTime());
        append(sb, o);
        append(sb, h);
        append(sb, l);
        append(sb, c);
        append(sb, volume);
        sb.append("]");
    }

    private void append(StringBuilder sb, double d) {
        sb.append(",").append(d);
    }

    private void append(StringBuilder sb, int n) {
        sb.append(",").append(n);
    }

    private Date parseDate(String sDate, Date defaultDate) {
        if(sDate == null)
            return defaultDate;
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            return df.parse(sDate);
        } catch (ParseException e) {
            return defaultDate;
        }
    }
}
