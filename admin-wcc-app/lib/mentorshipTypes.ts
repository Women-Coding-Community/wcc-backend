export const MENTORSHIP_TYPE_VALUES = {
  AD_HOC: 'AD_HOC',
  LONG_TERM: 'LONG_TERM',
} as const;

export const MENTORSHIP_TYPES = [
  { value: MENTORSHIP_TYPE_VALUES.AD_HOC, label: 'Ad Hoc' },
  { value: MENTORSHIP_TYPE_VALUES.LONG_TERM, label: 'Long Term' },
];
