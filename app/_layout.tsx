import { useFonts } from 'expo-font';
import { StatusBar } from 'expo-status-bar';
import { useState } from 'react';
import { ActivityIndicator, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { WebView } from 'react-native-webview';

//safeareaview 왜 안됨..ㅠ
export default function RootLayout() {
  const [loaded] = useFonts({
    SpaceMono: require('../assets/fonts/SpaceMono-Regular.ttf'),
  });

  const [webviewReady, setWebviewReady] = useState(false);

  if (!loaded) {
    return null;
  }
  //SafeAreaView is used to ensure the content is rendered within the safe area boundaries of a device
  return (
    <SafeAreaView style={{ flex: 1 }}>
      <View>
        <WebView
          source={{ uri: 'http://192.168.45.249:5173/' }}
          onLoadEnd={() => setWebviewReady(true)}
          style={{ flex: 1 }}
          originWhitelist={['*']}
          javaScriptEnabled
          domStorageEnabled
          startInLoadingState
          renderLoading={() => (
            <ActivityIndicator size="large" color="#0000ff" style={{ flex: 1, justifyContent: 'center' }} />
          )}
        />
      </View>
      <StatusBar style="auto" />
    </SafeAreaView>
  );
}
