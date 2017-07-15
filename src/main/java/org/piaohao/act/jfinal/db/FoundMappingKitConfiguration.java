package org.piaohao.act.jfinal.db;

import act.event.ActEvent;

/**
 * An event raised if JFinalService found MappingKit configuration
 */
public class FoundMappingKitConfiguration extends ActEvent<JFinalService> {
    public FoundMappingKitConfiguration(JFinalService source) {
        super(source);
    }
}
