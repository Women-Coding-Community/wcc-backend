// package com.wcc.platform.integrationtests;
//
// import static com.wcc.platform.factories.SetupFactories.createMemberTest;
// import static com.wcc.platform.factories.SetupFactories.deleteFile;
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// import com.wcc.platform.domain.platform.MemberType;
// import com.wcc.platform.service.PlatformService;
// import java.io.File;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
// import org.springframework.test.context.ActiveProfiles;
//
// @ActiveProfiles("test")
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// class PlatformServiceIntegrationTest extends SurrealDbIntegrationTest {
//
//  private final File testFile;
//  @Autowired private PlatformService service;
//
//  public PlatformServiceIntegrationTest(
//      @Value("${file.storage.directory}") final String directoryPath) {
//    super();
//    String testFileName = "members.json";
//    testFile = new File(directoryPath + File.separator + testFileName);
//  }
//
//  @BeforeEach
//  void deleteFileContent() {
//    deleteFile(testFile);
//  }
//
//  @Test
//  void testSaveMember() {
//    var member = createMemberTest(MemberType.MEMBER);
//    var result = service.createMember(member);
//
//    assertEquals(member, result);
//  }
//
//  @Test
//  void testGetAll() {
//    var total = service.getAll().size();
//
//    var member = createMemberTest(MemberType.MEMBER);
//    service.createMember(member);
//
//    var result = service.getAll();
//
//    assertEquals(total + 1, result.size());
//  }
// }
