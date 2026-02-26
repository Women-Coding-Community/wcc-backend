export interface MentorNetwork {
  type: string;
  link: string;
}

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

export interface MenteeSection {
  mentorshipType?: string[];
  availability?: any[];
  idealMentee?: string;
  additional?: string;
}

export interface MentorItem {
  id: number | string;
  fullName: string;
  position?: string;
  country?: MentorCountry;
  city?: string;
  companyName?: string;
  images?: string[];
  network?: MentorNetwork[];
  skills?: MentorSkills;
  spokenLanguages?: string[];
  bio?: string;
  menteeSection?: MenteeSection;
}
