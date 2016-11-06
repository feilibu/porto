package org.drb.porto.web.rest.v1;

import jxl.HeaderFooter;
import org.drb.porto.base.StockEntry;
import org.drb.porto.base.Stocks;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/v1/stocks")
public class GetSRDStocks {
    @RequestMapping(method= RequestMethod.GET, produces= "text/json")
    public @ResponseBody String get() {
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
