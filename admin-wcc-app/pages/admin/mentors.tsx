import {useEffect, useState} from 'react';
import {Alert, Avatar, Box, Chip, Link, Paper, Stack, Typography} from '@mui/material';
import AdminLayout from '@/components/AdminLayout';
import {apiFetch} from '@/lib/api';
import {getStoredToken, isTokenExpired} from '@/lib/auth';
import {useRouter} from 'next/router';

// API response types based on /api/platform/v1/mentors
interface MentorNetwork {
  type: string;
  link: string;
}

interface MentorSkills {
  yearsExperience?: number;
  areas?: string[];
  languages?: string[];
  mentorshipFocus?: string[];
}

interface MentorCountry {
  countryCode?: string;
  countryName?: string;
}

interface MenteeSection {
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

// Some APIs paginate; support a few known shapes
type MentorsResponse = MentorItem[] | {
  items?: MentorItem[];
  content?: MentorItem[];
  data?: MentorItem[]
};

const CMS_MENTORS_PATH = '/api/platform/v1/mentors';

export default function MentorsPage() {
  const router = useRouter();
  const [items, setItems] = useState<MentorItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = getStoredToken();
    if (!token || isTokenExpired(token)) router.replace('/login');
    else {
      load(token);
    }
  }, [router]);

  const normalize = (resp: MentorsResponse): MentorItem[] => {
    if (Array.isArray(resp)) return resp;
    if (resp?.items && Array.isArray(resp.items)) return resp.items;
    if ((resp as any)?.content && Array.isArray((resp as any).content)) return (resp as any).content;
    if ((resp as any)?.data && Array.isArray((resp as any).data)) return (resp as any).data;
    return [];
  };

  const load = async (t: string) => {
    try {
      const data = await apiFetch<MentorsResponse>(CMS_MENTORS_PATH, {token: t});
      setItems(normalize(data));
    } catch (e: any) {
      setError(e.message);
    }
  };

  const prettyLocation = (m: MentorItem) => {
    const parts = [m.city, m.country?.countryName || m.country?.countryCode].filter(Boolean);
    return parts.join(', ');
  };

  const getLinkedIn = (m: MentorItem) => m.network?.find(n => n.type?.toLowerCase() === 'linkedin')?.link;

  return (
      <AdminLayout>
        <Paper sx={{p: 3}}>
          <Typography variant="h5" gutterBottom>
            Mentors
          </Typography>
          {error && (
              <Alert severity="error" sx={{mb: 2}}>
                {error}
              </Alert>
          )}
          <Box>
            {items.map((m) => (
                <Paper key={m.id} sx={{p: 2, mb: 2}}>
                  <Stack direction={{xs: 'column', sm: 'row'}} spacing={2} alignItems="flex-start">
                    <Avatar sx={{bgcolor: 'primary.main', width: 60, height: 60}}>
                      {(m.fullName || '?').substring(0, 1)}
                    </Avatar>
                    <Box sx={{flex: 1}}>
                      <Typography variant="subtitle1" sx={{fontWeight: 600}}>
                        {m.fullName}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {[m.position, m.companyName].filter(Boolean).join(' @ ')}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {prettyLocation(m)}
                      </Typography>

                      {/* Skills */}
                      {m.skills && (
                          <Stack direction="row" spacing={1} flexWrap="wrap" sx={{mt: 1}}>
                            {m.skills.areas?.map((a) => (
                                <Chip key={`area-${m.id}-${a}`} label={a} size="small"/>
                            ))}
                            {m.skills.languages?.map((l) => (
                                <Chip key={`lang-${m.id}-${l}`} label={l} size="small"
                                      color="secondary"/>
                            ))}
                          </Stack>
                      )}

                      {/* Spoken Languages */}
                      {m.spokenLanguages && m.spokenLanguages.length > 0 && (
                          <Stack direction="row" spacing={1} flexWrap="wrap" sx={{mt: 1}}>
                            {m.spokenLanguages.map((l) => (
                                <Chip key={`spoken-${m.id}-${l}`} label={l} size="small"
                                      variant="outlined"/>
                            ))}
                          </Stack>
                      )}

                      {/* Mentorship Type */}
                      {m.menteeSection?.mentorshipType && m.menteeSection.mentorshipType.length > 0 && (
                          <Stack direction="row" spacing={1} flexWrap="wrap" sx={{mt: 1}}>
                            {m.menteeSection.mentorshipType.map((t) => (
                                <Chip key={`type-${m.id}-${t}`} label={t} size="small"
                                      color="success"/>
                            ))}
                          </Stack>
                      )}

                      {/* Bio (short) */}
                      {m.bio && (
                          <Typography variant="body2" sx={{mt: 1}}>
                            {m.bio.length > 260 ? m.bio.substring(0, 260) + '…' : m.bio}
                          </Typography>
                      )}

                      {/* Network */}
                      {getLinkedIn(m) && (
                          <Typography variant="body2" sx={{mt: 1}}>
                            <Link href={getLinkedIn(m)!} target="_blank" rel="noopener noreferrer">
                              LinkedIn
                            </Link>
                          </Typography>
                      )}
                    </Box>
                  </Stack>
                </Paper>
            ))}
            {items.length === 0 && !error && (
                <Typography color="text.secondary">No mentors found.</Typography>
            )}
          </Box>
        </Paper>
      </AdminLayout>
  );
}
