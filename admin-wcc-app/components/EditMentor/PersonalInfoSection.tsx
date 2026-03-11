import { Autocomplete, Chip, Paper, TextField, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { Controller } from 'react-hook-form';
import { COUNTRIES } from '@/lib/countries';
import { SPOKEN_LANGUAGES } from '@/lib/languages';
import { FormSectionProps } from './types';

export default function PersonalInfoSection({ control, errors }: FormSectionProps) {
  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Personal Information
      </Typography>
      <Grid container spacing={3}>
        <Grid size={{ xs: 12, sm: 6 }}>
          <Controller
            name="fullName"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                required
                label="Full Name"
                error={!!errors.fullName}
                helperText={errors.fullName?.message}
              />
            )}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6 }}>
          <Controller
            name="email"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                required
                label="Email"
                type="email"
                error={!!errors.email}
                helperText={errors.email?.message}
              />
            )}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6 }}>
          <Controller
            name="position"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                required
                label="Position"
                error={!!errors.position}
                helperText={errors.position?.message}
              />
            )}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6 }}>
          <Controller
            name="companyName"
            control={control}
            render={({ field }) => <TextField {...field} fullWidth label="Company Name" />}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6 }}>
          <Controller
            name="country"
            control={control}
            render={({ field: { onChange, value } }) => (
              <Autocomplete
                options={COUNTRIES}
                value={value}
                onChange={(_, newValue) => onChange(newValue)}
                getOptionLabel={(option) => `${option.countryName} (${option.countryCode})`}
                isOptionEqualToValue={(option, val) => option.countryCode === val.countryCode}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    required
                    label="Country"
                    placeholder="Search country"
                    error={!!errors.country}
                    helperText={errors.country?.message}
                  />
                )}
              />
            )}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6 }}>
          <Controller
            name="city"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                required
                label="City"
                error={!!errors.city}
                helperText={errors.city?.message}
              />
            )}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6 }}>
          <Controller
            name="spokenLanguages"
            control={control}
            render={({ field }) => (
              <Autocomplete
                multiple
                options={SPOKEN_LANGUAGES}
                value={field.value}
                onChange={(_, newValue) => field.onChange(newValue)}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    required
                    label="Spoken Languages"
                    placeholder="Select languages"
                    error={!!errors.spokenLanguages}
                    helperText={errors.spokenLanguages?.message}
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
            )}
          />
        </Grid>

        <Grid size={{ xs: 12, sm: 6 }}>
          <Controller
            name="slackDisplayName"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                fullWidth
                required
                label="Slack Display Name"
                error={!!errors.slackDisplayName}
                helperText={errors.slackDisplayName?.message}
              />
            )}
          />
        </Grid>
      </Grid>
    </Paper>
  );
}
