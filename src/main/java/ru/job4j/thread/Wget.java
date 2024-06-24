package ru.job4j.thread;

import java.io.File;
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
        var file = new File("data/tmp.xml");
        try (var input = new URL(this.url).openStream();
             var output = new FileOutputStream(file)) {
            var dataBuffer = new byte[1024];
            int readBytes;
            var downloadStart = System.currentTimeMillis();
            while ((readBytes = input.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                output.write(dataBuffer, 0, readBytes);
                if (readBytes == 1024) {
                    var timeApp = System.currentTimeMillis() - downloadStart;
                    var speedApp = 1024 / timeApp;
                    if (speedApp > this.speed) {
                        Thread.sleep(speedApp / this.speed);
                    }
                }
                downloadStart = System.currentTimeMillis();
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