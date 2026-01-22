import {
  TextField,
  Box,
  Button,
  IconButton,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  Typography,
} from '@mui/material';
import Grid from '@mui/material/Grid2';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { Controller, useFieldArray, Control } from 'react-hook-form';
import { SOCIAL_NETWORK_TYPES } from '@/lib/socialNetworks';
import { MentorFormData } from './schema';

interface NetworkLinksSectionProps {
  control: Control<MentorFormData>;
}

export default function NetworkLinksSection({ control }: NetworkLinksSectionProps) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: 'network',
  });

  return (
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
          onClick={() => append({ type: SOCIAL_NETWORK_TYPES[0].id, link: '' })}
        >
          Add Link
        </Button>
      </Box>
      {fields.map((field, index) => (
        <Box key={field.id} sx={{ display: 'flex', gap: 1, mb: 2 }}>
          <Controller
            name={`network.${index}.type`}
            control={control}
            render={({ field }) => (
              <FormControl sx={{ minWidth: 150 }}>
                <InputLabel>Type</InputLabel>
                <Select {...field} label="Type">
                  {SOCIAL_NETWORK_TYPES.map((type) => (
                    <MenuItem key={type.id} value={type.id}>
                      {type.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            )}
          />
          <Controller
            name={`network.${index}.link`}
            control={control}
            render={({ field }) => (
              <TextField {...field} fullWidth label="URL" placeholder="https://..." />
            )}
          />
          <IconButton color="error" onClick={() => remove(index)} sx={{ alignSelf: 'center' }}>
            <DeleteIcon />
          </IconButton>
        </Box>
      ))}
    </Grid>
  );
}
