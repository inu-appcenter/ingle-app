/**
 * 환경변수 타입 정의
 * Expo에서는 EXPO_PUBLIC_ 접두사가 붙은 환경변수만 클라이언트에서 접근 가능
 */
export const Env = {
  FRONT_URL: process.env.EXPO_PUBLIC_FRONTEND_URL || "http://192.168.45.249:5173/",
} as const;

export type EnvType = typeof Env;
