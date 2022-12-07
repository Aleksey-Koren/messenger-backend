package com.scnsoft.bot.logic.impl;

import org.springframework.stereotype.Component;

import com.scnsoft.bot.logic.MessengerBot;
import com.scnsoft.bot.model.Message;

// TODO: only for test purposes, delete later
@Component
public class MessengerBotImpl implements MessengerBot {

    @Override
    public Message handleIncommingMessage(Message message) {
        return message;
    }

    @Override
    public String getBotId() {
        return """
            862801bf-7ed3-41aa-97e3-79e78fc42d4c
                """;
    }

    @Override
    public String getPublicKey() {
        return """
            -----BEGIN PUBLIC KEY-----
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4vC2B2q9J6aFDK+4x6fj
            I6gqXpES0qL/PvW3klY31E0khAykhanptNY0lJAZJKnEwzIEBQG2e3DdpEWf1Suu
            omUzAWSZSPvzrQtMOP2n+vRBRaWMG49cp+jJXUeFqj115hyQ4154m3AMpHHzee2w
            F9Zt3m7UcYVSeKvVgLWx92B7qzAIBq7C/t4m5wZYnnwpKVgBzXn57E4qIbEiwe2O
            PeRNbq4EowIFapkC7dPDVSIQwGFStB3L3uOHL8zw7t5aOAwSpI6QE4YL/xEi7BbZ
            vjsSZ2ltMeWExzEwdnZMJUYVcxUixbcne628AXtd1Wx4QGzgBHqHpCHf9n3XPGgD
            KQIDAQAB
            -----END PUBLIC KEY-----                                                                      
                """;
    }

    @Override
    public String getPrivateKey() {
        return """
            -----BEGIN RSA PRIVATE KEY-----
            MIIEpAIBAAKCAQEA4vC2B2q9J6aFDK+4x6fjI6gqXpES0qL/PvW3klY31E0khAyk
            hanptNY0lJAZJKnEwzIEBQG2e3DdpEWf1SuuomUzAWSZSPvzrQtMOP2n+vRBRaWM
            G49cp+jJXUeFqj115hyQ4154m3AMpHHzee2wF9Zt3m7UcYVSeKvVgLWx92B7qzAI
            Bq7C/t4m5wZYnnwpKVgBzXn57E4qIbEiwe2OPeRNbq4EowIFapkC7dPDVSIQwGFS
            tB3L3uOHL8zw7t5aOAwSpI6QE4YL/xEi7BbZvjsSZ2ltMeWExzEwdnZMJUYVcxUi
            xbcne628AXtd1Wx4QGzgBHqHpCHf9n3XPGgDKQIDAQABAoIBACA19kA7QDnegsf4
            MQv1omGvPsRJZxiKlhwDmFim8CmsAWDUwo1OPqOyO6hSg58WkZz6/mMPiS0gKfVl
            BHinSJQ4NPMfqjoXuNB46Aq2xh7yiEkwZCCNSqNygRJCUpCFRLSynUYIZKn7T3AP
            1HUb7YCUqOeHZ5PtTnVePmGUaHwp6x5qiP43jJZsNvX23W9x1nhXNpovyvAyX9ms
            6ktT2Lf/8LTpMivbK1ScaTWzFqEd9W3J5eamC9K7mSNB/uHOYZI+xvaqB07wOV/P
            /iPsooQ95JDgy1atknZkEReWjAvzNo8egoEulcWj0CMo8piszp0CFeGTuas62+Hn
            U1cTM9kCgYEA/kHPwB8/b16H8HeOQnguWKau7IvNqMQoeYVb4dE14Mvg27z0RnB8
            gO/bZu6FnWrSOf3q8lTUUnpzK4tCt5dGq0kBKDheCF9fBh9vPd8gWgS0jYP+cx2F
            2OqxfmQmWoR5Qd5aU3lb3WJSqmyI72Uoy7OgvE9sEgTVegtAE+8CyRMCgYEA5H72
            SVB+t1zxgLyynNQleJqAmeyRQq5bvWEIJAlfVX4awzAawIkCtIl/v0avunfA4UeS
            guxqfIIfVv7jORKJARgYJcXvhr3MMfYNSGdSBYHr/oKBtAaWkmC//7CthnKj6pRQ
            pOyp6wkTIpAg5Lvoi09L3giQ3+HGSQ5TI9W4JlMCgYEAprLPWs8h8AqyZd58zues
            u0LNfRlMBVJWlxXwX+WixXfmPSttnKEzf/XCi4Hm6IaYvpzT5X3olwrhZ7MuUuBD
            3RlIQO5z+FlPG9yQOqiHtX1QQ1g/A4pfrxBwvsU6wC4KsjzuS7xNlqBYQpRWU16n
            mKZ0xAHugQHaVOH7aRZTZTECgYEA0L6sgY3oNdvTRakeA+hL00JS+kwE6MYhSYG7
            zjBBx/jMukTYK8z/i6+R5UzOfaupqiWY08LoBZdayj9RlnI4d6draBbMHGyIf5wp
            xludsypeTsUreSwLou6AeTcPUCSrW0Lj6cfL88z2jjzd0Bla+OoYhV+N+vpoC5Xq
            nnJBKcECgYAz+gKoXia3ZZyJAOtCSlFMvvNjOwfBIVqAOIGKEG7sCOA9pryEvUZa
            Hnne3PeNogD3T1juhHxPMPgNnm56L7KQhreE3MxrdxGl49KRo83XqYwKpIufarw7
            7VX/WCpp1nypVqH/C7xN4To2Ozj0AIUGYD3UMD5fEROFE+5vOCiHPA==
            -----END RSA PRIVATE KEY-----                                                            
                """;
    }
    
}
