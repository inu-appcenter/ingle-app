import { useFonts } from "expo-font";
import { StatusBar } from "expo-status-bar";
import { ActivityIndicator } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { WebView } from "react-native-webview";
import { Env } from "../constants/Env";

//safeareaview 왜 안됨..ㅠ
export default function RootLayout() {
  const [loaded] = useFonts({
    SpaceMono: require("../assets/fonts/SpaceMono-Regular.ttf"),
  });

  // WebView에 환경변수를 주입하는 JavaScript 코드
  const injectedJavaScript = `
    window.env = {
      FRONT_URL: '${Env.FRONT_URL}',
    };
    true; // 이 값이 반환되어야 함
  `;

  if (!loaded) {
    return null;
  }
  //SafeAreaView is used to ensure the content is rendered within the safe area boundaries of a device
  return (
    <SafeAreaView style={{ flex: 1 }}>
      <WebView
        source={{ uri: Env.FRONT_URL }}
        onLoadEnd={() => {}}
        style={{ flex: 1 }}
        originWhitelist={["*"]}
        javaScriptEnabled
        domStorageEnabled
        startInLoadingState
        injectedJavaScript={injectedJavaScript}
        renderLoading={() => <ActivityIndicator size="large" color="#0000ff" style={{ flex: 1, justifyContent: "center" }} />}
      />
      <StatusBar style="auto" />
    </SafeAreaView>
  );
}
