# Tauri
Initialize tauri project. This will desktop build.
```
npm install -D @tauri-apps/cli@latest
npx tauri init
```


# Android
```
rustup target add aarch64-linux-android
rustup target add armv7-linux-androideabi
rustup target add x86_64-linux-android
cargo install cargo-ndk



cargo tauri android init

# Install Android Studio
# Install platform tools, cli tools
# Create emulator in Android Studio
# Add Android Studio and platfrom-tools and cli-tools to PATH

# Terminal 1 
adb reverse tcp:9630 tcp:9630
adb reverse tcp:8000 tcp:8000
emulator -avd Tauri_Clasic

# Terminal 2
cargo tauri android dev
```

# IOS
```
rustup target add aarch64-apple-ios x86_64-apple-ios
sudo xcodebuild -license accept

cargo install cargo-tauri

cargo tauri ios init
cargo tauri ios dev
```
