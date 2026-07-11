#!/usr/bin/env bash
#
# scripts/release.sh
#
# Uruchamiane LOKALNIE przez dewelopera zamiast ręcznego:
#   git tag vX.Y.Z && git push origin vX.Y.Z
#
# Użycie:
#   scripts/release.sh            # auto-bump patcha z ostatniego tagu (v0.1.5 -> v0.1.6)
#   scripts/release.sh v0.2.0     # jawna wersja
#
# Wypchnięcie tagu vX.Y.Z uruchamia .github/workflows/publish-images.yml:
# testy -> build i publikacja 8 obrazów do GHCR -> automatyczny deploy na VPS.

set -euo pipefail

REPO_SLUG="Dariusz95/PoorBetApplication"
REMOTE="origin"
MAIN_BRANCH="main"

# --- 1. Czyste drzewo robocze ------------------------------------------------
if [[ -n "$(git status --porcelain)" ]]; then
  echo "BŁĄD: masz niezacommitowane zmiany. Zacommituj albo 'git stash' przed release." >&2
  git status --short >&2
  exit 1
fi

# --- 2. Właściwa gałąź --------------------------------------------------------
current_branch="$(git rev-parse --abbrev-ref HEAD)"
if [[ "$current_branch" != "$MAIN_BRANCH" ]]; then
  echo "BŁĄD: jesteś na '$current_branch'. Release można robić tylko z '$MAIN_BRANCH'." >&2
  exit 1
fi

# --- 3. Aktualność względem origin/main --------------------------------------
echo "==> git fetch $REMOTE $MAIN_BRANCH"
git fetch "$REMOTE" "$MAIN_BRANCH"

local_sha="$(git rev-parse HEAD)"
remote_sha="$(git rev-parse "$REMOTE/$MAIN_BRANCH")"
if [[ "$local_sha" != "$remote_sha" ]]; then
  echo "BŁĄD: lokalny '$MAIN_BRANCH' ($local_sha) różni się od '$REMOTE/$MAIN_BRANCH' ($remote_sha)." >&2
  echo "       Zrób 'git pull'/'git push' i spróbuj ponownie." >&2
  exit 1
fi

# --- 4. Wersja: argument albo auto-bump patcha z ostatniego tagu -------------
if [[ $# -ge 1 ]]; then
  version="$1"
  if [[ ! "$version" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "BŁĄD: wersja musi mieć format vX.Y.Z, podano: '$version'" >&2
    exit 1
  fi
else
  last_tag="$(git describe --tags --abbrev=0 --match 'v[0-9]*.[0-9]*.[0-9]*' 2>/dev/null || true)"
  if [[ -z "$last_tag" ]]; then
    echo "BŁĄD: brak istniejącego tagu vX.Y.Z do auto-bumpa. Podaj wersję jawnie: scripts/release.sh v0.1.0" >&2
    exit 1
  fi
  IFS='.' read -r major minor patch <<< "${last_tag#v}"
  version="v${major}.${minor}.$((patch + 1))"
  echo "==> Auto-bump: $last_tag -> $version"
fi

# --- 5. Tag nie może już istnieć ---------------------------------------------
if git rev-parse "$version" >/dev/null 2>&1; then
  echo "BŁĄD: tag '$version' już istnieje lokalnie." >&2
  exit 1
fi
if git ls-remote --tags "$REMOTE" "refs/tags/$version" | grep -q "$version"; then
  echo "BŁĄD: tag '$version' już istnieje na '$REMOTE'." >&2
  exit 1
fi

# --- 6. Potwierdzenie, tag adnotowany, push ----------------------------------
read -r -p "Utworzyć i wypchnąć tag '$version' z commita $(git rev-parse --short HEAD)? [y/N] " confirm
if [[ ! "$confirm" =~ ^[Yy]$ ]]; then
  echo "Anulowano."
  exit 1
fi

git tag -a "$version" -m "Release $version"
git push "$REMOTE" "$version"

echo
echo "==> Tag '$version' wypchnięty. Śledź build/publish/deploy tutaj:"
echo "    https://github.com/${REPO_SLUG}/actions"
