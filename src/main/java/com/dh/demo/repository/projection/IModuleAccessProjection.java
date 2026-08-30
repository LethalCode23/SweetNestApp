package com.dh.demo.repository.projection;

public interface IModuleAccessProjection {

    Long getModuleId();
    String getName();
    String getDescription();
    String getUrl();
    String getState();
    Character getEntryAllowed();
    Long getSubModuleId();
    String getSubModuleName();
    String getSubModuleUrl();

    Character getActionCode();     // nuevo
    String getActionName();        // nuevo
    Character getCheckAllowed();   // nuevo
}