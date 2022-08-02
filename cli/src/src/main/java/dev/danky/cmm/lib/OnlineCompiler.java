package dev.danky.cmm.lib;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.net.http.HttpClient;
public class OnlineCompiler {
    public byte[] compileOnline(String c) throws Exception {
        String data = Base64.getEncoder().encodeToString(c.getBytes()).replace("=", "");

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://compile.cmm.danky.dev/compile"))
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<?> res = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (res.body().toString().startsWith("ERR")) {
            throw new Exception(res.body().toString());
        } else {
            return Base64.getDecoder().decode((String) res.body());
        }
    }
}
