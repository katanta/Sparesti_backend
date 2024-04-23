package org.ntnu.idi.idatt2106.sparesti.sparestibackend.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.dto.ChallengeConfigDTO;
import org.ntnu.idi.idatt2106.sparesti.sparestibackend.model.ChallengeConfig;

@Mapper(
        uses = {ChallengeTypeConfigMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ChallengeConfigMapper {

    ChallengeConfigMapper INSTANCE = Mappers.getMapper(ChallengeConfigMapper.class);

    ChallengeConfigDTO toDTO(ChallengeConfig challengeConfig);

    ChallengeConfig toEntity(ChallengeConfigDTO challengeConfigDTO);

    ChallengeConfig updateEntity(
            @MappingTarget ChallengeConfig challengeConfig, ChallengeConfigDTO challengeConfigDTO);
}
