package com.platform.common.repository;

import com.platform.common.GlobalConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration, Long> {
    
    Optional<GlobalConfiguration> findByConfigKeyAndIsActiveTrue(String configKey);
    
    List<GlobalConfiguration> findByIsActiveTrue();
    
    @Query("SELECT gc FROM GlobalConfiguration gc WHERE gc.configKey = :configKey AND gc.isActive = true")
    Optional<GlobalConfiguration> findActiveConfigByKey(@Param("configKey") String configKey);
    
    @Query("SELECT gc.configValue FROM GlobalConfiguration gc WHERE gc.configKey = :configKey AND gc.isActive = true")
    Optional<String> findConfigValueByKey(@Param("configKey") String configKey);
    
    boolean existsByConfigKeyAndIsActiveTrue(String configKey);
}
