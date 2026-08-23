const AVATAR_COLOR_CLASSES = [
  'bg-app-primary',
  'bg-app-accent',
  'bg-app-success',
  'bg-app-danger',
  'bg-app-warning',
  'bg-app-primaryDark',
  'bg-app-accentDark',
];

export const getTeamInitials = (name: string): string => {
  const words = name.trim().split(/\s+/).filter(Boolean);
  if (words.length >= 2) {
    return (words[0][0] + words[1][0]).toUpperCase();
  }
  return (words[0] ?? '').slice(0, 2).toUpperCase();
};

export const getTeamAvatarColorClass = (id: string): string => {
  let hash = 0;
  for (let i = 0; i < id.length; i++) {
    hash = (hash * 31 + id.charCodeAt(i)) | 0;
  }
  return AVATAR_COLOR_CLASSES[Math.abs(hash) % AVATAR_COLOR_CLASSES.length];
};
