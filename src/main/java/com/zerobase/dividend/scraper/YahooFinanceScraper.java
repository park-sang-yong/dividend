package com.zerobase.dividend.scraper;

import com.zerobase.dividend.model.Company;
import com.zerobase.dividend.model.Dividend;
import com.zerobase.dividend.model.ScrapedResult;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YahooFinanceScraper {

    /*private static final String URL = "https://finance.yahoo.com/quote/COKE/history?" +
            "period1=99100800&" +
            "period2=1698537600&" +
            "interval=1mo&" +
            "filter=history&" +
            "frequency=1mo&" +
            "includeAdjustedClose=true";*/
    private static final String STATISTICS_URL
            = "https://finance.yahoo.com/quote/%s/history?" +
            "period1=%d&period2=%d&interval=1mo";
    public ScrapedResult scrap(Company company){
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);
        try {
            long start = 0;
            long end = 0;

            String url = String.format(STATISTICS_URL,company.getTicker(),start,end);

            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);// table전체

            Element tbody = tableEle.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }
                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

//                System.out.println(year + "/" + month + "/" + day + "->" + dividend);

            }
            scrapResult.setDividendEntities(dividends);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scrapResult;
    }
}
