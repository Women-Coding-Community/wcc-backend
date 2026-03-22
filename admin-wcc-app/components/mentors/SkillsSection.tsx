import { Box, Chip, Typography } from '@mui/material';
import { MentorSkills } from '@/types/mentor';

interface SkillsSectionProps {
  skills: MentorSkills;
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
                a.proficiencyLevel ? `${a.technicalArea} · ${a.proficiencyLevel}` : a.technicalArea
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
              label={l.proficiencyLevel ? `${l.language} · ${l.proficiencyLevel}` : l.language}
              size="small"
              color="secondary"
            />
          ))}
        </Box>
      )}
      {skills.mentorshipFocus && skills.mentorshipFocus.length > 0 && (
        <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
          {skills.mentorshipFocus.map((f) => (
            <Chip key={f} label={f} size="small" color="info" />
          ))}
        </Box>
      )}
    </Box>
  );
}
