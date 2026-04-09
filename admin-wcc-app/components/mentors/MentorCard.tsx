import { Avatar, Box, Chip, Link, Paper, Stack, Typography } from '@mui/material';
import BioSection from '@/components/mentors/BioSection';
import SkillsSection from '@/components/mentors/SkillsSection';
import { MentorItem } from '@/types/mentor';

interface MentorCardProps {
  mentor: MentorItem;
}

function prettyLocation(mentor: MentorItem): string {
  const parts = [mentor.city, mentor.country?.countryName || mentor.country?.countryCode].filter(
    Boolean
  );
  return parts.join(', ');
}

export default function MentorCard({ mentor }: MentorCardProps) {
  return (
    <Paper sx={{ p: 2, mb: 2 }}>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="flex-start">
        <Avatar sx={{ bgcolor: 'primary.main', width: 60, height: 60 }}>
          {(mentor.fullName || '?').substring(0, 1)}
        </Avatar>
        <Box sx={{ flex: 1 }}>
          <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
            {mentor.fullName}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {[mentor.position, mentor.companyName].filter(Boolean).join(' @ ')}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {prettyLocation(mentor)}
          </Typography>

          {mentor.skills && <SkillsSection skills={mentor.skills} />}

          {mentor.spokenLanguages && mentor.spokenLanguages.length > 0 && (
            <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
              {mentor.spokenLanguages.map((l) => (
                <Chip key={`spoken-${l}`} label={l} size="small" variant="outlined" />
              ))}
            </Box>
          )}

          {(() => {
            const section = mentor.menteeSection;
            const types: string[] = [];
            if (section?.longTerm) types.push('LONG_TERM');
            if (section?.adHoc?.length) types.push('AD_HOC');
            return types.length > 0 ? (
              <Box sx={{ mt: 2, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {types.map((t) => (
                  <Chip key={`type-${t}`} label={t} size="small" color="success" />
                ))}
              </Box>
            ) : null;
          })()}

          {mentor.bio && <BioSection bio={mentor.bio} />}

          {mentor.network && mentor.network.length > 0 && (
            <Stack direction="row" spacing={2} flexWrap="wrap" sx={{ mt: 2 }}>
              {mentor.network.map((n) => (
                <Link
                  key={`network-${n.type}`}
                  href={n.link}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  {n.type}
                </Link>
              ))}
            </Stack>
          )}
        </Box>
      </Stack>
    </Paper>
  );
}
