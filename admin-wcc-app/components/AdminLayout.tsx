import React from 'react';
import { AppBar, Button, Container, Stack, Toolbar, Typography } from '@mui/material';
import Link from 'next/link';
import { useAuth } from '@/components/AuthProvider';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const { logout, roles } = useAuth();

  const isAdmin = roles.includes('ADMIN');
  const isMentorshipAdmin = roles.includes('MENTORSHIP_ADMIN');
  const isLeader = roles.includes('LEADER');
  const isMentor = roles.includes('MENTOR');

  return (
    <>
      <AppBar position="static" color="primary">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            WCC Admin
          </Typography>
          <Stack direction="row" spacing={2}>
            <Button component={Link} href="/admin" color="inherit">
              Dashboard
            </Button>
            {(isAdmin || isMentor) && (
              <Button component={Link} href="/admin/mentor" color="inherit">
                Mentor Dashboard
              </Button>
            )}
            {(isAdmin || isMentorshipAdmin || isLeader) && (
              <Button component={Link} href="/admin/mentors" color="inherit">
                Mentors
              </Button>
            )}
            {(isAdmin || isMentorshipAdmin || isLeader) && (
              <Button component={Link} href="/admin/members" color="inherit">
                Members
              </Button>
            )}
            {(isAdmin || isMentorshipAdmin) && (
              <Button component={Link} href="/admin/mentees" color="inherit">
                Mentees
              </Button>
            )}
            {(isAdmin || isLeader) && (
              <Button component={Link} href="/admin/users" color="inherit">
                Users
              </Button>
            )}
            <Button onClick={logout} color="inherit">
              Logout
            </Button>
          </Stack>
        </Toolbar>
      </AppBar>
      <Container sx={{ my: 4 }}>{children}</Container>
    </>
  );
}
