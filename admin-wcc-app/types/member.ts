/**
 * Mirror of the Backend SocialNetworkType. Values must stay in sync with the backend enum.
 */
export const SocialNetworkType = {
  DEFAULT_LINK: 'default_link',
  EMAIL: 'email',
  FACEBOOK: 'facebook',
  GITHUB: 'github',
  INSTAGRAM: 'instagram',
  LINKEDIN: 'linkedin',
  MEETUP: 'meetup',
  MEDIUM: 'medium',
  SLACK: 'slack',
  UNKNOWN: 'unknown',
  WEBSITE: 'website',
  YOUTUBE: 'youtube',
} as const;

export type SocialNetworkTypeValue = (typeof SocialNetworkType)[keyof typeof SocialNetworkType];

export interface SocialNetwork {
  type: SocialNetworkTypeValue | string;
  link: string;
}
