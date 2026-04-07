import { useEffect, useState } from 'react';
import { Alert, Box, Button, CircularProgress, Paper, Stack, Typography } from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { API_BASE, API_KEY, apiFetch } from '@/lib/api';
import { getStoredToken } from '@/lib/auth';
import { EditMentorFormData, editMentorSchema } from './schema';
import ProfilePictureSection from './ProfilePictureSection';
import PersonalInfoSection from './PersonalInfoSection';
import BioSection from './BioSection';
import SkillsSection from './SkillsSection';
import MentorshipAvailabilitySection from './MentorshipAvailabilitySection';
import ResourcesSection from './ResourcesSection';
import { getMentorById } from '@/services/mentorService';
import { MentorItem } from '@/types/mentor';

const MONTHS = [
  'JANUARY',
  'FEBRUARY',
  'MARCH',
  'APRIL',
  'MAY',
  'JUNE',
  'JULY',
  'AUGUST',
  'SEPTEMBER',
  'OCTOBER',
  'NOVEMBER',
  'DECEMBER',
];

function deriveMentorshipType(menteeSection?: MentorItem['menteeSection']): string[] {
  const types: string[] = [];
  if (menteeSection?.longTerm) types.push('LONG_TERM');
  if (menteeSection?.adHoc?.length) types.push('AD_HOC');
  return types;
}

function buildDefaultValues(mentor: MentorItem): EditMentorFormData {
  const adHocMap = new Map((mentor.menteeSection?.adHoc ?? []).map((a) => [a.month, a.hours]));

  const booksRaw = mentor.resources?.books;
  const booksString = Array.isArray(booksRaw) ? booksRaw.join('\n') : (booksRaw ?? '');

  return {
    fullName: mentor.fullName ?? '',
    email: mentor.email ?? '',
    position: mentor.position ?? '',
    slackDisplayName: mentor.slackDisplayName ?? '',
    companyName: mentor.companyName ?? '',
    city: mentor.city ?? '',
    country: mentor.country
      ? {
          countryCode: mentor.country.countryCode ?? '',
          countryName: mentor.country.countryName ?? '',
        }
      : null,
    spokenLanguages: mentor.spokenLanguages ?? [],
    bio: mentor.bio ?? '',
    yearsExperience: mentor.skills?.yearsExperience ?? 0,
    technicalAreas: (mentor.skills?.areas ?? []).map((a) => ({
      technicalArea: a.technicalArea,
      proficiencyLevel: a.proficiencyLevel ?? '',
    })),
    programmingLanguages: (mentor.skills?.languages ?? []).map((l) => ({
      language: l.language,
      proficiencyLevel: l.proficiencyLevel ?? '',
    })),
    mentorshipFocus: mentor.skills?.mentorshipFocus ?? [],
    mentorshipType: deriveMentorshipType(mentor.menteeSection),
    idealMentee: mentor.menteeSection?.idealMentee ?? '',
    additionalInfo: mentor.menteeSection?.additional ?? '',
    monthAvailability: MONTHS.map((month) => ({
      month,
      hours: adHocMap.get(month) ?? 0,
      enabled: adHocMap.has(month) && (adHocMap.get(month) ?? 0) > 0,
    })),
    books: booksString,
    links: mentor.resources?.links ?? [],
    network: mentor.network ?? [],
  };
}

interface EditMentorFormProps {
  mentorId: string;
}

