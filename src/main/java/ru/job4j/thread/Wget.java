package ru.job4j.thread;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Wget implements Runnable {
    private final String url;
    private final int speed;

    public Wget(String url, int speed) {
        this.url = url;
        this.speed = speed;
    }

    @Override
    public void run() {
        var file = this.url.substring(this.url.lastIndexOf("/") + 1);
        var countBytes = 0;
        try (var input = new URL(this.url).openStream();
             var output = new BufferedOutputStream(new FileOutputStream(file))) {
            var dataBuffer = new byte[1024];
            int readBytes;
            var downloadStartTime = System.currentTimeMillis();
            while ((readBytes = input.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                output.write(dataBuffer, 0, readBytes);
                countBytes += readBytes;
                if (countBytes >= this.speed) {
                    var appDownloadTime = System.currentTimeMillis() - downloadStartTime;
                    if (appDownloadTime < 1000) {
                        Thread.sleep(1000 - appDownloadTime);
                    }
                    countBytes = 0;
                    downloadStartTime = System.currentTimeMillis();
                }
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2
                || args[0].isEmpty()
                || args[1].isEmpty()) {
            throw new IllegalArgumentException();
        }
        String url = args[0];
        int speed = Integer.parseInt(args[1]);
        Thread wget = new Thread(new Wget(url, speed));
        wget.start();
        wget.join();
    }
}