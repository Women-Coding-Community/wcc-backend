import { useEffect, useState } from "react";
import {
  Alert,
  Avatar,
  Box,
  Button,
  Chip,
  Link,
  Paper,
  Stack,
  Typography,
} from "@mui/material";
import AdminLayout from "@/components/AdminLayout";
import { apiFetch } from "@/lib/api";
import { getStoredToken, isTokenExpired } from "@/lib/auth";
import { useRouter } from "next/router";

interface MemberCountry {
  countryCode?: string;
  countryName?: string;
}

interface MemberNetwork {
  type: string;
  link: string;
}

interface MemberItem {
  id: number | string;
  fullName: string;
  position?: string;
  email?: string;
  slackDisplayName?: string;
  country?: MemberCountry;
  city?: string;
  companyName?: string;
  memberTypes?: string[];
  images?: any[];
  network?: MemberNetwork[];
}

type MembersResponse =
  | MemberItem[]
  | {
      items?: MemberItem[];
      content?: MemberItem[];
      data?: MemberItem[];
    };

const MEMBERS_PATH = "/api/platform/v1/members";

export default function MembersPage() {
  const router = useRouter();
  const [items, setItems] = useState<MemberItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = getStoredToken();
    if (!token || isTokenExpired(token)) router.replace("/login");
    else {
      load(token);
    }
  }, [router]);

  const normalize = (resp: MembersResponse): MemberItem[] => {
    if (Array.isArray(resp)) return resp;
    if (resp?.items && Array.isArray(resp.items)) return resp.items;
    if (resp?.content && Array.isArray(resp.content)) return resp.content;
    if (resp?.data && Array.isArray(resp.data)) return resp.data;
    return [];
  };

  const load = async (t: string) => {
    try {
      const data = await apiFetch<MembersResponse>(MEMBERS_PATH, { token: t });
      setItems(normalize(data));
    } catch (e: any) {
      setError(e.message);
    }
  };

  const prettyLocation = (m: MemberItem) => {
    const parts = [
      m.city,
      m.country?.countryName || m.country?.countryCode,
    ].filter(Boolean);
    return parts.join(", ");
  };

  const getLinkedIn = (m: MemberItem) =>
    m.network?.find((n) => n.type?.toLowerCase() === "linkedin")?.link;

  const handleCreateMember = () => {
    router.push('/admin/members/create');
  };

  return (
    <AdminLayout>
      <Paper sx={{ p: 3 }}>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            mb: 2,
          }}
        >
          <Typography variant="h5">Members</Typography>
          <Button
            variant="contained"
            color="primary"
            onClick={handleCreateMember}
          >
            Create New Member
          </Button>
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box>
          {items.map((m) => (
            <Paper key={m.id} sx={{ p: 2, mb: 2 }}>
              <Stack
                direction={{ xs: "column", sm: "row" }}
                spacing={2}
                alignItems="flex-start"
              >
                <Avatar sx={{ bgcolor: "primary.main", width: 50, height: 50 }}>
                  {(m.fullName || "?").substring(0, 1)}
                </Avatar>
                <Box sx={{ flex: 1 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                    {m.id} - {m.fullName}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {[m.position, m.companyName].filter(Boolean).join(" @ ")}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {prettyLocation(m)}
                  </Typography>

                  {/* Email & Slack */}
                  <Stack
                    direction={{ xs: "column", sm: "row" }}
                    spacing={2}
                    sx={{ mt: 1 }}
                  >
                    {m.email && (
                      <Typography variant="body2">Email: {m.email}</Typography>
                    )}
                    {m.slackDisplayName && (
                      <Typography variant="body2">
                        Slack: {m.slackDisplayName}
                      </Typography>
                    )}
                  </Stack>

                  {/* Member Types */}
                  {m.memberTypes && m.memberTypes.length > 0 && (
                    <Stack
                      direction="row"
                      spacing={1}
                      flexWrap="wrap"
                      sx={{ mt: 1 }}
                    >
                      {m.memberTypes.map((t) => (
                        <Chip
                          key={`type-${m.id}-${t}`}
                          label={t}
                          size="small"
                          color="secondary"
                        />
                      ))}
                    </Stack>
                  )}

                  {/* Network */}
                  {getLinkedIn(m) && (
                    <Typography variant="body2" sx={{ mt: 1 }}>
                      <Link
                        href={getLinkedIn(m)!}
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        LinkedIn
                      </Link>
                    </Typography>
                  )}
                </Box>
              </Stack>
            </Paper>
          ))}
          {items.length === 0 && !error && (
            <Typography color="text.secondary">No members found.</Typography>
          )}
        </Box>
      </Paper>
    </AdminLayout>
  );
}
