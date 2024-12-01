package com.project.dataCrawler.mappers;

import com.project.dataCrawler.domain.dtos.UserDetailsDto;
import com.project.dataCrawler.domain.entities.UserDetailsEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsMapper {

    public UserDetailsDto toDto(UserDetailsEntity userDetailsEntity) {
        return new UserDetailsDto(
                userDetailsEntity.getId(),
                userDetailsEntity.getRegNo(),
                userDetailsEntity.getDob(),
                userDetailsEntity.getFullName(),
                userDetailsEntity.getMobileNo(),
                userDetailsEntity.getEmailId(),
                userDetailsEntity.getStandard(),
                userDetailsEntity.getGender(),
                userDetailsEntity.getSchool(),
                userDetailsEntity.getRegion(),
                userDetailsEntity.getScore()
        );
    }

}
