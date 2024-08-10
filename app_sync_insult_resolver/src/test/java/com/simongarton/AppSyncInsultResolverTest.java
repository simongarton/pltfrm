package com.simongarton;

import org.junit.jupiter.api.Test;

public class AppSyncInsultResolverTest {

    @Test
    public void getInsult() {

        // given
        final AppSyncInsultResolver appSyncInsultResolver = new AppSyncInsultResolver();

        // when
        final String insult = appSyncInsultResolver.getInsult();

        // then
        assert insult != null;
        System.out.println(insult);
    }
}