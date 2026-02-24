import { useState } from 'react';
import { Button, Box, Alert, CircularProgress, Paper, Typography, Stack } from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { apiFetch, API_BASE, API_KEY } from '@/lib/api';
import { getStoredToken } from '@/lib/auth';
import { editMentorSchema, EditMentorFormData } from './schema';
import { MOCK_MENTOR } from './mockData';
import ProfilePictureSection from './ProfilePictureSection';
import PersonalInfoSection from './PersonalInfoSection';
import BioSection from './BioSection';
import SkillsSection from './SkillsSection';
import MentorshipAvailabilitySection from './MentorshipAvailabilitySection';
import ResourcesSection from './ResourcesSection';

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

function buildDefaultValues(mentor: typeof MOCK_MENTOR): EditMentorFormData {
  const adHocMap = new Map((mentor.menteeSection.adHoc || []).map((a) => [a.month, a.hours]));

  return {
    fullName: mentor.fullName,
    email: mentor.email,
    position: mentor.position,
    slackDisplayName: mentor.slackDisplayName,
    companyName: mentor.companyName,
    city: mentor.city,
    country: mentor.country,
    spokenLanguages: mentor.spokenLanguages,
    bio: mentor.bio,
    yearsExperience: mentor.skills.yearsExperience,
    technicalAreas: mentor.skills.areas,
    programmingLanguages: mentor.skills.languages,
    mentorshipFocus: mentor.skills.mentorshipFocus,
    mentorshipType: mentor.menteeSection.mentorshipType,
    idealMentee: mentor.menteeSection.idealMentee,
    additionalInfo: mentor.menteeSection.additional,
    monthAvailability: MONTHS.map((month) => ({
      month,
      hours: adHocMap.get(month) || 0,
      enabled: adHocMap.has(month) && (adHocMap.get(month) || 0) > 0,
    })),
    books: mentor.resources.books,
    links: mentor.resources.links,
    network: mentor.network,
  };
}

interface EditMentorFormProps {
  mentorId: string;
}

export default function EditMentorForm({ mentorId }: EditMentorFormProps) {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [profilePictureUrl, setProfilePictureUrl] = useState<string | undefined>(
    MOCK_MENTOR.profilePictureUrl
  );
  const [profilePictureUploading, setProfilePictureUploading] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<EditMentorFormData>({
    resolver: zodResolver(editMentorSchema),
    defaultValues: buildDefaultValues(MOCK_MENTOR),
    mode: 'onBlur',
  });

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
      disabled={loading}
      startIcon={loading ? <CircularProgress size={20} /> : <SaveIcon />}
      size="large"
      fullWidth={fullWidth}
      sx={fullWidth ? { py: 1.5 } : undefined}
    >
      {loading ? 'Saving...' : 'Save Profile'}
    </Button>
  );

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
          fullName={MOCK_MENTOR.fullName}
          profileStatus={MOCK_MENTOR.profileStatus}
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
