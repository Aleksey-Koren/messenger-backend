package com.example.whisper.initialization;

import com.example.whisper.app_properties.AppProperties;
import com.example.whisper.service.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppInitialization {

    private final InitService initService;
    private final UtilService utilService;

    @EventListener(ContextRefreshedEvent.class)
    public void postContextInitialization(ContextRefreshedEvent event) {
        System.out.println("I am in event Listener");
        ApplicationContext applicationContext = event.getApplicationContext();
        AppProperties appProperties = applicationContext.getBean(AppProperties.class);
        if(appProperties.getFetchPropsFromDbAtStartup()) {
            initService.fetchPropsFromDb();
        } else {
            utilService.savePropsToUtilities();
        }

        if(appProperties.getIsRecreateServerUserNeeded()) {
            initService.recreateServerUser();
        }
    }
}