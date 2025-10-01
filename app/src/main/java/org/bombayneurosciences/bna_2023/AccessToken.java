package org.bombayneurosciences.bna_2023;

import android.util.Log;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    private static final String firebaseMessagingScope =
            "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"bnafirebase\",\n" +
                    "  \"private_key_id\": \"81f6c0400080636179e604eb517239b4dfe72e06\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDErDHJ8wcVyLpQ\\ndOD0j9MICKjOx6GW1PGnMwWGY32e7hk/quT/njzyL+pmQoWN1QL372NSJwzXMWpQ\\n8BrPJrNxqzdezV2Oe2620xCymK49S77nwTWiBIjSnzDZIYgS4qiMlgGaklVTADg2\\npsbTJrYwhmwGuLFKEH4F1Ejt6Zx14yNkxtjkAF6eYEZeZr0MFjjuw3xOynKsLD6D\\ndGrXtcTrrwtJtQhCMbNkO3uzkOp3TyFD98wNVw7GMZJi89bmK9aLRA2VUmF4yA6S\\nGAEIpeYPV20PIKBedwfLwukZtrXwEGXjksEScgrLdBgQP8KVekBjEHLVVKkpLmaB\\n+4ORx4inAgMBAAECggEATt9JplYu2LcjtQFyd9G4DYg/fpHDxXQHp4iIJjF/Hv4Y\\npRZZeQPSKEQ8Ch+SZONwqdLYr4kWlrRsESKBdeLs4uCJt5y/cNUifPJfR2iAs/dN\\nC69J3XxZDLKnyeQvKHe6pYrBAOa/jiZP5Ob0ZJipRwOZjgaCnxPHHnpYdwURaJrm\\n9pe8Bh3PMlssRzptoTPiZYurAwVXvK1Lvm+DHSB9YyPIStdFMg9Gw6Q+CRxONzuo\\nQMkfSi3A/5qL1lnKe9X9M5SKInYYAy2oP31LQmzOGBqzn6QXln4pXzY/LS2FusZu\\nGb0H7gGvysdWi7I+DngmBe8PvejysdH89fxRylH9+QKBgQDuj79112oyj4xx7yJy\\nJcat59bhuXgr9NK4j/uPDmWA5mNSOfSpOXHDoHgn28AYSCJb5ZcuQ2c9pRIZr0Pf\\n5U/XxJ0HkMCwV5u4VUWPo9rOHrqAC9azc7L0RMLl5XbbkGLUinqgj8SmG6h/Dg3e\\nARemizaL7FjHrHyqCbC4oYywKQKBgQDTDJI0cDUWVxtWSycLNNeIF01M4gXhYLHR\\n7Z2c+D3MRFzbViYlwgQMKJXCtDTmT/FL9MOqOqk93yk6HJHkqnzBBYxXuXqPFGMq\\ndVIDrnVxGorNPw7jG5O9wprThqVPIgjp6QaUAsVqNvSip/Fxjxlakvi/dQsC6cs5\\nURRraKlMTwKBgE4fuR2IO/ju1ZmCLI+hG11cLRyitl3+wPs7+6+vkUITMLd+z0aT\\nz1O3O2IiLthE1yutFkNKQ4cGhdTvdpTd7bXFZ39Qwil9pztIgY02pbMWkRDZz6fY\\n3ePsmL7c1H223QapitsF+epD6HwjSBtJ1ErwEB7meXR6YiD1J3BvRzlxAoGAAz7+\\nQLA5IM7WLM2bHCED76PsHHKdhInoMkJlWjS3Pp0OkJESpSIGzekR5JzA84nnPDPX\\njpa4Y6s7BxnJZKr6oD1lmzPPHsRBgjWrvULBq6us1ttRb0zNSO3lU7uOb1kmqfJb\\nl4szXh7aRGYw4H5C9ZGtIcS7FqBh78+/cKd7cyECgYEAh/5S0RQFpdA/b7o9aSmj\\nRRoS+gg5U1MKPW1rFU6pHL2HY8mWTTJ9e+dcrVnuzHSTgJpx6GAAODLpnTOOJMjt\\ndu4Asx/F5RkjUixqzfwXBErxCE8m3A02M7ObKAT+mAC8ng7QKkNKHom/mrJL3NYP\\ndY7vfs2IInhkPTFoNR07Aww=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-6njvu@bnafirebase.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"107965164605916041305\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-6njvu%40bnafirebase.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(firebaseMessagingScope);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            Log.e("AccessToken", "getAccessToken: " + e.getLocalizedMessage());
            return null;
        }
    }
}
