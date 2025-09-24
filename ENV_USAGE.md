# 환경변수 사용법

## 개요

Vite 환경에서 사용하던 환경변수를 Expo WebView 환경에 맞게 수정했습니다.

## 변경사항

### 1. 환경변수 접두사 변경

- **이전**: `VITE_` 접두사
- **이후**: `EXPO_PUBLIC_` 접두사

### 2. 환경변수 파일

`.env` 파일에 다음과 같이 정의되어 있습니다:

```
EXPO_PUBLIC_BASE_URL=https://ingle-server.inuappcenter.kr
EXPO_PUBLIC_PORTAL_URL=https://ingle-server.inuappcenter.kr/api/v1/auth/login
EXPO_PUBLIC_NICK_CHECK_URL=https://ingle-server.inuappcenter.kr/api/v1/auth/nickname
EXPO_PUBLIC_SIGN_UP_URL=https://ingle-server.inuappcenter.kr/api/v1/auth/signup
```

### 3. WebView에서 환경변수 사용

WebView 내부의 JavaScript 코드에서 다음과 같이 사용할 수 있습니다:

```javascript
// WebView 내부에서 환경변수 접근
console.log(window.env.BASE_URL);
console.log(window.env.PORTAL_URL);
console.log(window.env.NICK_CHECK_URL);
console.log(window.env.SIGN_UP_URL);
```

### 4. 타입 안전성

TypeScript를 사용하는 경우, `window.env` 객체에 대한 타입 정의가 `types/global.d.ts`에 포함되어 있습니다.

## 파일 구조

- `.env`: 환경변수 정의
- `constants/Env.ts`: 환경변수 상수 및 타입 정의
- `types/global.d.ts`: 전역 타입 정의
- `app/_layout.tsx`: WebView에 환경변수 주입

## 주의사항

1. Expo에서는 `EXPO_PUBLIC_` 접두사가 붙은 환경변수만 클라이언트에서 접근 가능합니다.
2. WebView에 주입된 환경변수는 `window.env` 객체를 통해 접근할 수 있습니다.
3. 환경변수는 빌드 시점에 주입되므로, 런타임에 변경할 수 없습니다.

