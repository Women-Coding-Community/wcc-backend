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
import { MentorFormData } from './schema';

interface ImagesSectionProps {
  control: Control<MentorFormData>;
}

export default function ImagesSection({ control }: ImagesSectionProps) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: 'images',
  });

  return (
    <Grid size={12}>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          mb: 2,
          mt: 3,
        }}
      >
        <Typography variant="h6">Mentor Images</Typography>
        <Button
          size="small"
          startIcon={<AddIcon />}
          onClick={() => append({ path: '', alt: '', type: 'Desktop' })}
        >
          Add Image
        </Button>
      </Box>
      {fields.map((field, index) => (
        <Box key={field.id} sx={{ display: 'flex', gap: 1, mb: 2 }}>
          <Controller
            name={`images.${index}.type`}
            control={control}
            render={({ field }) => (
              <FormControl sx={{ minWidth: 120 }}>
                <InputLabel>Type</InputLabel>
                <Select {...field} label="Type">
                  <MenuItem value="Desktop">Desktop</MenuItem>
                  <MenuItem value="Mobile">Mobile</MenuItem>
                </Select>
              </FormControl>
            )}
          />
          <Controller
            name={`images.${index}.path`}
            control={control}
            render={({ field }) => (
              <TextField {...field} label="Image URL" placeholder="https://..." sx={{ flex: 2 }} />
            )}
          />
          <Controller
            name={`images.${index}.alt`}
            control={control}
            render={({ field }) => (
              <TextField {...field} label="Alt Text" placeholder="Description" sx={{ flex: 1 }} />
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
