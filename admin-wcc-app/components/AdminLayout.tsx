import React from 'react';
import { AppBar, Toolbar, Typography, Button, Container, Stack } from '@mui/material';
import Link from 'next/link';
import { useAuth } from '@/components/AuthProvider';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const { logout } = useAuth();
  return (
    <>
      <AppBar position="static" color="primary">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>WCC Admin</Typography>
          <Stack direction="row" spacing={2}>
            <Button component={Link} href="/admin" color="inherit">Dashboard</Button>
            <Button component={Link} href="/admin/users" color="inherit">Users</Button>
            <Button component={Link} href="/admin/mentors" color="inherit">Mentors</Button>
            <Button component={Link} href="/admin/members" color="inherit">Members</Button>
            <Button onClick={logout} color="inherit">Logout</Button>
          </Stack>
        </Toolbar>
      </AppBar>
      <Container sx={{ my: 4 }}>{children}</Container>
    </>
  );
}
