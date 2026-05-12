import { MentorItem } from './mentor';

export interface MenteeItem {
  id: number;
  fullName: string;
  position?: string;
  email?: string;
  slackDisplayName?: string;
  country?: {
    countryCode: string;
    countryName: string;
  };
  city?: string;
  companyName?: string;
  memberTypes?: string[];
  images?: {
    path: string;
    alt: string;
    type: string;
  }[];
  network?: {
    type: string;
    link: string;
  }[];
  isWomen?: boolean;
  profileStatus?: string;
  skills?: {
    yearsExperience?: number;
    areas?: {
      technicalArea: string;
      proficiencyLevel: string;
    }[];
    languages?: {
      language: string;
      proficiencyLevel: string;
    }[];
    mentorshipFocus?: string[];
  };
  bio?: string;
  spokenLanguages?: string[];
  availableHsMonth?: number;
}

export interface MenteeMatchSuggestion {
  mentee: MenteeItem;
  score: number;
  applicationStatus?: string | null;
}

export interface MenteeApplicationItem {
  menteeId: number;
  mentee: MenteeItem;
  mentorId?: number;
  status: string;
  rejectionReason?: string;
  appliedAt?: string;
}

export interface MentorMatches {
  mentor: MentorItem;
  mentees: MenteeMatchSuggestion[];
}

export interface MentorshipRecommendationResponse {
  matchedMentors: MentorMatches[];
  notMatchedMentors: MentorItem[];
  notMatchedMentees: MenteeItem[];
}
