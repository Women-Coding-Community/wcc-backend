import {
  TextField,
  Chip,
  Autocomplete,
  Paper,
  Typography,
  Box,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  IconButton,
  Button,
} from '@mui/material';
import Grid from '@mui/material/Grid2';
import { Controller, useFieldArray, useWatch } from 'react-hook-form';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { TECHNICAL_AREAS } from '@/lib/technicalAreas';
import { PROGRAMMING_LANGUAGES } from '@/lib/programmingLanguages';
import { MENTORSHIP_FOCUS_AREAS } from '@/lib/mentorshipFocusAreas';
import { PROFICIENCY_LEVELS } from '@/lib/proficiencyLevels';
import { FormSectionProps } from './types';

export default function SkillsSection({ control, errors }: FormSectionProps) {
  const {
    fields: areaFields,
    append: appendArea,
    remove: removeArea,
  } = useFieldArray({
    control,
    name: 'technicalAreas',
  });

  const {
    fields: langFields,
    append: appendLang,
    remove: removeLang,
  } = useFieldArray({
    control,
    name: 'programmingLanguages',
  });

  const watchedAreas = useWatch({ control, name: 'technicalAreas' }) ?? [];
  const watchedLangs = useWatch({ control, name: 'programmingLanguages' }) ?? [];

  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Skills and Experience
      </Typography>
      <Grid container spacing={3}>
        <Grid size={12}>
          <Controller
            name="yearsExperience"
            control={control}
            render={({ field: { onChange, value, ...field } }) => (
              <TextField
                {...field}
                value={value}
                onChange={(e) => onChange(e.target.value === '' ? '' : Number(e.target.value))}
                fullWidth
                required
                type="number"
                label="Years of Experience"
                error={!!errors.yearsExperience}
                helperText={errors.yearsExperience?.message}
                slotProps={{ htmlInput: { min: 0, max: 50 } }}
              />
            )}
          />
        </Grid>

        <Grid size={12}>
          <Typography variant="subtitle2" sx={{ mb: 1 }}>
            Technical Areas *
          </Typography>
          {areaFields.map((field, index) => {
            const selectedAreas = watchedAreas
              .filter((_, i) => i !== index)
              .map((a) => a.technicalArea);

            return (
              <Box key={field.id} sx={{ display: 'flex', gap: 2, mb: 1 }}>
                <Controller
                  name={`technicalAreas.${index}.technicalArea`}
                  control={control}
                  render={({ field: areaField }) => (
                    <FormControl size="small" sx={{ flex: 2 }}>
                      <InputLabel>Area</InputLabel>
                      <Select {...areaField} label="Area">
                        {TECHNICAL_AREAS.filter(
                          (a) => !selectedAreas.includes(a.value) || a.value === areaField.value
                        ).map((a) => (
                          <MenuItem key={a.value} value={a.value}>
                            {a.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  )}
                />
                <Controller
                  name={`technicalAreas.${index}.proficiencyLevel`}
                  control={control}
                  render={({ field: profField }) => (
                    <FormControl size="small" sx={{ flex: 1 }}>
                      <InputLabel>Proficiency</InputLabel>
                      <Select {...profField} label="Proficiency">
                        {PROFICIENCY_LEVELS.map((l) => (
                          <MenuItem key={l.value} value={l.value}>
                            {l.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  )}
                />
                <IconButton
                  color="error"
                  onClick={() => removeArea(index)}
                  sx={{ alignSelf: 'center' }}
                >
                  <DeleteIcon />
                </IconButton>
              </Box>
            );
          })}
          {errors.technicalAreas?.message && (
            <Typography variant="caption" color="error" sx={{ display: 'block', mb: 1 }}>
              {errors.technicalAreas.message}
            </Typography>
          )}
          <Button
            size="small"
            startIcon={<AddIcon />}
            onClick={() => appendArea({ technicalArea: '', proficiencyLevel: 'INTERMEDIATE' })}
            variant="outlined"
            sx={{ mt: 1 }}
          >
            Add Area
          </Button>
        </Grid>

        <Grid size={12}>
          <Typography variant="subtitle2" sx={{ mb: 1 }}>
            Programming Languages *
          </Typography>
          {langFields.map((field, index) => {
            const selectedLangs = watchedLangs.filter((_, i) => i !== index).map((l) => l.language);

            return (
              <Box key={field.id} sx={{ display: 'flex', gap: 2, mb: 1 }}>
                <Controller
                  name={`programmingLanguages.${index}.language`}
                  control={control}
                  render={({ field: langField }) => (
                    <FormControl size="small" sx={{ flex: 2 }}>
                      <InputLabel>Language</InputLabel>
                      <Select {...langField} label="Language">
                        {PROGRAMMING_LANGUAGES.filter(
                          (l) => !selectedLangs.includes(l.value) || l.value === langField.value
                        ).map((l) => (
                          <MenuItem key={l.value} value={l.value}>
                            {l.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  )}
                />
                <Controller
                  name={`programmingLanguages.${index}.proficiencyLevel`}
                  control={control}
                  render={({ field: profField }) => (
                    <FormControl size="small" sx={{ flex: 1 }}>
                      <InputLabel>Proficiency</InputLabel>
                      <Select {...profField} label="Proficiency">
                        {PROFICIENCY_LEVELS.map((l) => (
                          <MenuItem key={l.value} value={l.value}>
                            {l.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  )}
                />
                <IconButton
                  color="error"
                  onClick={() => removeLang(index)}
                  sx={{ alignSelf: 'center' }}
                >
                  <DeleteIcon />
                </IconButton>
              </Box>
            );
          })}
          {errors.programmingLanguages?.message && (
            <Typography variant="caption" color="error" sx={{ display: 'block', mb: 1 }}>
              {errors.programmingLanguages.message}
            </Typography>
          )}
          <Button
            size="small"
            startIcon={<AddIcon />}
            onClick={() => appendLang({ language: '', proficiencyLevel: 'INTERMEDIATE' })}
            variant="outlined"
            sx={{ mt: 1 }}
          >
            Add Language
          </Button>
        </Grid>

        <Grid size={12}>
          <Controller
            name="mentorshipFocus"
            control={control}
            render={({ field }) => (
              <Autocomplete
                multiple
                options={MENTORSHIP_FOCUS_AREAS}
                getOptionLabel={(option) =>
                  typeof option === 'string'
                    ? MENTORSHIP_FOCUS_AREAS.find((f) => f.value === option)?.label || option
                    : option.label
                }
                value={MENTORSHIP_FOCUS_AREAS.filter((f) => field.value.includes(f.value))}
                onChange={(_, newValue) =>
                  field.onChange(newValue.map((v) => (typeof v === 'string' ? v : v.value)))
                }
                isOptionEqualToValue={(option, value) =>
                  option.value === (typeof value === 'string' ? value : value.value)
                }
                renderInput={(params) => (
                  <TextField
                    {...params}
                    required
                    label="Mentorship Focus"
                    placeholder="Select focus areas"
                    error={!!errors.mentorshipFocus}
                    helperText={errors.mentorshipFocus?.message}
                  />
                )}
                renderTags={(value, getTagProps) =>
                  value.map((option, index) => (
                    <Chip
                      label={option.label}
                      {...getTagProps({ index })}
                      key={option.value}
                      color="secondary"
                    />
                  ))
                }
              />
            )}
          />
        </Grid>
      </Grid>
    </Paper>
  );
}
