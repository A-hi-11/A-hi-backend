package com.example.Ahi.dto.oauthDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoProfile {

    public Long id;
    public String connected_at;
    public Properties properties;
    public KakaoAccount kakao_account;

    @Data
    public class Properties { //(1)
        public String nickname;
        public String profile_image;
        public String thumbnail_image;
    }

    @Data
    public class KakaoAccount { //(2)
        public Boolean profile_nickname_needs_agreement;
        public Boolean profile_image_needs_agreement;
        public Profile profile;

        @Data
        public class Profile {
            public String nickname;
            public String thumbnail_image_url;
            public String profile_image_url;
            public Boolean is_default_image;
        }
    }
}