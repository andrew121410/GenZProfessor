package com.andrew121410.genzprofessor.manager;

import com.andrew121410.genzprofessor.objects.CheggRequest;
import com.andrew121410.genzprofessor.objects.CheggRequestResult;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CheggRequestManager extends Thread {

    private File tempFolder;
    private String cookie;

    private CheggRequest currentRequest;

    public CheggRequestManager() {
        this.tempFolder = new File("cache");
        if (this.tempFolder.exists()) {
            for (File file : this.tempFolder.listFiles()) file.delete();
        } else {
            this.tempFolder.mkdir();
        }

        File cookieFile = new File("cookie.txt");
        try {
            this.cookie = Files.readString(cookieFile.toPath());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @SneakyThrows
    public void processLink(CheggRequest currentRequest, Consumer<CheggRequestResult> consumer) {
        this.currentRequest = currentRequest;

        Document document = Jsoup.connect(currentRequest.getLink()).headers(createHeaders()).get();
        String code = document.html().replaceAll("\"//", "\"https://");
        document = Jsoup.parse(code);

        if (isTextbookSolution(document)) {
            consumer.accept(new CheggRequestResult(null, CheggRequestResult.Result.FAILED_TEXTBOOK_SOLUTION));
            return;
        }

        List<File> files = new ArrayList<>(getPictures(document));
        File htmlFile = getAnswerHtml(document);
        files.add(htmlFile);
        consumer.accept(new CheggRequestResult(files, CheggRequestResult.Result.SUCCESS));
    }

    private boolean isTextbookSolution(Document document) {
        return !document.select("#solution-player-sdk").isEmpty();
    }

    private List<File> getPictures(Document document) {
        Objects.requireNonNull(document, "Document can't be null");
        Elements answerElements = document.getElementsByClass("answers-list");
        Elements imageElements = answerElements.select("img");
        return imageElements.stream().map(element -> URLToFile(element.absUrl("src"))).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private int a = 0;

    private File URLToFile(String url) {
        //https://stackoverflow.com/questions/12465586/how-can-i-download-an-image-using-jsoup
        if (url.contains("avatars")) return null;

        File file = new File(this.tempFolder, a + Instant.now().getNano() + ".png");
        try {
            URL urlObject = new URL(url);
            BufferedImage saveImage = ImageIO.read(urlObject);
            ImageIO.write(saveImage, "png", file);
            a++;
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private File getAnswerHtml(Document document) {
        Objects.requireNonNull(document, "Document can't be null");
//        Elements elements = document.getElementsByClass("txt-body answer-body");
        Document document1 = new WebsiteStyler().format(document);
        File file = new File(this.tempFolder, "answer." + Instant.now().getNano() + ".html");
        try (FileWriter fileWriter = new FileWriter(file)) {
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(document1.toString());
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private File getTestHTML(Document document) {
        File file = new File(this.tempFolder, "answer.html");
        try (FileWriter fileWriter = new FileWriter(file)) {
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(document.body().toString());
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private Map<String, String> createHeaders() {
        Map<String, String> map = new HashMap<>();
        map.put("cookie", this.cookie);
        map.put("authority", "www.chegg.com");
        map.put("method", "GET");
        map.put("scheme", "https");
        map.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        map.put("accept-encoding", "gzip, deflate, br");
        map.put("accept-language", "en-US,en;q=0.9");
        map.put("cache-control", "max-age=0");
        map.put("sec-fetch-dest", "document");
        map.put("sec-fetch-mode", "navigate");
        map.put("sec-fetch-site", "same-origin");
        map.put("sec-fetch-user", "?1");
        map.put("upgrade-insecure-requests", "1");
        map.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36 Edg/86.0.622.69");
        return map;
    }
}