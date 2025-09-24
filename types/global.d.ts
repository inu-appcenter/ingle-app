/**
 * 전역 타입 정의
 * WebView에서 주입된 환경변수에 대한 타입 정의
 */
declare global {
  interface Window {
    env: {
      BASE_URL: string;
      PORTAL_URL: string;
      NICK_CHECK_URL: string;
      SIGN_UP_URL: string;
    };
  }
}

export {};

