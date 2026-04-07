import type { SocialNetwork } from '@/types/member';
export type { SocialNetwork, SocialNetworkType, SocialNetworkTypeValue } from '@/types/member';

export interface MentorSkillArea {
  technicalArea: string;
  proficiencyLevel?: string;
}

export interface MentorSkillLanguage {
  language: string;
  proficiencyLevel?: string;
}

export interface MentorSkills {
  yearsExperience?: number;
  areas?: MentorSkillArea[];
  languages?: MentorSkillLanguage[];
  mentorshipFocus?: string[];
}

export interface MentorCountry {
  countryCode?: string;
  countryName?: string;
}

export interface MentorAdHocAvailability {
  month: string;
  hours: number;
}

export interface MentorLongTerm {
  numMentee?: number;
  hours?: number;
}

export interface MenteeSection {
  idealMentee?: string;
  additional?: string;
  longTerm?: MentorLongTerm | null;
  adHoc?: MentorAdHocAvailability[];
}

export interface MentorResources {
  books?: string[];
  links?: { title?: string; label?: string; uri: string }[];
}

export interface MentorItem {
  id: number | string;
  fullName: string;
  email?: string;
  slackDisplayName?: string;
  position?: string;
  profileStatus?: string;
  country?: MentorCountry;
  city?: string;
  companyName?: string;
  images?: string[];
  network?: SocialNetwork[];
  skills?: MentorSkills;
  spokenLanguages?: string[];
  bio?: string;
  menteeSection?: MenteeSection;
  resources?: MentorResources;
}