export default function EditMentorForm({ mentorId }: EditMentorFormProps) {
  const [mentor, setMentor] = useState<MentorItem | null>(null);
  const [fetchLoading, setFetchLoading] = useState(true);
  const [fetchError, setFetchError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [profilePictureUrl, setProfilePictureUrl] = useState<string | undefined>(undefined);
  const [profilePictureUploading, setProfilePictureUploading] = useState(false);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<EditMentorFormData>({
    resolver: zodResolver(editMentorSchema),
    mode: 'onBlur',
  });

  useEffect(() => {
    const token = getStoredToken();
    if (!token) return;

    setFetchLoading(true);
    setFetchError(null);

    Promise.all([
      getMentorById(mentorId, token),
      apiFetch<{ resource: { driveFileLink: string } }>(
        `/api/platform/v1/resources/member-profile-picture/${mentorId}`,
        { token }
      ).catch(() => null),
    ])
      .then(([fetchedMentor, pictureData]) => {
        if (!fetchedMentor) {
          setFetchError(`Mentor with ID ${mentorId} not found`);
          return;
        }
        setMentor(fetchedMentor);
        reset(buildDefaultValues(fetchedMentor));
        if (pictureData?.resource?.driveFileLink) {
          setProfilePictureUrl(pictureData.resource.driveFileLink);
        }
      })
      .catch((e: unknown) => {
        setFetchError(e instanceof Error ? e.message : 'Failed to load mentor data');
      })
      .finally(() => setFetchLoading(false));
  }, [mentorId, reset]);

  const transformFormData = (data: EditMentorFormData) => ({
    fullName: data.fullName,
    position: data.position,
    email: data.email,
    slackDisplayName: data.slackDisplayName,
    country: {
      countryCode: data.country?.countryCode,
      countryName: data.country?.countryName,
    },
    city: data.city,
    companyName: data.companyName,
    memberTypes: ['MENTOR'],
    network: data.network,
    bio: data.bio,
    spokenLanguages: data.spokenLanguages,
    skills: {
      yearsExperience: Number(data.yearsExperience),
      areas: data.technicalAreas,
      languages: data.programmingLanguages,
      mentorshipFocus: data.mentorshipFocus,
    },
    menteeSection: {
      idealMentee: data.idealMentee,
      additional: data.additionalInfo,
      longTerm: data.mentorshipType.includes('LONG_TERM') ? { numMentee: 1, hours: 2 } : null,
      adHoc: data.mentorshipType.includes('AD_HOC')
        ? data.monthAvailability
            .filter((m) => m.enabled)
            .map((m) => ({ month: m.month, hours: m.hours }))
        : [],
    },
    resources: {
      books: (data.books ?? '')
        .split('\n')
        .map((b) => b.trim())
        .filter(Boolean),
      links: data.links,
    },
  });

  const handleProfilePictureUpload = async (file: File) => {
    const token = getStoredToken();
    if (!token) {
      setApiError('Authentication token not found. Please login again.');
      return;
    }

    setProfilePictureUploading(true);
    setApiError(null);

    try {
      const formData = new FormData();
      formData.append('file', file);

      const res = await fetch(
        `${API_BASE}/api/platform/v1/resources/member-profile-picture?memberId=${mentorId}`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`,
            ...(API_KEY ? { 'X-API-KEY': API_KEY } : {}),
          },
          body: formData,
          credentials: 'include',
        }
      );

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data?.message ?? `${res.status} ${res.statusText}`);
      }

      const data = await res.json();
      setProfilePictureUrl(data.resource.driveFileLink);
    } catch (e: unknown) {
      setApiError(e instanceof Error ? e.message : 'Failed to upload profile picture');
    } finally {
      setProfilePictureUploading(false);
    }
  };

  const onSubmit = async (data: EditMentorFormData) => {
    setLoading(true);
    setApiError(null);
    setSuccessMessage(null);

    try {
      const token = getStoredToken();
      if (!token) {
        setApiError('Authentication token not found. Please login again.');
        setLoading(false);
        return;
      }

      await apiFetch(`/api/platform/v1/mentors/${mentorId}`, {
        method: 'PUT',
        body: transformFormData(data),
        token,
      });

      setSuccessMessage('Profile updated successfully!');
    } catch (e: unknown) {
      setApiError(e instanceof Error ? e.message : 'Failed to update profile');
    } finally {
      setLoading(false);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const SaveButton = ({ fullWidth = false }: { fullWidth?: boolean }) => (
    <Button
      type="submit"
      variant="contained"
      color="primary"
      disabled={loading || fetchLoading}
      startIcon={loading ? <CircularProgress size={20} /> : <SaveIcon />}
      size="large"
      fullWidth={fullWidth}
      sx={fullWidth ? { py: 1.5 } : undefined}
    >
      {loading ? 'Saving...' : 'Save Profile'}
    </Button>
  );

  if (fetchLoading) {
    return (
      <Box display="flex" justifyContent="center" mt={6}>
        <CircularProgress />
      </Box>
    );
  }

  if (fetchError) {
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        {fetchError}
      </Alert>
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <Paper sx={{ p: 3, mb: 3 }}>
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <Box>
            <Typography variant="h5" sx={{ fontWeight: 600 }}>
              Profile Editor
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Update profile information
            </Typography>
          </Box>
          <SaveButton />
        </Box>
      </Paper>

      {successMessage && (
        <Alert severity="success" sx={{ mb: 3 }} onClose={() => setSuccessMessage(null)}>
          {successMessage}
        </Alert>
      )}
      {apiError && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setApiError(null)}>
          {apiError}
        </Alert>
      )}

      <Stack spacing={3}>
        <ProfilePictureSection
          fullName={mentor?.fullName ?? ''}
          profileStatus={mentor?.profileStatus}
          imageUrl={profilePictureUrl}
          onPictureChange={handleProfilePictureUpload}
          uploading={profilePictureUploading}
        />
        <PersonalInfoSection control={control} errors={errors} />
        <BioSection control={control} errors={errors} />
        <SkillsSection control={control} errors={errors} />
        <MentorshipAvailabilitySection control={control} errors={errors} />
        <ResourcesSection control={control} />
      </Stack>

      <Box sx={{ mt: 3 }}>
        <SaveButton fullWidth />
      </Box>
    </form>
  );
}
