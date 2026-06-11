package com.zfx.community.provider;

import com.alibaba.fastjson.JSON;
import com.zfx.community.dto.AccessTokenDTO;
import com.zfx.community.dto.GithubUser;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GithubProvider {

    private static final Logger log = LoggerFactory.getLogger(GithubProvider.class);

    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String[] pairs = string.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2 && "access_token".equals(kv[0])) {
                    return kv[1];
                }
            }
            log.error("Failed to parse access_token from response: {}", string);
        } catch (Exception e) {
            log.error("getAccessToken error, accessTokenDTO={}", accessTokenDTO, e);
        }
        return null;
    }

    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user")
                .header("Authorization", "token " + accessToken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            log.debug("GitHub user response received");
            return JSON.parseObject(body, GithubUser.class);
        } catch (Exception e) {
            log.error("getUser error, accessToken={}", accessToken, e);
        }
        return null;
    }
}
