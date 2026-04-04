export type ApplicationStatus =
  | 'PENDING'
  | 'MENTOR_REVIEWING'
  | 'MENTOR_ACCEPTED'
  | 'MENTOR_DECLINED'
  | 'MATCHED'
  | 'DROPPED'
  | 'REJECTED'
  | 'EXPIRED';

export interface MenteeApplication {
  applicationId: number;
  menteeId: number;
  mentorId: number;
  cycleId: number;
  priorityOrder: number;
  status: ApplicationStatus;
  applicationMessage?: string;
  whyMentor: string;
  appliedAt: string;
  reviewedAt?: string;
  matchedAt?: string;
  mentorResponse?: string;
  createdAt: string;
  updatedAt: string;
  reviewed: boolean;
  matched: boolean;
  daysSinceApplied: number;
}

export interface MenteeSkillArea {
  technicalArea: string;
  proficiencyLevel?: string;
}

export interface MenteeSkillLanguage {
  language: string;
  proficiencyLevel?: string;
}

export interface MenteeSkills {
  yearsExperience?: number;
  areas?: MenteeSkillArea[];
  languages?: MenteeSkillLanguage[];
  mentorshipFocus?: string[];
  mentorshipType?: string[];
}

export interface MenteeImage {
  url: string;
  name?: string;
}

export interface MenteeApplicationReview {
  applicationId: number;
  menteeId: number;
  fullName: string;
  position: string;
  yearsExperience?: number;
  linkedinUrl?: string;
  slackDisplayName: string;
  email: string;
  mentorshipGoal: string;
}

export interface DashboardMentee {
  id: number;
  fullName: string;
  position?: string;
  email?: string;
  profileStatus?: string;
  skills?: MenteeSkills;
  images?: MenteeImage[];
  bio?: string;
}
