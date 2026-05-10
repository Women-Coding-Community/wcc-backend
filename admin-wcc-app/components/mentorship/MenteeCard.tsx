import React, { useState } from 'react';
import {
  Avatar,
  Box,
  Card,
  CardContent,
  Chip,
  Collapse,
  Divider,
  Grid,
  Grid2,
  IconButton,
  Link,
  Stack,
  Typography,
} from '@mui/material';
import {
  Business as BusinessIcon,
  ExpandLess as ExpandLessIcon,
  ExpandMore as ExpandMoreIcon,
  Language as LanguageIcon,
  Public as PublicIcon,
  Work as WorkIcon,
} from '@mui/icons-material';
import { MenteeItem } from '@/types/mentorship';
import LinkedInIcon from '@mui/icons-material/LinkedIn';

interface MenteeCardProps {
  mentee: MenteeItem;
  score?: number;
}

export default function MenteeCard({ mentee, score }: MenteeCardProps) {
  const [expanded, setExpand] = useState(false);

  const location = [mentee.city, mentee.country?.countryName].filter(Boolean).join(', ');

  return (
    <Card variant="outlined" sx={{ mb: 2, position: 'relative' }}>
      {score !== undefined && (
        <Box
          sx={{
            position: 'absolute',
            top: 10,
            right: 10,
            bgcolor: 'primary.main',
            color: 'white',
            borderRadius: '50%',
            width: 40,
            height: 40,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 'bold',
            zIndex: 1,
          }}
        >
          {score}
        </Box>
      )}
      <CardContent>
        <Stack direction="row" spacing={2}>
          <Avatar
            src={mentee.images?.[0]?.path}
            alt={mentee.fullName}
            sx={{ width: 56, height: 56 }}
          >
            {mentee.fullName.charAt(0)}
          </Avatar>
          <Box sx={{ flex: 1 }}>
            <Typography variant="h6">{mentee.fullName}</Typography>
            <Stack direction="row" spacing={1} alignItems="center" color="text.secondary">
              <Typography variant="body2">Mentee Id: {mentee.id}</Typography>
              {mentee.network && mentee.network.length > 0 && (
                <Stack direction="row" spacing={2}>
                  {mentee.network.map((n) => (
                    <Link key={n.link} href={n.link} target="_blank" rel="noopener">
                      <LinkedInIcon fontSize="small" sx={{ color: '#0077b5' }} />
                    </Link>
                  ))}
                </Stack>
              )}
            </Stack>
            <Stack direction="row" spacing={1} alignItems="center" color="text.secondary">
              <WorkIcon fontSize="small" />
              <Typography variant="body2">{mentee.position || 'N/A'}</Typography>
            </Stack>
            <Stack direction="row" spacing={1} alignItems="center" color="text.secondary">
              <BusinessIcon fontSize="small" />
              <Typography variant="body2">{mentee.companyName || 'N/A'}</Typography>
            </Stack>
            <Stack direction="row" spacing={1} alignItems="center" color="text.secondary">
              <PublicIcon fontSize="small" />
              <Typography variant="body2">{location || 'N/A'}</Typography>
            </Stack>
          </Box>
          <IconButton onClick={() => setExpand(!expanded)}>
            {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
          </IconButton>
        </Stack>

        <Collapse in={expanded} timeout="auto" unmountOnExit>
          <Box sx={{ mt: 2 }}>
            <Divider sx={{ mb: 2 }} />

            <Typography variant="subtitle2" gutterBottom>
              Bio
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              {mentee.bio || 'No bio provided.'}
            </Typography>

            <Grid2 container spacing={2}>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle2" gutterBottom>
                  Skills & Proficiency
                </Typography>
                <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                  {mentee.skills?.areas?.map((a) => (
                    <Chip
                      key={a.technicalArea}
                      label={`${a.technicalArea.replace(/_/g, ' ')} (${a.proficiencyLevel})`}
                      size="small"
                    />
                  ))}
                </Stack>
              </Grid>

              <Box sx={{ mt: 1 }}>
                <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                  {mentee.skills?.mentorshipFocus?.map((f) => (
                    <Chip
                      key={f}
                      label={f.replace(/_/g, ' ')}
                      size="small"
                      color="info"
                      variant="outlined"
                    />
                  ))}
                </Stack>
              </Box>

              <Grid item xs={12} md={6}>
                <Typography variant="subtitle2" gutterBottom>
                  Languages
                </Typography>
                <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                  {mentee.spokenLanguages?.map((l) => (
                    <Chip key={l} label={l} size="small" icon={<LanguageIcon />} />
                  ))}
                </Stack>
              </Grid>

              <Box sx={{ mt: 2 }}>
                <Typography variant="body2">
                  <strong>Available hours/month:</strong> {mentee.availableHsMonth || 'N/A'}
                </Typography>
              </Box>
            </Grid2>
          </Box>
        </Collapse>
      </CardContent>
    </Card>
  );
}
