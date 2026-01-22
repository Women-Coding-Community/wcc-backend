import { useState } from 'react';
import {
  Paper,
  Typography,
  Button,
  TextField,
  Box,
  Autocomplete,
  Chip,
  IconButton,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Alert,
  CircularProgress,
  Breadcrumbs,
  Link as MuiLink,
} from '@mui/material';
import Grid from '@mui/material/Grid2';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import Link from 'next/link';
import AdminLayout from '@/components/AdminLayout';
import { COUNTRIES } from '@/lib/countries';
import { SOCIAL_NETWORK_TYPES } from '@/lib/socialNetworks';
import { apiFetch } from '@/lib/api';
import { getStoredToken } from '@/lib/auth';
import { useRouter } from 'next/router';

const MEMBER_TYPES = [
  'Director',
  'Collaborator',
  'Evangelist',
  'Leader',
  'Member',
  'Partner',
  'Speaker',
  'Volunteer',
];

interface NetworkLink {
  type: string;
  link: string;
}

interface MemberImage {
  path: string;
  alt: string;
  type: string;
}

export default function CreateMemberPage() {
  const router = useRouter();
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    position: '',
    companyName: '',
    city: '',
    slackDisplayName: '',
    country: null as { countryCode: string; countryName: string } | null,
    memberTypes: [] as string[],
    images: [] as MemberImage[],
    network: [] as NetworkLink[],
  });

  const [errors, setErrors] = useState<Record<string, string>>({
    fullName: '',
    email: '',
    position: '',
    slackDisplayName: '',
    country: '',
    memberTypes: '',
  });

  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleFieldChange = <K extends keyof typeof formData>(
    field: K,
    value: (typeof formData)[K]
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field as keyof typeof errors]) {
      setErrors((prev) => ({ ...prev, [field]: '' }));
    }
  };

  const handleArrayItemChange = (
    arrayField: 'network' | 'images',
    index: number,
    itemField: string,
    value: string
  ) => {
    setFormData((prev) => ({
      ...prev,
      [arrayField]: prev[arrayField].map((item, i) =>
        i === index ? { ...item, [itemField]: value } : item
      ),
    }));
  };

  const handleArrayAdd = (
    arrayField: 'network' | 'images',
    defaultItem: { path?: string; alt?: string; type: string; link?: string }
  ) => {
    setFormData((prev) => ({
      ...prev,
      [arrayField]: [...prev[arrayField], defaultItem],
    }));
  };

  const handleArrayRemove = (arrayField: 'network' | 'images', index: number) => {
    setFormData((prev) => ({
      ...prev,
      [arrayField]: prev[arrayField].filter((_, i) => i !== index),
    }));
  };

  const validateEmail = (email: string) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!formData.fullName) errors.fullName = 'Full name is required';
    if (!formData.position) errors.position = 'Position is required';
    if (!formData.email) errors.email = 'Email is required';
    else if (!validateEmail(formData.email)) errors.email = 'Invalid email format';
    if (!formData.slackDisplayName) errors.slackDisplayName = 'Slack display name is required';
    if (!formData.country?.countryCode) errors.country = 'Country is required';
    if (!formData.memberTypes || formData.memberTypes.length === 0) {
      errors.memberTypes = 'At least one member type is required';
    }

    return errors;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const validationErrors = validateForm();
    setErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      return;
    }

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

      const { country, ...restFormData } = formData;

      const submitData = {
        ...restFormData,
        country: {
          countryCode: country?.countryCode,
          countryName: country?.countryName,
        },
      };

      await apiFetch('/api/platform/v1/members', {
        method: 'POST',
        body: submitData,
        token,
      });

      setFormData({
        fullName: '',
        email: '',
        position: '',
        companyName: '',
        city: '',
        slackDisplayName: '',
        country: null,
        memberTypes: [],
        images: [],
        network: [],
      });

      setApiError(null);
      setSuccessMessage('Member created successfully!');
    } catch (e) {
      setSuccessMessage(null);
      const errorMessage = e instanceof Error ? e.message : 'Failed to create member';
      setApiError(errorMessage);
    } finally {
      setLoading(false);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const handleCancel = () => router.push('/admin/members');

  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} sx={{ mb: 2 }}>
          <Link href="/admin" passHref legacyBehavior>
            <MuiLink underline="hover" color="inherit">
              Admin
            </MuiLink>
          </Link>
          <Link href="/admin/members" passHref legacyBehavior>
            <MuiLink underline="hover" color="inherit">
              Members
            </MuiLink>
          </Link>
          <Typography color="text.primary">Create</Typography>
        </Breadcrumbs>
        <Typography variant="h5" sx={{ mb: 3 }}>
          Create New Member
        </Typography>

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

        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid size={12}>
              <TextField
                fullWidth
                required
                label="Full Name"
                value={formData.fullName}
                onChange={(e) => handleFieldChange('fullName', e.target.value)}
                error={!!errors.fullName}
                helperText={errors.fullName}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                required
                label="Email"
                type="email"
                value={formData.email}
                onChange={(e) => handleFieldChange('email', e.target.value)}
                error={!!errors.email}
                helperText={errors.email}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                required
                label="Slack Display Name"
                value={formData.slackDisplayName}
                onChange={(e) => handleFieldChange('slackDisplayName', e.target.value)}
                error={!!errors.slackDisplayName}
                helperText={errors.slackDisplayName}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                required
                label="Position"
                value={formData.position}
                onChange={(e) => handleFieldChange('position', e.target.value)}
                error={!!errors.position}
                helperText={errors.position}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="Company Name"
                value={formData.companyName}
                onChange={(e) => handleFieldChange('companyName', e.target.value)}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <TextField
                fullWidth
                label="City"
                value={formData.city}
                onChange={(e) => handleFieldChange('city', e.target.value)}
              />
            </Grid>

            <Grid size={{ xs: 12, sm: 6 }}>
              <Autocomplete
                options={COUNTRIES}
                value={formData.country}
                onChange={(_, newValue) => handleFieldChange('country', newValue)}
                getOptionLabel={(option) => `${option.countryName} (${option.countryCode})`}
                isOptionEqualToValue={(option, value) => option.countryCode === value.countryCode}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Country *"
                    placeholder="Search country"
                    error={!!errors.country}
                    helperText={errors.country}
                  />
                )}
              />
            </Grid>

            <Grid size={12}>
              <Autocomplete
                multiple
                options={MEMBER_TYPES}
                value={formData.memberTypes}
                onChange={(_, newValue) => handleFieldChange('memberTypes', newValue)}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Member Types *"
                    placeholder="Select member types"
                    error={!!errors.memberTypes}
                    helperText={errors.memberTypes}
                  />
                )}
                renderTags={(value, getTagProps) =>
                  value.map((option, index) => (
                    <Chip
                      label={option}
                      {...getTagProps({ index })}
                      key={option}
                      color="secondary"
                    />
                  ))
                }
              />
            </Grid>

            <Grid size={12}>
              <Box
                sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  mb: 2,
                }}
              >
                <Typography variant="h6">Member Images</Typography>
                <Button
                  size="small"
                  startIcon={<AddIcon />}
                  onClick={() =>
                    handleArrayAdd('images', {
                      path: '',
                      alt: '',
                      type: 'Desktop',
                    })
                  }
                >
                  Add Image
                </Button>
              </Box>
              {formData.images.map((image, index) => (
                <Box key={index} sx={{ display: 'flex', gap: 1, mb: 2 }}>
                  <FormControl sx={{ minWidth: 120 }}>
                    <InputLabel>Type</InputLabel>
                    <Select
                      value={image.type}
                      label="Type"
                      onChange={(e) =>
                        handleArrayItemChange('images', index, 'type', e.target.value)
                      }
                    >
                      <MenuItem value="Desktop">Desktop</MenuItem>
                      <MenuItem value="Mobile">Mobile</MenuItem>
                    </Select>
                  </FormControl>
                  <TextField
                    label="Image URL"
                    value={image.path}
                    onChange={(e) => handleArrayItemChange('images', index, 'path', e.target.value)}
                    placeholder="https://..."
                    sx={{ flex: 2 }}
                  />
                  <TextField
                    label="Alt Text"
                    value={image.alt}
                    onChange={(e) => handleArrayItemChange('images', index, 'alt', e.target.value)}
                    placeholder="Description"
                    sx={{ flex: 1 }}
                  />
                  <IconButton
                    color="error"
                    onClick={() => handleArrayRemove('images', index)}
                    sx={{ alignSelf: 'center' }}
                  >
                    <DeleteIcon />
                  </IconButton>
                </Box>
              ))}
            </Grid>

            <Grid size={12}>
              <Box
                sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  mb: 2,
                }}
              >
                <Typography variant="h6">Social Network Links</Typography>
                <Button
                  size="small"
                  startIcon={<AddIcon />}
                  onClick={() =>
                    handleArrayAdd('network', {
                      type: SOCIAL_NETWORK_TYPES[0].id,
                      link: '',
                    })
                  }
                >
                  Add Link
                </Button>
              </Box>
              {formData.network.map((networkLink, index) => (
                <Box key={index} sx={{ display: 'flex', gap: 1, mb: 2 }}>
                  <FormControl sx={{ minWidth: 150 }}>
                    <InputLabel>Type</InputLabel>
                    <Select
                      value={networkLink.type}
                      label="Type"
                      onChange={(e) =>
                        handleArrayItemChange('network', index, 'type', e.target.value)
                      }
                    >
                      {SOCIAL_NETWORK_TYPES.map((type) => (
                        <MenuItem key={type.id} value={type.id}>
                          {type.name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                  <TextField
                    fullWidth
                    label="URL"
                    value={networkLink.link}
                    onChange={(e) =>
                      handleArrayItemChange('network', index, 'link', e.target.value)
                    }
                    placeholder="https://..."
                  />
                  <IconButton
                    color="error"
                    onClick={() => handleArrayRemove('network', index)}
                    sx={{ alignSelf: 'center' }}
                  >
                    <DeleteIcon />
                  </IconButton>
                </Box>
              ))}
            </Grid>

            <Grid size={12}>
              <Box
                sx={{
                  display: 'flex',
                  gap: 2,
                  justifyContent: 'flex-end',
                  mt: 2,
                }}
              >
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
                  {loading ? 'Creating...' : 'Create Member'}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </AdminLayout>
  );
}
