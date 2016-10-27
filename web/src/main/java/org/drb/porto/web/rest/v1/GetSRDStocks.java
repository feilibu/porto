package org.drb.porto.web.rest.v1;

import org.drb.porto.base.StockEntry;
import org.drb.porto.base.Stocks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/v1/stocks")
public class GetSRDStocks {
    @GET
    @Produces("text/json")
    public String get() {
         StockEntry[] stocks = Stocks.GetAllStockEntries();
         return toJson(stocks);
    }

    private String toJson(StockEntry[] stocks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(StockEntry s:stocks) {
            if (sb.length() > 1)
                sb.append(",\n");
            sb.append(toJson(s));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(StockEntry se) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        appendAttribute(sb, "isin", se.GetIsin(), true);
        appendAttribute(sb, "label", se.GetLabel(), true);
        appendAttribute(sb, "symbol", se.GetTicker(), false);
        sb.append("}");
        return sb.toString();
    }

    private void appendAttribute(StringBuilder sb, String name, String value, boolean appendComma) {
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
        if(appendComma)
            sb.append(",");
    }
}
