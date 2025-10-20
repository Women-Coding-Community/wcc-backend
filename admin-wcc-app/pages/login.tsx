import {useState} from 'react';
import {Alert, Box, Button, Container, Paper, TextField, Typography} from '@mui/material';
import {useAuth} from '@/components/AuthProvider';

export default function LoginPage() {
  const {login} = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await login(email, password);
    } catch (err: any) {
      setError(err.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
      <Container maxWidth="sm" sx={{mt: 12}}>
        <Paper elevation={3} sx={{p: 4}}>
          <Typography variant="h4" color="primary" fontWeight={700} gutterBottom>
            Women Coding Community
          </Typography>
          <Typography variant="body1" color="text.secondary" gutterBottom>
            Please sign in to continue
          </Typography>
          {error && <Alert severity="error" sx={{mb: 2}}>{error}</Alert>}
          <Box component="form" onSubmit={handleSubmit}>
            <TextField
                label="Email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                fullWidth
                required
                margin="normal"
            />
            <TextField
                label="Password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                fullWidth
                required
                margin="normal"
            />
            <Button type="submit" variant="contained" color="primary" fullWidth disabled={loading}
                    sx={{mt: 2}}>
              {loading ? 'Signing in...' : 'Sign In'}
            </Button>
          </Box>
        </Paper>
      </Container>
  );
}
