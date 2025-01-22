package com.wcc.platform.domain.cms.pages.aboutUs;

import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.platform.Member;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * CMS Celebrate Her page.
 *
 * @param page Page details as title and images
 * @param members all details
 */
public record CelebrateHerPage(
    @NotNull Page page,
    @NotNull List<Member> members) {}
