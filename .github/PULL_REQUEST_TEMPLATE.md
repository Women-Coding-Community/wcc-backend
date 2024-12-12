## Description

- Describe your changes in detail.
- Why is this change required?
- What problem does it solve?
- Add Notes if anything is left unclear or not completed

*Example:*

*PR for feat: Create Member rest api - PUT*

*PUT operation will update Member's data. Not all the data is allowed to be updated.
That is why the MemberDTO class is created, with just some of the Member's class fields.*

*Implemented PUT operation for Member (general type of MEMBER) in the MemberController.*

*Implemented service method public Member updateMember(String email, MemberDto memberDto) that
updates member from the \wcc-backend\data\members.json file.*

*NOTE: As a temp. solution data is saved in the members.json file (located outside of the resources
since resources are read-only files) in the \wcc-backend\data\ folder. When testing (integration
tests) another members.json file is used, located in \wcc-backend\data-test folder. To switch
between different configurations (testing, development, production) we have defined different
config. files (\wcc-backend\src\main\resources\application.yml,
\wcc-backend\src\main\resources\application-docker.yml,
\wcc-backend\src\test\resources\application-test.yml)*

## Related Issue

Please link to the issue here
<!--- If suggesting a new feature or change, please discuss it in an issue first -->
<!--- If fixing a bug, there should be an issue describing it with steps to reproduce -->

## Change Type

- [ ] Bug Fix
- [ ] New Feature
- [ ] Code Refactor
- [ ] Mentor Update
- [ ] Data Update
- [ ] Documentation
- [ ] Other

## Screenshots

<!--  If you are changing html, css or new resources it is mandatory to add screenshot. -->
<!--  Please add screenshot from *before* and *after* to simplify the code review -->

## Pull request checklist

Please check if your PR fulfills the following requirements:

- [ ] I checked and followed the [contributor guide](../CONTRIBUTING.md)
- [ ] I have tested my changes locally.
- [ ] I have added a screenshot from the website after I tested it locally

<!--  Thanks for sending a pull request! -->