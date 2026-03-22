import { useState } from 'react';
import { Button, Box, Alert, CircularProgress } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { apiFetch } from '@/lib/api';
import { getStoredToken } from '@/lib/auth';
import { mentorSchema, MentorFormData } from './schema';
import BasicInfoSection from './BasicInfoSection';
import ProfileSection from './ProfileSection';
import SkillsSection from './SkillsSection';
import MentorshipSection from './MentorshipSection';
import ImagesSection from './ImagesSection';
import NetworkLinksSection from './NetworkLinksSection';

const defaultValues: MentorFormData = {
  fullName: '',
  email: '',
  position: '',
  slackDisplayName: '',
  companyName: '',
  city: '',
  country: null,
  memberTypes: ['MENTOR'],
  profileStatus: '',
  bio: '',
  spokenLanguages: [],
  yearsExperience: '',
  technicalAreas: [],
  programmingLanguages: [],
  mentorshipFocus: [],
  mentorshipType: [],
  idealMentee: '',
  additionalInfo: '',
  images: [],
  network: [],
};

export default function CreateMentorForm() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<MentorFormData>({
    resolver: zodResolver(mentorSchema),
    defaultValues,
    mode: 'onBlur',
  });

  const transformFormData = (data: MentorFormData) => ({
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
    memberTypes: data.memberTypes,
    images: data.images,
    network: data.network,
    profileStatus: data.profileStatus,
    bio: data.bio,
    spokenLanguages: data.spokenLanguages,
    skills: {
      yearsExperience: Number(data.yearsExperience),
      areas: data.technicalAreas,
      languages: data.programmingLanguages,
      mentorshipFocus: data.mentorshipFocus,
    },
    menteeSection: {
      mentorshipType: data.mentorshipType,
      availability: [],
      idealMentee: data.idealMentee,
      additional: data.additionalInfo,
    },
  });

  const onSubmit = async (data: MentorFormData) => {
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

      await apiFetch('/api/platform/v1/mentors', {
        method: 'POST',
        body: transformFormData(data),
        token,
      });

      reset(defaultValues);
      setSuccessMessage('Mentor created successfully!');
    } catch (e: unknown) {
      setApiError(e instanceof Error ? e.message : 'Failed to create mentor');
    } finally {
      setLoading(false);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const handleCancel = () => router.push('/admin/mentors');

  return (
    <>
      {successMessage && (
        <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccessMessage(null)}>
          {successMessage}
        </Alert>
      )}
      {apiError && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setApiError(null)}>
          {apiError}
        </Alert>
      )}
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Grid container spacing={3}>
          <BasicInfoSection control={control} errors={errors} />
          <ProfileSection control={control} errors={errors} />
          <SkillsSection control={control} errors={errors} />
          <MentorshipSection control={control} errors={errors} />
          <ImagesSection control={control} />
          <NetworkLinksSection control={control} />
          <Grid size={12}>
            <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
              <Button onClick={handleCancel} disabled={loading} size="large">
                Cancel
              </Button>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                disabled={loading}
                startIcon={loading ? <CircularProgress size={20} /> : null}
                size="large"
              >
                {loading ? 'Creating...' : 'Create Mentor'}
              </Button>
            </Box>
          </Grid>
        </Grid>
      </form>
    </>
  );
}
