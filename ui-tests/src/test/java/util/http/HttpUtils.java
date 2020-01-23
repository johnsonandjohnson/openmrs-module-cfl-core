package util.http;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;

public class HttpUtils {

    public static BaseHttpResponse get(String url) {
        try {
            Response response = perform(url, HttpMethod.GET.asString(), null);
            return new Gson().fromJson(response.body().string(), BaseHttpResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read the request response", e);
        }
    }

    public static <V> Response post(String url, V object) {
        return perform(url, HttpMethod.POST.asString(), new Gson().toJson(object));
    }

    public static <V, K> K post(String url, V object, Class<K> clazz) {
        try {
            Response response = post(url, object);
            return new Gson().fromJson(response.body().string(), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read the request response", e);
        }
    }

    public static void delete(String url) {
        perform(url, HttpMethod.DELETE.asString(), "");
    }

    private static Response perform(String url, String method, String content) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = content == null ? null : RequestBody.create(content, mediaType);
            Request request = new Request.Builder()
                    .url(url)
                    .method(method, body)
                    .addHeader("Content-Type", "application/json")
                    //TODO: Use real credentials when it'll be configurable
                    .addHeader("Authorization", "Basic YWRtaW46QWRtaW4xMjM=")
                    .build();
            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("Unable to perform a request", e);
        }
    }
}
