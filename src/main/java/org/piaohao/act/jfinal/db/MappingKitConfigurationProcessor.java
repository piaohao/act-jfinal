package org.piaohao.act.jfinal.db;

import act.Act;
import act.event.ActEventListenerBase;
import org.osgl.$;

/**
 * Handles the {@link FoundMappingKitConfiguration} event and process
 * the mapping kit class
 */
public class MappingKitConfigurationProcessor extends ActEventListenerBase<FoundMappingKitConfiguration> {
    @Override
    public void on(FoundMappingKitConfiguration event) throws Exception {
        JFinalService service = event.source();
        Class<?> mappingKitClass = $.classForName(service.mappingKitClassName, Act.app().classLoader());
        $.invokeStatic(mappingKitClass, "init", service.arp());
        service.start();
    }
}
