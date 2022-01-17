import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Platform, View } from 'react-native';
import { requireNativeComponent } from 'react-native';
import { LiveLikeAndroidView } from './LiveLikeAndroidView';
const LiveLikeiOSView = requireNativeComponent("LiveLikeiOSView")

export default function App() {
  const isIOS = Platform.OS == 'ios';
  return (
    <View style={styles.container}>
     {isIOS ? (
        <LiveLikeiOSView style={styles.wrapper}></LiveLikeiOSView>
      ) : (
        <LiveLikeAndroidView style={styles.wrapper}></LiveLikeAndroidView>
      )}
      <StatusBar style="auto" />
    </View>
  );
}


const styles = StyleSheet.create({
  container: {
    flex: 1, alignItems: "stretch"
  },
  wrapper: {
    flex: 1, alignItems: "center", justifyContent: "center"
  },
  border: {
    borderColor: "#eee", borderBottomWidth: 1
  },
  button: {
    fontSize: 50, color: "orange"
  }
});
