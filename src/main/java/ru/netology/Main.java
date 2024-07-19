package ru.netology;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;
import java.util.Arrays;


public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=QdbcbHao7DZdqikXv51pzO6oJyc1lvMFdFui7v4b";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("get NASA image")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build()) {
            HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

                NasaAnswer nasaAnswer = mapper.readValue(response.getEntity().getContent(), NasaAnswer.class);

                HttpGet request1 = new HttpGet(nasaAnswer.getHdurl());

                try (CloseableHttpResponse response1 = httpClient.execute(request1)) {

                    byte[] body = response1.getEntity().getContent().readAllBytes();

                    try (BufferedInputStream in = new BufferedInputStream(new URL(nasaAnswer.getHdurl()).openStream());
                         FileOutputStream out = new FileOutputStream("2024-07-11Pavel_2048p.jpg")) {
                        int bytesRead;
                        while ((bytesRead = in.read(body, 0, 1024)) != -1) {
                            out.write(body, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
