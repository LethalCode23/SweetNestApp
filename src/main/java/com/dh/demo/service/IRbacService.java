package com.dh.demo.service;

import com.dh.demo.dto.ModuleDto;
import java.util.List;

public interface IRbacService {

    List<ModuleDto> getModulesWithActionsByProfile(Long profileId);

    void updateEntryAllowed(Long profileId, Long moduleId, Boolean entryAllowed);
}