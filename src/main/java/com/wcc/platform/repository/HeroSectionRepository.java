package com.wcc.platform.repository;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import java.util.Optional;

public interface HeroSectionRepository {
  Optional<HeroSection> findByPage(final String pageName);

  void save(final String pageName, final HeroSection heroSection);

  void update(final String pageName, final HeroSection heroSection);
}
