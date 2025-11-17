#!/usr/bin/env bash
set -euo pipefail

# --------------------------------------------
# WCC Backend seeding script
# Creates Mentors page, adds sample mentor,
# and fetches mentor list for verification.
# --------------------------------------------

API_BASE="http://localhost:8080/api"
API_KEY="dev-key"

echo "🚀 Starting WCC backend seed..."

# 1️⃣ Create Mentors Page
echo "➡️ Creating Mentors Page..."
curl -s -X POST "${API_BASE}/platform/v1/page?pageType=MENTORS" \
  -H "accept: */*" \
  -H "X-API-KEY: ${API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "page:MENTORS_PAGE",
    "heroSection": {
      "title": "Meet Our Mentors!",
      "customStyle": {
        "backgroundColour": {
          "color": "primary",
          "shade": {
            "name": "light",
            "value": 90
          }
        }
      }
    },
    "mentors": []
  }'
echo " "
echo "✅  Mentors page created."
echo " "

# 2️⃣ Add Mentor
echo "➡️ Adding Mentors..."
curl -s -X POST "${API_BASE}/platform/v1/mentors" \
  -H "accept: */*" \
  -H "X-API-KEY: ${API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
        "fullName": "Sonali Goel",
        "position": "Lead Engineer",
        "email": "goelsonali@gmail.com",
        "slackDisplayName": "@Sonali",
        "country": { "countryCode": "GB",  "countryName": "United Kingdom"},
        "city": "London",
        "companyName": "Tesco Technology",
        "memberTypes": ["MENTOR", "LEADER"],
        "images": [],
        "network": [
          {
            "type": "LINKEDIN",
            "link": "https://www.linkedin.com/in/sonali-goel-6b611522/"
          }
        ],
        "profileStatus": "ACTIVE",
        "skills": {
          "yearsExperience": 10,
          "areas": [ "BACKEND", "DISTRIBUTED_SYSTEMS", "Machine Learning"],
          "languages": [ "java", "javascript", "Python", "C++" ],
          "mentorshipFocus": [
            "Switch career to IT",
            "Grow from beginner to mid-level",
            "Grow beyond senior level"
          ]
        },
        "spokenLanguages": [ "english"],
        "bio": "Sonali serves as a Lead Engineer at Yoox-Net-a-porter, specializing in managing and constructing extensive-scale ecommerce solutions. She leverages Java-based commerce platforms and integrates them with open-source technologies like Spring Boot and Spring Batch. As an AWS-certified Solution Architect, she possesses deep knowledge of Amazon Web Services, complemented by proficiency in implementing continuous integration and continuous deployment (CICD) practices to ensure seamless software delivery. Additionally, she excels in architecting and implementing microservices-based architectures to drive agility and modularity in software development With over 13 years of experience in the technology industry, Sonali brings a wealth of expertise in steering technical direction and fostering high-performance outcomes. Her passion lies in nurturing a culture of continuous learning and innovation. Outside of her professional endeavors, Sonali actively volunteers with Women Who Code, advocating for gender diversity. She is also a creative enthusiast, utilizing design templates to convey ideas, thoughts, and emotions visually.",
        "menteeSection": {
          "mentorshipType": [ "LONG_TERM", "AD_HOC"],
          "availability": [
            {
              "month": 6,
              "hours": 1
            },
            {
              "month": 7,
              "hours": 1
            }
          ],
          "idealMentee": "I am seeking mentees who are enthusiastic about learning and growing in the field of technology. Whether you are a beginner looking to enhance your programming skills or an experienced professional aiming to delve deeper into cloud architecture and e-commerce systems, I am here to support your journey. I value proactive communication, eagerness to learn, and a collaborative spirit. If you are committed to expanding your knowledge and skills in software development, AWS architecture, CI/CD pipelines, and Terraform, I would be delighted to work with you.",
          "additional": "Career Growth and Development, Resume Review, Preparation for Technical Interviews, Leadership and Team Management Skills, Learning Resources and Skill Development Plans."
        }
      }'
echo " "
echo "✅  Mentor Sonali added."
echo " "

curl -s -X POST "${API_BASE}/platform/v1/mentors" \
  -H "accept: */*" \
  -H "X-API-KEY: ${API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
        "fullName": "Adriana Zencke Zimmermann",
        "position": "Senior Software Engineer",
        "email": "adriana@womencodingcommunity.com",
        "slackDisplayName": "@Adriana Zencke",
        "country": {
          "countryCode": "ES",
          "countryName": "Spain"
        },
        "city": "Valencia",
        "companyName": "Centric Software",
        "memberTypes": [ "MENTOR", "DIRECTOR"],
        "images": [],
        "network": [
          { "type": "LINKEDIN", "link": "https://www.linkedin.com/in/dricazenck/" },
          { "type": "GITHUB", "link": "https://github.com/dricazenck" }
        ],
        "profileStatus": "ACTIVE",
        "skills": {
          "yearsExperience": 15,
          "areas": [ "BACKEND", "FULLSTACK", "DISTRIBUTED_SYSTEMS" ],
          "languages": [ "java", "javascript", "Kotlin" ],
          "mentorshipFocus": [
            "Grow from beginner to mid-level",
            "Grow beyond senior level"
          ]
        },
        "spokenLanguages": [ "portuguese", "english", "spanish"],
        "bio": "I am a mother, a wife, and a Software Engineer. I graduated in Computer Science and have over 14 years of experience working in tech companies in Brazil, Germany, and Spain. As Backend Engineer, I found this to be my passion which I focused on in the last six years of my career. I am always excited to help others, and my goal is to empower women and support them with the difficulties I had as an engineer, and I still have from time to time.",
        "menteeSection": {
          "mentorshipType": [ "LONG_TERM",  "AD_HOC" ],
          "availability": [
            {
              "month": 6,
              "hours": 2
            },
            {
              "month": 7,
              "hours": 2
            },
            {
              "month": 8,
              "hours": 2
            },
            {
              "month": 9,
              "hours": 2
            },
            {
              "month": 10,
              "hours": 2
            },
            {
              "month": 11,
              "hours": 2
            }
          ],
          "idealMentee": "Someone willing to learn new technologies or to improve current skills",
          "additional": "Spring Boot and Best Practices"
        }
      }'
echo ""
echo "✅  Mentor Adriana added."
echo ""

# 3️⃣ List Mentors
echo "➡️ Fetching all mentors..."
curl -s -X GET "${API_BASE}/cms/v1/mentorship/mentors?currentPage=1&pageSize=0" \
  -H "accept: */*" \
  -H "X-API-KEY: ${API_KEY}" | jq '.'
echo ""
echo "✅  Mentors."
echo ""

echo "➡️ Add Member..."
curl -s -X POST "${API_BASE}/platform/v1/members" \
  -H "accept: */*" \
  -H "X-API-KEY: ${API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
  "fullName": "Nevena",
  "position": "Software Engineer",
  "email": "nevena@email.com",
  "slackDisplayName": "nevena",
  "country": {
    "countryCode": "SI",
    "countryName": "Slovenia"
  },
  "city": "Ljubljana",
  "companyName": "VisionAriser",
  "memberTypes": [
    "MEMBER", "VOLUNTEER", "COLLABORATOR"
  ],
  "network": [
    {
      "type": "LINKEDIN",
      "link": "https://www.linkedin.com/in/nevena-verbi%C4%8D-83a16ab"
    }
  ]
}'
echo " "
echo "✅  Done!"
