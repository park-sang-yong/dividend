package com.zerobase.dividend;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DividendApplication {

	public static void main(String[] args) {
		//SpringApplication.run(DividendApplication.class, args);

		try {
			Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/" +
					"history?" +
					"period1=99100800&" +
					"period2=1698537600&" +
					"interval=1mo&" +
					"filter=history&" +
					"frequency=1mo&" +
					"includeAdjustedClose=true");
			Document document = connection.get();

			Elements eles = document.getElementsByAttributeValue("data-test","historical-prices");
			Element ele = eles.get(0);
			System.out.println(ele);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

}
