import axios from 'axios'

/**
 * Single axios instance for the whole app. Every feature's api.ts imports
 * this rather than creating its own client, so auth headers, base URL, and
 * refresh-token handling live in exactly one place (docs/API_DESIGN.md §1).
 */
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api/v1',
  withCredentials: true, // refresh token travels as an httpOnly cookie
})

let accessToken: string | null = null

export function setAccessToken(token: string | null) {
  accessToken = token
}

apiClient.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }
  return config
})

// Access-token refresh-on-401 and problem-detail error normalization are
// wired up here once the auth feature (Phase 3) has a refresh endpoint to
// call — left as the single seam for that, rather than duplicated per call site.
