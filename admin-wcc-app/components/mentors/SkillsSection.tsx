import { Box, Chip, Typography } from '@mui/material';
import { MentorSkills } from '@/types/mentor';
import { TECHNICAL_AREAS } from '@/lib/technicalAreas';
import { PROGRAMMING_LANGUAGES } from '@/lib/programmingLanguages';
import { MENTORSHIP_FOCUS_AREAS } from '@/lib/mentorshipFocusAreas';
import { PROFICIENCY_LEVELS } from '@/lib/proficiencyLevels';

interface SkillsSectionProps {
  skills: MentorSkills;
}

function areaLabel(value: string): string {
  return TECHNICAL_AREAS.find((a) => a.value === value)?.label ?? value;
}

function langLabel(value: string): string {
  return PROGRAMMING_LANGUAGES.find((l) => l.value === value)?.label ?? value;
}

function profLabel(value: string): string {
  return PROFICIENCY_LEVELS.find((p) => p.value === value)?.label ?? value;
}

function focusLabel(value: string): string {
  return MENTORSHIP_FOCUS_AREAS.find((f) => f.value === value)?.label ?? value;
}

export default function SkillsSection({ skills }: SkillsSectionProps) {
  return (
    <Box sx={{ mt: 2 }}>
      {skills.yearsExperience !== undefined && (
        <Typography variant="body2" color="text.secondary">
          {skills.yearsExperience} years experience
        </Typography>
      )}
      {skills.areas && skills.areas.length > 0 && (
        <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
          {skills.areas.map((a) => (
            <Chip
              key={a.technicalArea}
              label={
                a.proficiencyLevel
                  ? `${areaLabel(a.technicalArea)} · ${profLabel(a.proficiencyLevel)}`
                  : areaLabel(a.technicalArea)
              }
              size="small"
            />
          ))}
        </Box>
      )}
      {skills.languages && skills.languages.length > 0 && (
        <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
          {skills.languages.map((l) => (
            <Chip
              key={l.language}
              label={
                l.proficiencyLevel
                  ? `${langLabel(l.language)} · ${profLabel(l.proficiencyLevel)}`
                  : langLabel(l.language)
              }
              size="small"
              color="secondary"
            />
          ))}
        </Box>
      )}
      {skills.mentorshipFocus && skills.mentorshipFocus.length > 0 && (
        <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
          {skills.mentorshipFocus.map((f) => (
            <Chip key={f} label={focusLabel(f)} size="small" color="info" />
          ))}
        </Box>
      )}
    </Box>
  );
}
