package ru.mtuci.MindScape.home.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class WebPurifyCensorship {

    private static final String WEB_PURIFY_API_KEY = System.getenv("WEB_PURIFY_API_KEY");
    private static final String WEB_PURIFY_ENDPOINT = "http://api1.webpurify.com/services/rest/";

    public static String censorText(String text) throws IOException, InterruptedException, URISyntaxException {
        if (WEB_PURIFY_API_KEY == null || WEB_PURIFY_API_KEY.isEmpty()) {
            throw new RuntimeException("WEB_PURIFY_API_KEY is not set in environment variables.");
        }

        // Параметры запроса
        String method = "webpurify.live.replace";
        String replacesymbol = "%23"; // это заменит каждый символ на #

        // Формирование URL-запроса
        String apiUrl = String.format("%s?api_key=%s&method=%s&text=%s&replacesymbol=%s&lang=ru&semail=1&sphone=1&slink=1",
                WEB_PURIFY_ENDPOINT, WEB_PURIFY_API_KEY, method, URLEncoder.encode(text, "UTF-8"), replacesymbol);

        // Отправка HTTP-запроса
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(apiUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Обработка ответа и извлечение цензурированного текста
        String censoredText = parseResponse(response.body());

        return censoredText;
    }
    private static String parseResponse(String responseBody) {
        try {
            // Создаем фабрику построителей документов
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Преобразуем строку ответа в поток и строим документ
            Document document = builder.parse(new InputSource(new StringReader(responseBody)));

            // Получаем корневой элемент (rsp)
            Element rspElement = document.getDocumentElement();

            // Проверяем, что статус ответа "ok"
            if ("ok".equals(rspElement.getAttribute("stat"))) {
                // Получаем элемент text
                NodeList textNodes = rspElement.getElementsByTagName("text");
                if (textNodes.getLength() > 0) {
                    // Возвращаем текст из первого элемента text
                    return textNodes.item(0).getTextContent();
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // Обработка ошибок парсинга
        }

        return null; // Возвращаем null в случае ошибки или отсутствия текста
    }

}
