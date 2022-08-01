package com.example.whisper.initialization;

import com.example.whisper.app_properties.AppProperties;
import com.example.whisper.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppInitialization {

    private final InitService initService;
    private final PropertyService propertyService;

    @EventListener(ContextRefreshedEvent.class)
    public void postContextInitialization(ContextRefreshedEvent event) {
        log.info("Initialization method has started");
        ApplicationContext applicationContext = event.getApplicationContext();
        AppProperties appProperties = applicationContext.getBean(AppProperties.class);
        if(appProperties.getFetchPropsFromDbAtStartup()) {
            initService.fetchPropsFromDb();
        } else {
            propertyService.savePropsToUtilities();
        }

        if(appProperties.getIsRecreateServerUserNeeded()) {
            initService.recreateServerUser();
        }
    }
}