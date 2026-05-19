import React from 'react';
import {Avatar, Box, Card, CardContent, Stack, Typography} from '@mui/material';
import {
  Business as BusinessIcon,
  Public as PublicIcon,
  Work as WorkIcon,
} from '@mui/icons-material';
import {MentorItem} from '@/types/mentor';
import SkillsSection from '@/components/mentors/SkillsSection';
import BioSection from '@/components/mentors/BioSection';

interface MentorInfoCardProps {
  mentor: MentorItem;
}

export default function MentorInfoCard({mentor}: MentorInfoCardProps) {
  const location = [mentor.city, mentor.country?.countryName].filter(Boolean).join(', ');

  return (
      <Card variant="outlined" sx={{mb: 2, bgcolor: 'grey.50'}}>
        <CardContent>
          <Stack direction="row" spacing={2}>
            <Avatar
                src={
                  typeof mentor.images?.[0] === 'string'
                      ? mentor.images[0]
                      : (mentor.images?.[0] as any)?.path
                }
                alt={mentor.fullName}
                sx={{width: 64, height: 64}}
            >
              {mentor.fullName.charAt(0)}
            </Avatar>
            <Box sx={{flex: 1}}>
              <Typography variant="h6">{mentor.fullName}</Typography>
              <Stack direction="row" spacing={1} alignItems="center" color="text.secondary">
                <WorkIcon fontSize="small"/>
                <Typography variant="body2">{mentor.position || 'N/A'}</Typography>
              </Stack>
              <Stack direction="row" spacing={1} alignItems="center" color="text.secondary">
                <BusinessIcon fontSize="small"/>
                <Typography variant="body2">{mentor.companyName || 'N/A'}</Typography>
              </Stack>
              <Stack direction="row" spacing={1} alignItems="center" color="text.secondary">
                <PublicIcon fontSize="small"/>
                <Typography variant="body2">{location || 'N/A'}</Typography>
              </Stack>
            </Box>
          </Stack>

          <Box sx={{mt: 1}}>{mentor.bio && <BioSection bio={mentor.bio}/>}</Box>
          <Box sx={{mt: 1}}>{mentor.skills && <SkillsSection skills={mentor.skills}/>}</Box>
          <Box sx={{mt: 2}}>
            <Stack direction="row" spacing={2}>
              <Typography variant="body2">
                <strong>Availability Long Term: </strong>
                {mentor.menteeSection?.longTerm?.numMentee || 0} mentees,{' '}
                {mentor.menteeSection?.longTerm?.hours || 0} hours
              </Typography>
            </Stack>
          </Box>
          <Box sx={{mt: 2}}>
            <Typography variant="subtitle2" gutterBottom>
              Ideal Mentee
            </Typography>
            <Stack direction="row" spacing={2}>
              <Typography variant="body2">{mentor.menteeSection?.idealMentee}</Typography>
            </Stack>
          </Box>
          <Box sx={{mt: 2}}>
            <Typography variant="subtitle2" gutterBottom>
              Additional
            </Typography>
            <Stack direction="row" spacing={2}>
              <Typography variant="body2">{mentor.menteeSection?.additional}</Typography>
            </Stack>
          </Box>
        </CardContent>
      </Card>
  );
}
