package com.pathdx.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class CommonUtil {
    public  String capitalizeFirstCharacter(String inputString) {

        String s1 = inputString.substring(0, 1).toUpperCase();
        String s2 = inputString.substring(1);
        String result = inputString.substring(0, 1).toUpperCase() + inputString.substring(1).toLowerCase();
        return result;
    }
    public  Long getDifference(String firstDt, String secondDt, String zone) {
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime firstZdt = ZonedDateTime.of(
                LocalDateTime.parse(firstDt), zoneId
        );
        ZonedDateTime secondZdt = ZonedDateTime.of(
                LocalDateTime.parse(secondDt), zoneId
        );


        Duration duration;

        if (firstZdt.isAfter(secondZdt)) {
            duration = Duration.between(secondZdt, firstZdt);
        } else {
            duration = Duration.between(firstZdt, secondZdt);
        }

        long hoursBetween;
        hoursBetween = duration.toHours();
        long minutesBetween;
        minutesBetween = duration.toMinutes() - (hoursBetween * 60);
        long secondsBetween;
        secondsBetween = duration.getSeconds() - (duration.toMinutes() * 60);
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(hoursBetween).append(" hours ");
        if (minutesBetween < 10 && minutesBetween > 0)
            resultBuilder.append("0");
        resultBuilder.append(minutesBetween).append(" minutes ");
        if (secondsBetween < 10 && secondsBetween > 0)
            resultBuilder.append("0");
        resultBuilder.append(secondsBetween).append(" seconds");

        return duration.toHours();
    }
    public String convertSingedURLToBase64(String strSignedUrl) throws Exception{
        URL imageUrl = new URL(strSignedUrl);
        URLConnection ucon = imageUrl.openConnection();
        InputStream is = ucon.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = is.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, read);
        }
        baos.flush();
        byte[] encode = Base64.encodeBase64(baos.toByteArray(), true);
        return new String(encode);
    }
}
