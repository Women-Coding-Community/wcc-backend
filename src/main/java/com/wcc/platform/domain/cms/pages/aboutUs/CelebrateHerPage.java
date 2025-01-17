package com.wcc.platform.domain.cms.pages.aboutUs;

<<<<<<< HEAD
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.platform.Member;
=======
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.MemberByType;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.Section;
import com.wcc.platform.domain.platform.Member;
import jakarta.validation.constraints.NotEmpty;
>>>>>>> 72da30f (Initial commit for CelebrateHer page)
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
<<<<<<< HEAD
 * CMS Celebrate Her page.
=======
 * CMS About Us page.
>>>>>>> 72da30f (Initial commit for CelebrateHer page)
 *
 * @param page Page details as title and images
 * @param members all details
 */
public record CelebrateHerPage(
    @NotNull Page page,
    @NotNull List<Member> members) {}
